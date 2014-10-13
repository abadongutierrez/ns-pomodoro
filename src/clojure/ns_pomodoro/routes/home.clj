(ns ns-pomodoro.routes.home
  (:require [compojure.core :refer :all]
            [ns-pomodoro.views.layout :as layout]
            [ns-pomodoro.views.util :as util]
            [noir.session :as session]))

(defn home []
    (let [flash (util/get-flash!)]
        (if (nil? (session/get :user))
            (layout/render-layout "login" {:flash flash})
            (layout/render-layout "index" {:flash flash}))))

(defroutes home-routes
  (GET "/" [] (home)))
