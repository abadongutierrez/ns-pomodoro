(ns ns-pomodoro.routes.task
  (:require [compojure.core :refer :all]
            [ns-pomodoro.models.task :as tasks]
            [ns-pomodoro.views.layout :as layout]))

(defn render-tasks []
    (layout/render-layout "tasks/list" {:title "Tasks" :tasks (tasks/read-tasks)}))

(defn render-pomodoro [pomodoro task]
    (layout/render-layout "pomodoros/show" {:title (str "Pomodoro for Task '" (:name task) "'") 
                                      :pomodoro pomodoro 
                                      :task task 
                                      :total-pomodoros (count (:pomodoros task))}))

(defn list-tasks []
    (render-tasks))

(defn create-task [name]
    (do
        (tasks/create-task name)
        (render-tasks)))

(defn create-pomodoro [task-id]
    (do
        ; first because create-pomodoro return a sequence with a map of the new row
        (render-pomodoro (first (tasks/create-pomodoro task-id)) (tasks/get-task-with-pomodoros task-id))))

(defn start-pomodoro [pomodoro-id]
    (do
        (tasks/start-pomodoro pomodoro-id)
        (str "done")))

(defn end-pomodoro [pomodoro-id]
    (do
        (tasks/end-pomodoro pomodoro-id)
        (str "done")))

(defroutes task-routes
  (GET "/tasks" [] (list-tasks))
  (POST "/tasks" [name] (create-task name))
  ; TODO do not create pomodoro until user starts it
  (POST "/tasks/:task-id/pomodoros" [task-id] (create-pomodoro task-id))
  (POST "/tasks/:task-id/pomodoros/:pomodoro-id/start" [task-id pomodoro-id] (start-pomodoro pomodoro-id))
  (POST "/tasks/:task-id/pomodoros/:pomodoro-id/end" [task-id pomodoro-id] (end-pomodoro pomodoro-id)))