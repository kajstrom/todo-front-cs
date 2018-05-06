(ns todo-front-cs.prod
  (:require
    [todo-front-cs.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
