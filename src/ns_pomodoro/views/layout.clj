(ns ns-pomodoro.views.layout
  (:require [hiccup.page :refer [html5 include-css]]
            [clostache.parser :refer [render-resource]]
            [clojure.java.io :as io]
            [noir.session :as session]
            [ns-pomodoro.views.util :as util]))

(defn common [& body]
    (html5
        [:head
            [:title "Welcome to ns-pomodoro"]
            (include-css "/css/screen.css")]
        [:body body]))

(defn render-page [template data partials]
    (render-resource
        (str "templates/" template ".mustache")
        ;; add :user to the data map
        ;; add :flash from session if it does not exist already in data
        (let [new-data (assoc data :user (session/get :user))]
            (if (= nil (:flash new-data)) (assoc new-data :flash (util/get-flash!))))
        (reduce (fn [accum pt] ;; "pt" is the name (as a keyword) of the partial.
              (assoc accum pt (slurp (io/resource (str "templates/layout/"
                                                       (name pt)
                                                       ".mustache")))))
            {}
            partials)))

(defn render-layout [template data]
    (render-page template data [:header :footer :inner-header :inner-footer]))
