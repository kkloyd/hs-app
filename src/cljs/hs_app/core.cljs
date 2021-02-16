(ns hs-app.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [reagent.core :as reagent]
   [reagent.dom :as rdom]
   [reagent.session :as session]
   [reitit.frontend :as reitit]
   [clerk.core :as clerk]
   [accountant.core :as accountant]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]))

;; -------------------------
;; Routes

(def router
  (reitit/router
   [["/" :index]
    ["/:id" :details]
    ;; ["/create" :form-page]
    ["/:id/edit" :form-page]]))


(defn path-for [route & [params]]
  (if params
    (:path (reitit/match-by-name router route params))
    (:path (reitit/match-by-name router route))))

;; -------------------------
;; Page components

(defn home-page []
  (fn []
    [:div.main
     [:div.main__title
      [:h1 "Список пациентов"]
      [:button.btn.btn-primary {:style {:margin-left "20px"}} "Добавить"]]
     [:ul.patients-list
      (map (fn [patient-id]
             [:li {:name (str "item-" patient-id) :key (str "item-" patient-id)}
              [:a {:href (path-for :details {:id patient-id})} "Пациент: " patient-id]])
           (range 1 10))]]))



(defn view-page []
  (fn []
    (let [routing-data (session/get :route)
          item (get-in routing-data [:route-params :id])]
      [:span.main
       [:h1 (str "Item " item " of hs-app")]])))



;; -------------------------
;; Translate routes -> page components

(defn page-for [route]
  (case route
    :index #'home-page
    :details #'view-page))


;; -------------------------
;; Page mounting component

(defn current-page []
  (fn []
    (let [page (:current-page (session/get :route))]
      [:div.page
       [:header
        [:p.link [:a {:href (path-for :index)} "Пациенты"]]]
       [page]
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
        (reagent/after-render clerk/after-render!)
        (session/put! :route {:current-page (page-for current-page)
                              :route-params route-params})
        (clerk/navigate-page! path)))
    :path-exists?
    (fn [path]
      (boolean (reitit/match-by-path router path)))})
  (accountant/dispatch-current!)
  (mount-root))
