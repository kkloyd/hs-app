(ns hs-app.services
  (:require [hs-app.db :as db]
            [hs-app.util :refer [valid-gender?]]))

(defn get-patients [_]
  (let [patients (db/get-all-patients)
        total (count patients)]
    {:status 200
     :body {:patients patients :total total}}))

(defn get-patient [{:keys [parameters]}]
  (let [id (:id (:path parameters))
        patient (db/get-patient-by-id id)]
    (if (nil? patient)
      {:status 404 :body {:error (str "Patient with id " id " not found")}}
      {:status 200 :body {:patient patient}})))


(defn create-patient [{:keys [parameters]}]
  (let [data (:body parameters)
        gender (:gender data)]
    (if (valid-gender? gender)
      (let [patient (db/create-patient data)]
        {:status 201
         :body {:patient patient}})
      {:status 400
       :body {:error (str "Not a valid gender " gender)}})))


(defn edit-patient [{:keys [parameters]}]
  (let [id (:id (:path parameters))
        data (:body parameters)
        gender (:gender data)]
    (if (valid-gender? gender)
      (let [patient (db/update-patient id data)]
        (if (nil? patient)
          {:status 404 :body {:error (str "Patient with id " id " not found")}}
          {:status 200 :body {:patient patient}}))
      {:status 400
       :body {:error (str "Not a valid gender " gender)}})))


(defn delete-patient [{:keys [parameters]}]
  (let [id (:id (:path parameters))
        patient (db/delete-patient id)]
    (if (nil? patient)
      {:status 404 :body {:error (str "Patient with id " id " not found")}}
      {:status 200 :body {:patient patient}})))



(comment

  (create-patient {:parameters {:body {:fullname "Test testovich Testov"
                                       :gender 1
                                       :birth_date "02.02.1912"
                                       :address "221B Baker St, Marylebone, London NW1 6XE, UK"
                                       :policy_number 423412341314}}})

  (get-patients [])
  (get-patient {:parameters {:path {:id 59}}})

  (edit-patient {:parameters {:path {:id 5}
                              :body {:fullname "Test testovich Testov"
                                     :gender 0
                                     :birth_date "02.02.1912"
                                     :address "221B Baker St, Marylebone, London NW1 6XE, UK"
                                     :policy_number 123412341314}}})

  (println "commetn"))
