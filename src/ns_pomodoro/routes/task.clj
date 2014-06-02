(ns ns-pomodoro.routes.task
  (:require [compojure.core :refer :all]
            [ns-pomodoro.models.task :as tasks]
            [ns-pomodoro.views.layout :as layout]))

(defn render-tasks []
    (layout/render-layout "tasks" {:title "Tasks" :tasks (tasks/read-tasks)}))

(defn render-pomodoro [task]
    (layout/render-layout "pomodoro" {:title (str "Pomodoro for Task '" (:name task) "'") :task task}))

(defn list-tasks []
    (render-tasks))

(defn create-task [name]
    (do
        (tasks/create-task name)
        (render-tasks)))

(defn new-pomodoro [task-id]
    (do
        ; first because create-pomodoro return a sequence with a map of the new row
        (render-pomodoro (tasks/get-task task-id))))

(defn start-pomodoro [task-id]
    (let [new-pomodoro (tasks/create-pomodoro task-id)]
        (str "{ id: " (:pomodoro_id new-pomodoro) " }")))

(defn end-pomodoro [pomodoro-id]
    (do
        (tasks/end-pomodoro pomodoro-id)
        (str "done")))

(defroutes task-routes
  (GET "/tasks" [] (list-tasks))
  (POST "/tasks" [name] (create-task name))
  ; TODO do not create pomodoro until user starts it
  (POST "/tasks/:task-id/pomodoros" [task-id] (new-pomodoro task-id))
  (POST "/tasks/:task-id/pomodoros/start" [task-id pomodoro-id] (start-pomodoro task-id))
  (POST "/tasks/:task-id/pomodoros/:pomodoro-id/end" [task-id pomodoro-id] (end-pomodoro pomodoro-id)))