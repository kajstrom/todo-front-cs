(ns todo-front-cs.core
    (:require
      [reagent.core :as r]
      [ajax.core :as ajax]))

(def todos (r/atom []))
(defn get-todos
  []
  (ajax/GET "https://cvm3yhffx8.execute-api.eu-central-1.amazonaws.com/latest/todos"
            {:handler #(reset! todos %)}))

;; -------------------------
;; Views

(defn home-page []
  [:div [:h2 "Welcome to TODO"]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
