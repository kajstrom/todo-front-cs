(ns todo-front-cs.core
    (:require
      [reagent.core :as r]
      [ajax.core :as ajax]))
(def api-url "https://cvm3yhffx8.execute-api.eu-central-1.amazonaws.com/latest/todos")


(def todos (r/atom []))
(def todo-to-add (r/atom ""))
(defn get-todos
  []
  (ajax/GET api-url
            {:handler #(reset! todos %)}))

(defn add-todo
  [todo]
  (ajax/POST api-url {:params todo
              :format :json
              :handler get-todos}))

(defn update-todo
  [todo]
  (ajax/PUT (str api-url "/" (get todo "todoId")) {:params todo
                                                  :format :json
                                                  :handler get-todos}))

;; -------------------------
;; Views
(defn todo-list-item
  [todo]
  ^{:key (get todo "todoId")} [:div
     [:input {:type "checkbox"
              :checked (get todo "done")
              :on-change #(update-todo (update todo "done" not))}]
     [:span (get todo "description")]])

(defn todo-list
  []
  [:div (map todo-list-item @todos)])

(defn add-todo-form
  []
  [:div
   [:h3 "Add a TODO"]
   [:input {:type "text" :value @todo-to-add :on-change #(reset! todo-to-add (-> % .-target .-value))}]
   [:input {:type "button" :value "Add" :on-click #(do
                                                    (add-todo {"description" @todo-to-add})
                                                    (reset! todo-to-add ""))}]])

(defn todo-app []
  (get-todos)
  [:div [:h2 "Welcome to the TODO app"]
   [todo-list]
   [add-todo-form]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [todo-app] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
