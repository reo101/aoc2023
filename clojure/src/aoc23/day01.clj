(ns aoc23.day01
  (:require
   [clojure.string :as string]
   [typed.clojure :as t]))

;;;;;;;;;;;;;;
;;; Part 1 ;;;
;;;;;;;;;;;;;;

(t/ann input-1 t/Str)
(def input-1 (slurp "../data/01/1.in"))

(defn first-and-last-digits [regex-l->r regex-r->l s]
  (let [rev #(apply str (reverse %))]
   [(re-find (re-matcher regex-l->r s))
    (rev (re-find (re-matcher regex-r->l (rev s))))]))

(defn combine-digits [d1 d2]
  (Integer/parseInt (str d1 d2)))

(def output-1
  (reduce
   (fn [acc line]
     (+ acc
        (apply
         combine-digits
         (first-and-last-digits
          (re-pattern #"\d")
          (re-pattern #"\d")
          line))))
   0
   (string/split-lines
    input-1)))

output-1

;;;;;;;;;;;;;;
;;; Part 2 ;;;
;;;;;;;;;;;;;;

(def input-2 (slurp "../data/01/2.in"))

(def number-names
  {:zero  0
   :one   1
   :two   2
   :three 3
   :four  4
   :five  5
   :six   6
   :seven 7
   :eight 8
   :nine  9})

(def regex-l->r
  (re-pattern
   (str "(?:\\d|"
        (string/join
         "|"
         (map
          (fn [[k _]]
            (name k))
          number-names))
        ")")))

(def regex-r->l
  (re-pattern
   (str "(?:\\d|"
        (string/join
         "|"
         (map
          (fn [[k _]]
            (apply str (reverse (name k))))
          number-names))
        ")")))

(defn normalize-digit [d]
  (reduce
   (fn [acc [k v]]
     (string/replace acc (name k) (str v)))
   d
   number-names))

#_(first-and-last-digits regex-l->r regex-r->l "twone")

(def output-2
  (reduce
   (fn [acc line]
     (+ acc
        (apply
         combine-digits
         (map
          normalize-digit
          (first-and-last-digits
           regex-l->r
           regex-r->l
           line)))))
   0
   (string/split-lines
    input-2)))

output-2
