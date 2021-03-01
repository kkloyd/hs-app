(ns hs-app.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [reagent.session :as session]
   [reitit.frontend :as reitit]
   [clerk.core :as clerk]
   [accountant.core :as accountant]
   [hs-app.router :refer [router path-for]]
   [hs-app.states :refer [patients-data form-fields]]
   [hs-app.components :refer [patients-list patients-form message-popup]]
   [hs-app.api :refer [fetch-patients! get-patient!]]))


;; -------------------------
;; Page components

(defn home-page []
  (fetch-patients! patients-data)
  (fn [] (when (not (nil? @patients-data))
           (let [data @patients-data
                 total (:total data)
                 patients (:patients data)]
             [:<>
              [:div.main__title
               [:h1.title "Список пациентов: " total]

               [:a {:href (path-for :create-page)}
                [:button.btn.btn-primary {:style {:margin-left "20px"}}
                 "Добавить"]]]

              (if (> (count patients) 0)
                [patients-list patients]

                [:span "Нет данных"])]))))



(defn form-page []
  (let [routing-data (session/get :route)
        id (get-in routing-data [:route-params :id])
        form (r/atom form-fields)]
    (cond
      (nil? id) [patients-form form]
      :else
      (fn [] (let [edit-form (r/atom nil)]
               (get-patient! id edit-form)
               (fn []
                 (when (not (nil? @edit-form))
                   [patients-form edit-form])))))))


;; -------------------------
;; Translate routes -> page components
(defn page-for [route]
  (case route
    :index #'home-page
    :create-page #'form-page
    :edit-page #'form-page))


;; -------------------------
;; Page mounting component

(defn current-page []
  (fn []
    (let [page (:current-page (session/get :route))]
      [:div.page
       [message-popup]
       [:header
        [:p.link [:a {:href (path-for :index)} "Пациенты"]]]
       [:div.main [page]]
       [:footer]])))

;; -------------------------
;; Initialize app

(defn mount-root []
  (rdom/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (clerk/initialize!)
  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (let [match (reitit/match-by-path router path)
            current-page (:name (:data  match))
            route-params (:path-params match)]
        (r/after-render clerk/after-render!)
        (session/put! :route {:current-page (page-for current-page)
                              :route-params route-params})
        (clerk/navigate-page! path)))
    :path-exists?
    (fn [path]
      (boolean (reitit/match-by-path router path)))})
  (accountant/dispatch-current!)
  (mount-root))

