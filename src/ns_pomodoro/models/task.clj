(ns ns-pomodoro.models.task
    (:require [clojure.java.jdbc :as sql]
              [ns-pomodoro.models.db :as db]))

; Util method to force value to be Long
(defn get-long [value]
    (if (instance? Long value) value (Long/valueOf (str value))))

(defn read-tasks []
    (sql/with-db-transaction [conn db/db-connection]
        (sql/query conn 
            ["SELECT * FROM task ORDER BY entered_date DESC"])))

(defn get-task [task-id]
    (first
        (sql/with-db-transaction [conn db/db-connection]
            (sql/query conn 
                ["SELECT * FROM task WHERE task_id = ?" (get-long task-id)]))))

(defn create-task [name]
    (sql/with-db-transaction [conn db/db-connection]
        (sql/insert! conn :task
            {:name name})))

(defn get-pomodoro [pomodoro-id]
    (first
        (sql/with-db-transaction [conn db/db-connection]
            (sql/query conn 
                ["SELECT * FROM pomodoro WHERE pomodoro_id = ?"
                    (get-long pomodoro-id)]))))

(defn create-pomodoro [task-id]
    (first
        (sql/with-db-transaction [conn db/db-connection]
            (sql/insert! conn :pomodoro
                {:task_id (get-long task-id)}))))

(defn end-pomodoro [pomodoro-id]
    (sql/with-db-transaction [conn db/db-connection]
        (sql/update! conn :pomodoro
            {:ended_date (new java.sql.Timestamp (.getTime (new java.util.Date)))}
            ["pomodoro_id = ? AND started_date IS NOT NULL" (get-long pomodoro-id)])))

(defn get-completed-pomodoros [task-id]
    (sql/with-db-transaction [conn db/db-connection]
        (sql/query conn
            ["SELECT * FROM pomodoro WHERE task_id = ? AND started_date IS NOT NULL AND ended_date IS NOT NULL" (get-long task-id)])))

(defn get-task-with-pomodoros [task-id]
    (let [task (get-task task-id) pomodoros (get-completed-pomodoros task-id)]
        (assoc task :pomodoros pomodoros)))