(ns hs-app.core-test
  (:require [clojure.test :refer [is deftest testing use-fixtures]]
            [hs-app.db :as db]
            [hs-app.services :as services]))

(defn test-fixture [test]
  (db/create-table-patients!)
  (test)
  (db/drop-table-patients!))


(use-fixtures :each test-fixture)

(defn create-patient [] (services/create-patient! {:parameters {:body {:fullname "Test testovich Testov"
                                                                       :gender 1
                                                                       :birth_date "2020-01-01"
                                                                       :address "221B Baker St, Marylebone, London NW1 6XE, UK"
                                                                       :policy_number 423412341314}}}))

(defn edit-patient [id] (services/edit-patient! {:parameters {:path {:id id}
                                                              :body {:fullname "Edited User"
                                                                     :gender 0
                                                                     :birth_date "2020-01-01"
                                                                     :address "221B Baker St, Marylebone, London NW1 6XE, UK"
                                                                     :policy_number 123412341314}}}))

(defn delete-patient
  [id] (services/delete-patient! {:parameters {:path {:id id}}}))

(defn get-patient
  [id] (services/get-patient {:parameters {:path {:id id}}}))

;; Tests crud services
(deftest get-empty-patients-test
  (testing "get empty patients"
    (is (= [] (db/get-all-patients)))))

(deftest create-patient-test
  (testing "create patient and test count 1"
    (create-patient)
    (is (= 1 (count (db/get-all-patients)))))
  (testing "create 2 more patients and test count 3"
    (create-patient)
    (create-patient)
    (is (= 3 (count (db/get-all-patients))))))

(deftest get-patient-test
  (testing "create patient and get patient with id 1"
    (create-patient)
    ;; id always = 1 after recreate table
    (is (= "Test testovich Testov" (:fullname (:patient (:body (get-patient 1))))))))

(deftest delete-patient-test
  (testing "delete patient and test count 0"
    (create-patient)
    (delete-patient 1) ;; id always = 1 after recreate table
    (is (= 0 (count (db/get-all-patients))))))

(deftest edit-patient-test
  (testing "create patient and update it"
    (create-patient)
    (is (= 1 (:gender (:patient (:body (get-patient 1))))))
    (edit-patient 1)
    (is (= "Edited User" (:fullname (:patient (:body (get-patient 1))))))
    (is (= 0 (:gender (:patient (:body (get-patient 1))))))))


