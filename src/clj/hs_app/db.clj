(ns hs-app.db
  (:require [config.core :refer [env]]
            [next.jdbc :as njdbc]
            [next.jdbc.result-set :as rs]
            [honeysql.core :as h]
            [honeysql.helpers :as hh]))

(def dbname (if (:test env) "patients_db_test" "patients_db"))
(def dbport (if (:test env) "5441" "5440"))

(def db-config
  {:dbtype "postgresql"
   :dbname dbname
   :host "localhost"
   :port dbport
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

(defn create-patient!
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

(defn get-patient-by-id [id]
  (let [patient (->
                 (hh/select :*)
                 (hh/from :patients)
                 (hh/where := :id id)
                 h/format
                 db-query-one)]
    patient))

(defn delete-patient! [id]
  (let [deleted (->
                 (hh/delete-from :patients)
                 (hh/where := :id id)
                 h/format
                 db-query-one)]
    deleted))

(defn update-patient! [id {:keys [fullname gender birth_date address policy_number]}]
  (let [updated (->
                 (hh/update :patients)
                 (hh/sset {:fullname fullname
                           :gender gender
                           :birth_date birth_date
                           :address address
                           :policy_number policy_number})
                 (hh/where := :id id)
                 h/format
                 db-query-one)]
    updated))


(defn create-table-patients! [] (njdbc/execute! db ["CREATE TABLE patients(id SERIAL NOT NULL, 
                      fullname VARCHAR(100) NOT NULL, 
                      gender SMALLINT NOT NULL, 
                      birth_date VARCHAR(20) NOT NULL, 
                      address VARCHAR(100) NOT NULL, 
                      policy_number BIGINT NOT NULL)"]))

(defn drop-table-patients! [] (njdbc/execute! db ["drop table patients"]))


(comment
  db
  (->
   (hh/select :*, :%count.id)
   (hh/from :patients)
   h/format)

  (njdbc/execute! db ["CREATE TABLE patients(id SERIAL NOT NULL, 
                      fullname VARCHAR(100) NOT NULL, 
                      gender SMALLINT NOT NULL, 
                      birth_date VARCHAR(20) NOT NULL, 
                      address VARCHAR(100) NOT NULL, 
                      policy_number BIGINT NOT NULL)"])

  (njdbc/execute! db ["drop table patients"])

  (njdbc/execute! db ["delete from patients"])

  (get-all-patients)
  (get-patient-by-id 11)
  (delete-patient! 16)

  (create-patient! {:fullname "Sherlock Holmes"
                   :gender 1
                   :birth_date "01.01.1989"
                   :address "221B Baker St, Marylebone, London NW1 6XE, UK"
                   :policy_number 423412341314})

  (println "sometext"))