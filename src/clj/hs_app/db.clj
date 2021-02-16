(ns hs-app.db
  (:require [next.jdbc :as njdbc]
            [next.jdbc.result-set :as rs]
            [honeysql.core :as h]
            [honeysql.helpers :as hh]))

(def db-config
  {:dbtype "postgresql"
   :dbname "patients_db"
   :host "localhost"
   :user "postgres"
   :password "postgres"})

(def db (njdbc/get-datasource db-config))

(defn db-query [sql]
  (njdbc/execute! db sql
                  {:return-keys true
                   :builder-fn rs/as-unqualified-maps}))

(defn db-query-one [sql]
  (njdbc/execute-one! db sql
                      {:return-keys true
                       :builder-fn rs/as-unqualified-maps}))

(defn create-patient
  [{:keys [fullname gender birth_date address policy_number]}]
  (let [created (->
                 (hh/insert-into :patients)
                 (hh/columns :fullname :gender :birth_date :address :policy_number)
                 (hh/values [[fullname gender birth_date address policy_number]])
                 h/format
                 db-query-one)]
    created))

(defn get-all-patients []
  (let [patients (->
                  (hh/select :*)
                  (hh/from :patients)
                  h/format
                  db-query)]
    patients))

(defn get-patient [{:keys [id]}]
  (let [patient (->
                 (hh/select :*)
                 (hh/from :patients)
                 (hh/where := :id id)
                 h/format
                 db-query-one)]
    patient))

(defn delete-patient [id]
  (let [patient (->
                  (hh/delete-from :patients)
                  (hh/where := :id id)
                  h/format
                  db-query-one)]
    patient))

(comment
  db
  (njdbc/execute! db ["CREATE TABLE patients(id SERIAL NOT NULL, 
                      fullname VARCHAR(100) NOT NULL, 
                      gender SMALLINT NOT NULL, 
                      birth_date VARCHAR(20) NOT NULL, 
                      address VARCHAR(100) NOT NULL, 
                      policy_number BIGINT NOT NULL)"])
  
  (njdbc/execute! db ["drop table patients"])

  (njdbc/execute! db ["delete from patients"])

  (get-all-patients)
  (delete-patient 16)

  (create-patient {:fullname "Sherlock Holmes"
                   :gender 1
                   :birth_date "01.01.1989"
                   :address "221B Baker St, Marylebone, London NW1 6XE, UK"
                   :policy_number 123412341314})
  )