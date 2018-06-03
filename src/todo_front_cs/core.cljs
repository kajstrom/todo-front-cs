(ns todo-front-cs.core
    (:require
      [reagent.core :as r]
      [ajax.core :as ajax]))
(def api-url "https://cvm3yhffx8.execute-api.eu-central-1.amazonaws.com/latest/todos")


(def todos (r/atom []))
(def todo-to-add (r/atom ""))

(defn positions
  "https://stackoverflow.com/questions/4830900/how-do-i-find-the-index-of-an-item-in-a-vector"
  [pred coll]
  (keep-indexed (fn [idx x]
                  (when (pred x)
                    idx))
                coll))

(defn find-todo-idx [todo]
  (let [todoId (:todoId todo)]
    (first (positions #(= todoId (:todoId %)) @todos))))

(defn get-todos
  []
  (ajax/GET api-url
            {:handler #(reset! todos %)
             :response-format :json
             :keywords? true}))

(defn add-todo
  [todo]
  (ajax/POST api-url {:params todo
              :format :json
              :handler get-todos}))

(defn update-todo [todo]
  (let [todoId (:todoId todo)
        idx (find-todo-idx todo)]
    (swap! todos assoc-in [idx] todo)))

(defn save-todo
  [todo]
  (ajax/PUT (str api-url "/" (:todoId todo)) {:params todo
                                                  :format :json
                                                  :handler get-todos}))

;; -------------------------
;; Views
(defn todo-list-item
  [todo]
  (let [todoId (:todoId todo)]
    ^{:key todoId} [:div
                                 [:input {:type "checkbox"
                                          :checked (:done todo)
                                          :on-change #(update-todo (update todo :done not))}]
                                 [:input {:type "text"
                                          :value (:description todo)
                                          :on-change #(update-todo (assoc todo :description (-> % .-target .-value)))}]]))

(defn todo-list
  []
  [:div (map todo-list-item @todos)])

(defn add-todo-form
  []
  [:div
   [:h3 "Add a TODO"]
   [:input {:type "text" :value @todo-to-add :on-change #(reset! todo-to-add (-> % .-target .-value))}]
   [:input {:type "button" :value "Add" :on-click #(do
                                                    (add-todo {:description @todo-to-add})
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
