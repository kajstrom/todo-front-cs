(ns todo-front-cs.core
    (:require
      [reagent.core :as r]
      [ajax.core :as ajax]))
(def api-url "https://cvm3yhffx8.execute-api.eu-central-1.amazonaws.com/latest/todos")


(def todos (r/atom []))
(defn get-todos
  []
  (ajax/GET api-url
            {:handler #(reset! todos %)}))

(defn add-todo
  [todo]
  (ajax/POST api-url {:params todo
              :format :json
              :handler get-todos}))

;; -------------------------
;; Views
(defn todo-list-item
  [todo]
  [:div
   [:input {:type "checkbox"
            :checked (get todo "done")}]
   [:span (get todo "description")]])

(defn todo-list
  []
  [:div (map todo-list-item @todos)])


(defn todo-app []
  (get-todos)
  [:div [:h2 "Welcome to the TODO app"]
   [todo-list]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [todo-app] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
