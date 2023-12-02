// Parser Combinators
import scala.util.parsing.combinator._

// ScalaZ
import scalaz._
import Scalaz._

// File IO
import scala.io.Source._

enum Cube:
  case Red, Green, Blue
import Cube.*

case class Handful(cubes: Map[Cube, Int])
case class Game(index: Int, picks: List[Handful])

class GameParser extends RegexParsers:
  val numberParser: Parser[Int] =
    """\d+""".r ^^ { _.toInt }

  val cubeParser: Parser[Cube] =
    """(red|green|blue)""".r ^^ { s => Cube.valueOf(s.capitalize) }

  val handfulParser: Parser[Handful] =
    def pickParser: Parser[(Cube, Int)] =
      for
        count <- numberParser
        cube <- cubeParser
      yield cube -> count
    rep1sep(pickParser, """, """.r) ^^ { l => Handful(l.toMap) }

  val gameParser: Parser[Game] =
    for
      _ <- """Game """.r
      index <- numberParser
      _ <- """: """.r
      picks <- rep1sep(handfulParser, """; """.r)
    yield Game(index, picks)

  val gamesParser: Parser[List[Game]] =
    rep(gameParser)
end GameParser

val gamesString = fromResource("02/2.in").getLines().mkString("\n")

def accumulateCubes(
    picks: List[Handful],
    acc: List[Int] => Int,
): Map[Cube, Int] =
  picks
    .foldMap(_.cubes.map((cube, count) => (cube, List(count))))
    .map((k, v) => (k, acc(v)))

object TestGameParser extends GameParser:
  def main(args: Array[String]) =
    val games = parse(gamesParser, gamesString) match {
      case Success(matched, _) => matched
      case Failure(msg, _)     => println(s"FAILURE: $msg"); ???
      case Error(msg, _)       => println(s"ERROR: $msg"); ???
    }

    def result1 = games.foldMap(game => {
      def maxPicks = accumulateCubes(game.picks, _.max)
      // println((game.index, maxPicks))
      given Monoid[Boolean] = Monoid.instance(_ || _, false)
      if List(Red -> 12, Green -> 13, Blue -> 14).foldMap((colour, limit) =>
          maxPicks.getOrElse(colour, 0) > limit,
        )
      then 0
      else game.index
    })

    given Monoid[Int] = Monoid.instance(_ + _, 0)
    val result2 = games.foldMap(game => {
      val minPicks = accumulateCubes(game.picks, _.max)
      // println((game.index, minPicks))
      given Monoid[Int] = Monoid.instance(_ * _, 1)
      List(Red, Green, Blue).foldMap(colour => minPicks.getOrElse(colour, 0))
    })

    println(result2)
