(ns hs-app.states
  (:require [reagent.core :as r]))

(defonce patients-data (r/atom nil))

(defonce form-fields {:patient {:fullname nil :gender -1 :birth_date nil :address nil :policy_number nil}})

(defonce redirect-to-list? (r/atom nil))

(defonce message (r/atom nil))
