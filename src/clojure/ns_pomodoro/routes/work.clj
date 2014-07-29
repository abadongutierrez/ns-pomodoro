(ns ns-pomodoro.routes.work
    (:require [clojure.string :as str]
              [compojure.core :refer :all]
              [ns-pomodoro.views.layout :as layout]
              [ns-pomodoro.models.work :as work]
              [ns-pomodoro.views.util :as util]
              [ns-pomodoro.models.util :as mutil]))

(defn render-week [offset]
    (let [int-offset (mutil/get-int offset)]
        (layout/render-layout "work/week" {
            :week (work/get-week-pomodoros int-offset (util/user-id)) 
            :offset-backward (- int-offset 1) 
            :offset-forward (+ int-offset 1)})))

(defroutes work-routes
  (GET "/work/week" [offset] (render-week (if (str/blank? offset) 0 offset))))