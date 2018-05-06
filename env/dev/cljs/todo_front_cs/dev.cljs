(ns ^:figwheel-no-load todo-front-cs.dev
  (:require
    [todo-front-cs.core :as core]
    [devtools.core :as devtools]))


(enable-console-print!)

(devtools/install!)

(core/init!)
