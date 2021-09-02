(ns api.calculator
  (:require [clojure.edn :as edn]))

(def operators  {"+" + "-" - "*" * "/" /})
(def precedence {+ 0 - 0 * 1 / 1})

(defn tokenize
  [s]
  (->> s
       (re-seq #"-?\d+(?:\.\d)?|\+|\-|\/|\*|\(|\)")
       (map (fn [t]
              (cond
                (= t "(") :l
                (= t ")") :r
                :else (operators t
                                 (edn/read-string t)))))))

(defn lower-precedence?
  "Returns a function checking whether the argument has a lower
  precedence than the given operator.
  Guards against a nil argument, returning always true."
  [than-op]
  (if than-op
    (fn [op] (< (precedence than-op)
                (precedence op)))
    identity))

(defn pop-higher-precedence
  "Take operators from stack with higher precedence than op."
  [op stack]
  (split-with (lower-precedence? op) stack))

(defn execute
  "Execute ops on first values on stack, adding the results to the stack
  as they are executed."
  [value-stack ops]
  (reduce (fn [[l r & rest-vals] op]
            (println r (last (butlast (pr-str op))) l "=" (op r l)) ; debug
            (cons (op r l) rest-vals))
          value-stack ops))

; Inspired by https://codereview.stackexchange.com/a/216583, with added support for parentheses
(defn infix-parse
  "Parse an infix expression and execute it"
  [expression]
  (loop [number-stack ()
         op-stack     ()
         [number operator & expr] expression]
    (cond
      ;; on a new parenthesis, execute its contents until it closes
      (= number :l)   (recur number-stack op-stack (infix-parse (cons operator expr)))
      ;; then add this to the start of the expression, and continue parsing
      (= operator :r) (into expr (execute (cons number number-stack) op-stack))
      ;; add number and op to their respective stacks
      ;; if we hit a lower precedence operator than is the op-stack, these operations can be executed
      :else (let [[ops rest-ops]  (pop-higher-precedence operator op-stack)
                  new-value-stack (execute (cons number number-stack) ops)
                  new-op-stack    (cons operator rest-ops)]
              (if operator
                (recur new-value-stack new-op-stack expr)
                (first new-value-stack))))))

(defn calculate
  [s]
  (println "calculating:" s)
  (-> s
      tokenize
      infix-parse))
