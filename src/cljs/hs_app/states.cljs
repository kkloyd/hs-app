(ns hs-app.states
  (:require [reagent.core :as r]))

(def patients-data (r/atom nil))

(def form-fields {:patient {:fullname nil :gender -1 :birth_date nil :address nil :policy_number nil}})

