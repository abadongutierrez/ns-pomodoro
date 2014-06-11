(ns ns-pomodoro.routes.work
    (:require [compojure.core :refer :all]
              [ns-pomodoro.views.layout :as layout]
              [ns-pomodoro.models.work :as work]
              [ns-pomodoro.views.util :as util]))

(defn render-week []
    (layout/render-layout "work/week" {:week (work/get-week-pomodoros (util/user-id))}))

(defroutes work-routes
  (GET "/work/week" [] (render-week)))