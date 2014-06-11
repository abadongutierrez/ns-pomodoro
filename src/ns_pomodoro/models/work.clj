(ns ns-pomodoro.models.work
    (:require [clj-time.core :as t]
              [ns-pomodoro.models.task :as tasks]))

(defn get-day-of-week [day]
    (.get day (org.joda.time.DateTimeFieldType/dayOfWeek)))

(defn get-week []
    (let [week-start (.withMinimumValue (.dayOfWeek (t/today)))
          week-end (.withMaximumValue (.dayOfWeek (t/today)))
          x (range 0 7)]
        (loop [day week-start days []]
            (if (= (get-day-of-week day) (get-day-of-week week-end))
                (conj days day)
                (recur (.plusDays day 1) (conj days day))))))

(defn get-week-pomodoros [user-id]
    (let [week (get-week)]
        (map #(assoc {:pomodoros (tasks/get-pomodoros-per-day % user-id)} :day %) week)))