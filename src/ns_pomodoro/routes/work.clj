(ns ns-pomodoro.routes.work
    (:require [compojure.core :refer :all]
              [ns-pomodoro.views.layout :as layout]
              [ns-pomodoro.models.work :as work]))

(defn render-week []
    (layout/render-layout "work/week" {:week (work/get-week-pomodoros)}))

(defroutes work-routes
  (GET "/work/week" [] (render-week)))