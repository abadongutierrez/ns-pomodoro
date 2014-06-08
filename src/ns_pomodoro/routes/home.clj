(ns ns-pomodoro.routes.home
  (:require [compojure.core :refer :all]
            [ns-pomodoro.views.layout :as layout]
            [ns-pomodoro.views.util :as util]))

(defn home []
    (let [flash (util/get-flash!)]
        (layout/render-layout "index" {:flash flash})))

(defroutes home-routes
  (GET "/" [] (home)))
