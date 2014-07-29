(ns ns-pomodoro.handler
  (:require [compojure.core :refer [defroutes]]
            [compojure.route :as route]
            [noir.util.middleware :as noir-middleware]
            [ns-pomodoro.routes.home :refer [home-routes]]
            [ns-pomodoro.routes.task :refer [task-routes]]
            [ns-pomodoro.routes.work :refer [work-routes]]
            [ns-pomodoro.routes.auth :refer [auth-routes]]
            [ns-pomodoro.routes.api :refer [api-routes]]))

(defn init []
  (println "ns-pomodoro is starting"))

(defn destroy []
  (println "ns-pomodoro is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app (noir-middleware/app-handler [
    home-routes task-routes work-routes auth-routes api-routes app-routes]))


