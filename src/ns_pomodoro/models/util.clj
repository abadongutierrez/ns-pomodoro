(ns ns-pomodoro.models.util
    (:require [clojure.java.jdbc :as sql]
              [ns-pomodoro.models.db :as db]))

; Util method to force value to be Long
(defn get-long [value]
    (if (instance? Long value) value (Long/valueOf (str value))))