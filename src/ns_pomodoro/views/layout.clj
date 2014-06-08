(ns ns-pomodoro.views.layout
  (:require [hiccup.page :refer [html5 include-css]]
            [clostache.parser :refer [render-resource]]
            [clojure.java.io :as io]))

(defn common [& body]
    (html5
        [:head
            [:title "Welcome to ns-pomodoro"]
            (include-css "/css/screen.css")]
        [:body body]))

(defn render-page [template data partials]
    (render-resource
        (str "templates/" template ".mustache")
        data
        (reduce (fn [accum pt] ;; "pt" is the name (as a keyword) of the partial.
              (assoc accum pt (slurp (io/resource (str "templates/layout/"
                                                       (name pt)
                                                       ".mustache")))))
            {}
            partials)))

(defn render-layout [template data]
    (render-page template data [:header :footer :inner-header :inner-footer]))
