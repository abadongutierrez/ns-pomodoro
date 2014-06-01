(ns ns-pomodoro.models.task
    (:require [clojure.java.jdbc :as sql]
              [ns-pomodoro.models.db :as db]))

(defn read-tasks []
    (sql/with-db-transaction [conn db/db-connection]
        (sql/query conn 
            ["SELECT * FROM task ORDER BY entered_date DESC"])))

(defn get-task [task-id]
    (first (sql/with-db-transaction [conn db/db-connection]
        (sql/query conn 
            ["SELECT * FROM task WHERE task_id = ?" (Long/valueOf task-id)]))))

(defn create-task [name]
    (sql/with-db-transaction [conn db/db-connection]
        (sql/insert! conn :task
            {:name name})))

(defn create-pomodoro [task-id]
    (sql/with-db-transaction [conn db/db-connection]
        (sql/insert! conn :pomodoro
            {:task_id (Long/valueOf task-id)})))

(defn start-pomodoro [pomodoro-id]
    (sql/with-db-transaction [conn db/db-connection]
        (sql/update! conn :pomodoro
            {:started_date (new java.sql.Timestamp (.getTime (new java.util.Date)))}
            ["pomodoro_id = ? AND started_date IS NULL" (Long/valueOf pomodoro-id)])))

(defn end-pomodoro [pomodoro-id]
    (sql/with-db-transaction [conn db/db-connection]
        (sql/update! conn :pomodoro
            {:ended_date (new java.sql.Timestamp (.getTime (new java.util.Date)))}
            ["pomodoro_id = ? AND started_date IS NOT NULL" (Long/valueOf pomodoro-id)])))