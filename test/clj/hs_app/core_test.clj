(ns hs-app.core-test
  (:require [clojure.test :refer [is deftest]]))


(deftest addition
  (is (= 4 (+ 2 2)))
  (is (= 7 (+ 5 2))))