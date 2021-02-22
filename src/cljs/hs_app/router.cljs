(ns hs-app.router
  (:require
   [reitit.frontend :as reitit]))


(def router
  (reitit/router
   [["/" :index]
    ["/create" :create-page]
    ["/edit/:id" :edit-page]]))


(defn path-for [route & [params]]
  (if params
    (:path (reitit/match-by-name router route params))
    (:path (reitit/match-by-name router route))))