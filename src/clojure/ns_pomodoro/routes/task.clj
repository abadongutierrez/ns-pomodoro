(ns ns-pomodoro.routes.task
  (:require [compojure.core :refer :all]
            [liberator.core :refer [defresource resource request-method-in]]
            [cheshire.core :refer [generate-string]]
            [ns-pomodoro.models.task :as tasks]
            [ns-pomodoro.views.layout :as layout]
            [ns-pomodoro.views.util :as util]
            [clj-time.core :as t]))

(defn render-tasks []
    (layout/render-layout "tasks/list" {
        :title "Tasks" 
        :tasks (tasks/get-not-finished-tasks (util/user-id))}))

(defn render-new-tasks []
    (layout/render-layout "tasks/index" {
        :title "Tasks"
        :outlet "{{outlet}}"}))

(defn render-pomodoro [task]
    (layout/render-layout "pomodoros/show" {:title (str "Pomodoro for Task '" (:name task) "'") 
                                      :task task 
                                      :total-pomodoros (count (:pomodoros task))}))

(defn create-task [name]
    (do
        (tasks/create-task name (util/user-id))
        (render-tasks)))

(defn finish-task [task-id]
    (do
        (tasks/finish-task task-id)
        (render-tasks)))

; Render the page to create a new pomodoro for the specified task
(defn new-pomodoro [task-id]
    (do
        (render-pomodoro (tasks/get-task-with-pomodoros task-id))))

(defroutes task-routes
  (GET "/tasks" {params :query-params} (if (= "true" (get params "new")) (render-new-tasks) (render-tasks)))
  (POST "/tasks" [name] (create-task name))
  (DELETE "/tasks/:task-id" [task-id] (new-pomodoro task-id))
  (POST "/tasks/:task-id/pomodoros" [task-id] (new-pomodoro task-id))
  (POST "/tasks/:task-id/done" [task-id] (finish-task task-id)))