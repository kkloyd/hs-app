(ns hs-app.middleware
  (:require
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.params :refer [wrap-params]]
   [prone.middleware :refer [wrap-exceptions]]
   [ring.middleware.reload :refer [wrap-reload]]
   [ring.middleware.defaults :refer [site-defaults wrap-defaults]]))



(def site-defaults-custom (assoc-in site-defaults [:security :anti-forgery] false))

(def middleware
  [#(wrap-defaults % site-defaults-custom)
   wrap-exceptions
   wrap-reload])
