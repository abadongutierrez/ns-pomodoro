(ns ns-pomodoro.routes.task
  (:require [compojure.core :refer :all]
            [liberator.core :refer [defresource resource request-method-in]]
            [cheshire.core :refer [generate-string]]
            [ns-pomodoro.models.task :as tasks]
            [ns-pomodoro.views.layout :as layout]))

(defn render-tasks []
    (layout/render-layout "tasks/list" {:title "Tasks" :tasks (tasks/read-tasks)}))

(defn render-pomodoro [task]
    (layout/render-layout "pomodoros/show" {:title (str "Pomodoro for Task '" (:name task) "'") 
                                      :task task 
                                      :total-pomodoros (count (:pomodoros task))}))

(defn list-tasks []
    (render-tasks))

(defn create-task [name]
    (do
        (tasks/create-task name)
        (render-tasks)))

; Render the page to create a new pomodoro for the specified task
(defn new-pomodoro [task-id]
    (do
        (render-pomodoro (tasks/get-task-with-pomodoros task-id))))

; Resource to start a new pomodoro
(defresource start-pomodoro [task-id]
    :allowed-methods [:post]
    :available-media-types ["application/json"]
    :post!
        (fn [context]
            (let [new-pomodoro (tasks/create-pomodoro task-id)]
                {::id (:pomodoro_id new-pomodoro)}))
    :handle-created
        (fn [context]
            (generate-string (tasks/get-pomodoro (::id context)))))

; Resource to end a existing pomodoro
(defresource end-pomodoro [task-id pomodoro-id]
    :allowed-methods [:post]
    :available-media-types ["application/json"]
    :post!
        (fn [_]
            (tasks/end-pomodoro pomodoro-id))
    :handle-created
        (fn [context]
            (generate-string (tasks/get-pomodoro pomodoro-id))))

(defroutes task-routes
  (GET "/tasks" [] (list-tasks))
  (POST "/tasks" [name] (create-task name))
  ; TODO do not create pomodoro until user starts it
  (POST "/tasks/:task-id/pomodoros" [task-id] (new-pomodoro task-id))
  (ANY "/tasks/:task-id/pomodoros/start" [task-id] (start-pomodoro task-id))
  (ANY "/tasks/:task-id/pomodoros/:pomodoro-id/end" [task-id pomodoro-id] (end-pomodoro task-id pomodoro-id)))