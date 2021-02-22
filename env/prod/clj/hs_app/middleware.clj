(ns hs-app.middleware
  (:require
   [ring.middleware.defaults :refer [site-defaults wrap-defaults]]))

(def site-defaults-custom (assoc-in site-defaults [:security :anti-forgery] false))


(def middleware
  [#(wrap-defaults % site-defaults-custom)])
