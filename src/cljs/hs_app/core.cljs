(ns hs-app.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [reagent.session :as session]
   [reitit.frontend :as reitit]
   [clerk.core :as clerk]
   [accountant.core :as accountant]
  ;;  [hs-app.home :refer [home-page]]
   [clojure.walk :as clj-walk]
   [ajax.core :refer [GET POST PUT DELETE]]))


(defn fetch-patients! [data]
  (GET "/api/patients" {:response-format :json
                        :handler #(reset! data %)
                        :error-handler (fn [{:keys [status status-text]}]
                                         (js/console.log status status-text))}))
;; -------------------------
;; Routes

(def router
  (reitit/router
   [["/" :index]
    ["/view/:id" :view-page]
    ["/create" :form-page]
    ["/edit/:id" :form-page {:id 1}]]))


(defn path-for [route & [params]]
  (if params
    (:path (reitit/match-by-name router route params))
    (:path (reitit/match-by-name router route))))

;; -------------------------
;; Page components

(defn home-page []
  (let [patients-data (r/atom nil)]
    (fetch-patients! patients-data)
    (fn []
      (let [data (clj-walk/keywordize-keys @patients-data)
            total (:total data)
            patients (:patients data)]
        [:div.main
         [:div.main__title
          [:h1 "Список пациентов" [:span "(" total ")"]]
          [:button.btn.btn-primary {:style {:margin-left "20px"}} "Добавить"]]
         [:ul.patients-list
          (map (fn [{:keys [id fullname]}]
                 [:li {:name (str "item-" fullname) :key (str "item-" fullname)}
                  [:a {:href (path-for :view-page {:id id})} "Пациент: " fullname]])
               patients)]]))))


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
    :view-page #'view-page))


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
        (r/after-render clerk/after-render!)
        (session/put! :route {:current-page (page-for current-page)
                              :route-params route-params})
        (clerk/navigate-page! path)))
    :path-exists?
    (fn [path]
      (boolean (reitit/match-by-path router path)))})
  (accountant/dispatch-current!)
  (mount-root))

(comment


  (println "core.cljs"))
