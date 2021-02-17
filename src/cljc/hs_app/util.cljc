(ns hs-app.util)


;; female 0, male 1
(def gender-types
  "gender types: 0 - female, 1 - male"
  `(0 1))

(defn in?
  "true if coll contains elm"
  [coll elm]
  (some #(= elm %) coll))

(defn valid-gender?
  [gender]
  (in? gender-types gender))

(comment
  (in? gender-types 0)
  (in? gender-types 1)
  (in? gender-types 2))

