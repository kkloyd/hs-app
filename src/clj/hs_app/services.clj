(ns hs-app.services
  (:require [hs-app.db :as db]))

(defn get-patients [_]
  {:status 200
   :body {:patients (db/get-all-patients)}})

(defn create-patient [{:keys [parameters]}]
  (let [data (:body parameters)
        patient (db/create-patient data)]
    {:status 201
     :body {:patient patient}}))

(defn edit-patient [{:keys [parameters]}]
  (let [id (:id (:path parameters))
        data (:body parameters)]
    {:status 200 :body {:id id
                        :data data}}))

(defn delete-patient [{:keys [parameters]}]
  (let [id (:id (:path parameters))
        data (db/delete-patient id)]
    {:status 200 :body {:patient data :parameters parameters}}))
