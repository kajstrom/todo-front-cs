(ns todo-front-cs.core
    (:require
      [reagent.core :as r]
      [ajax.core :as ajax]))
(def api-url "https://cvm3yhffx8.execute-api.eu-central-1.amazonaws.com/latest/todos")


(def todos (r/atom []))

(defn handle-respose [response]
  (into (hash-map) (map #(hash-map (:todoId %) %) response)))

(defn get-todos
  []
  (ajax/GET api-url
            {:handler #(reset! todos (handle-respose %))
             :response-format :json
             :keywords? true}))

(defn add-todo
  [todo]
  (ajax/POST api-url {:params todo
              :format :json
              :handler get-todos}))

(defn update-todo [todo]
  (let [todoId (:todoId todo)]
    (swap! todos assoc-in [todoId] todo)))

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
                            :on-change #(update-todo (assoc todo :description (-> % .-target .-value)))}]
                    [:input {:type "button"
                             :value "Save"
                             :on-click #(save-todo todo)}]]))

(defn todo-list
  []
  [:div (map todo-list-item (vals @todos))])

(defn add-todo-form
  []
  (let [new-todo (r/atom "")]
    (fn []
      [:div
       [:h3 "Add a TODO"]
       [:input {:type "text" :value @new-todo :on-change #(reset! new-todo (-> % .-target .-value))}]
       [:input {:type "button" :value "Add" :on-click #(do
                                                        (add-todo {:description @new-todo})
                                                        (reset! new-todo ""))}]])))

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
