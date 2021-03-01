(ns hs-app.api
  (:require
   [hs-app.states :refer [patients-data redirect-to-list? message]]
   [ajax.core :refer [GET POST PUT DELETE]]))


(defn fetch-patients! [data]
  (GET "/api/patients" {:response-format :json
                        :keywords? true
                        :handler #(reset! data %)
                        :error-handler (fn [res] (js/console.log res))}))


(defn get-patient! [id patient]
  (GET (str "/api/patient/" id) {:response-format :json
                                 :keywords? true
                                 :handler #(reset! patient %)}))

(defn delete-patient! [id]
  (DELETE (str "/api/patient/" id) {:response-format :json
                                    :keywords? true
                                    :handler #(do (reset! message {:status-ok? true :show? true})
                                                  (swap! patients-data assoc
                                                         :total (dec (:total @patients-data))
                                                         :patients (remove (fn [x]
                                                                             (= (:id x) id)) (:patients @patients-data))))}))

(defn create-patient! [form-data]
  (POST "api/patients" {:keywords? true
                        :format :json
                        :params form-data
                        :response-format :json
                        :handler #(do (reset! redirect-to-list? true) (reset! message {:status-ok? true :show? true}))
                        :error-handler #(do (reset! redirect-to-list? false) (reset! message {:status-ok? false :show? true}))}))

(defn edit-patient! [id form-data]
  (PUT (str "/api/patient/" id) {:keywords? true
                                 :format :json
                                 :params form-data
                                 :response-format :json
                                 :handler #(do (reset! redirect-to-list? true) (reset! message {:status-ok? true :show? true}))
                                 :error-handler #(do (reset! redirect-to-list? false) (reset! message {:status-ok? false :show? true}))}))