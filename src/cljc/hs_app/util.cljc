(ns hs-app.util)

(def gender-types
  "gender types: 0 - female, 1 - male"
  `(0 1))

(defn in?
  "true if collection contains elm"
  [collection elm]
  (some #(= elm %) collection))

(defn valid-gender?
  "check if value exists inside gender-types"
  [gender]
  (in? gender-types gender))

(defn gender-str [gender]
  (cond
    (= gender 0) "Ж"
    (= gender 1) "М"))

(comment
  (in? gender-types 0)
  (in? gender-types 1)
  (in? gender-types 2))

