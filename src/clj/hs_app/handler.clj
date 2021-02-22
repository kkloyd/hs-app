(ns hs-app.handler
  (:require
   [hs-app.middleware :refer [middleware]]
   [hs-app.services :refer [get-patients
                            get-patient
                            create-patient
                            edit-patient
                            delete-patient]]
   [reitit.ring :as reitit-ring]
   [hiccup.page :refer [include-js include-css html5]]
   [config.core :refer [env]]
   [muuntaja.core :as m]
   [schema.core :as s]
   [reitit.coercion.schema :refer [coercion]]
   [reitit.ring.coercion :refer [coerce-exceptions-middleware
                                 coerce-request-middleware
                                 coerce-response-middleware]]
   [reitit.ring.middleware.exception :refer [exception-middleware]]
   [reitit.ring.middleware.muuntaja :refer [format-middleware]]))

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name    "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])


(def mount-target
  [:div#app
   [:div.spinner
    [:h1 "Loading"]]])

(defn render-page []
  (html5
   (head)
   [:body
    mount-target
    (include-js "/js/app.js")
    [:script "hs_app.core.init_BANG_()"]]))

(defn index-handler
  [_request]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (render-page)})



(def routes [["/" {:get {:handler index-handler}}]
             ["/edit/:id" {:get {:handler index-handler
                                 :parameters {:path {:id s/Int}}}}]
             ["/create" {:get {:handler index-handler}}]

             ["/api"
              ["/patients" {:get get-patients ;; TODO add filter and pagination
                            :post {:parameters {:body {:fullname s/Str
                                                       :gender s/Int
                                                       :birth_date s/Str
                                                       :address s/Str
                                                       :policy_number s/Int}}
                                   :handler create-patient}}]

              ["/patient/:id" {:get {:parameters {:path {:id s/Int}}
                                     :handler get-patient}
                               :put {:parameters {:path {:id s/Int}
                                                  :body {:fullname s/Str
                                                         :gender s/Int
                                                         :birth_date s/Str
                                                         :address s/Str
                                                         :policy_number s/Int}}
                                     :handler edit-patient}
                               :delete {:parameters {:path {:id s/Int}}
                                        :handler delete-patient}}]]])



(def router (fn [] (reitit-ring/router [routes]
                                       {:data  {:muuntaja m/instance
                                                :coercion coercion
                                                :middleware [format-middleware
                                                             exception-middleware
                                                             coerce-exceptions-middleware
                                                             coerce-request-middleware
                                                             coerce-response-middleware]}})))


(def app
  (reitit-ring/ring-handler
   (router)
   (reitit-ring/routes
    (reitit-ring/create-resource-handler {:path "/" :root "/public"})
    (reitit-ring/create-default-handler))
   {:middleware middleware}))

