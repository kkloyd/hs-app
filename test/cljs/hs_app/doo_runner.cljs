(ns hs-app.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [hs-app.core-test]))

(doo-tests 'hs-app.core-test)
