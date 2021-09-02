(ns infix-test
  (:require [clojure.test :refer [is deftest]]
            [api.calculator :as calculator]))

(deftest calculator-tests
  (is (= (calculator/calculate "-1 * (2 * 6 / 3)")
         -4))
  (is (= (calculator/calculate "1 * 2 * (3 + 4) + 6 - 1")
         19))
  (is (= (calculator/calculate "1 * 2 * ((3 + 4) + 6) - 1")
         25)))
