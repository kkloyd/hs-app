(ns hs-app.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [hs-app.cljs.test]))

(doo-tests 'hs-app.cljs.test)
