(ns ns-pomodoro.models.task
    (:require [clojure.java.jdbc :as sql]
              [clj-time.format :as f]
              [ns-pomodoro.models.db :as db]
              [ns-pomodoro.models.util :as util]))

(defn read-tasks [user-id]
    (sql/with-db-transaction [conn db/db-connection]
        (sql/query conn 
            ["SELECT * FROM task WHERE user_id = ? ORDER BY entered_date DESC" (util/get-long user-id)])))

(defn get-task [task-id]
    (first
        (sql/with-db-transaction [conn db/db-connection]
            (sql/query conn 
                ["SELECT * FROM task WHERE task_id = ?" (util/get-long task-id)]))))

(defn create-task [name user-id]
    (sql/with-db-transaction [conn db/db-connection]
        (sql/insert! conn :task
            {:name name :user_id user-id})))

(defn get-pomodoro [pomodoro-id]
    (first
        (sql/with-db-transaction [conn db/db-connection]
            (sql/query conn 
                ["SELECT * FROM pomodoro WHERE pomodoro_id = ?"
                    (util/get-long pomodoro-id)]))))

(defn create-pomodoro [task-id]
    (first
        (sql/with-db-transaction [conn db/db-connection]
            (sql/insert! conn :pomodoro
                {:task_id (util/get-long task-id)}))))

(defn end-pomodoro [pomodoro-id]
    (sql/with-db-transaction [conn db/db-connection]
        (sql/update! conn :pomodoro
            {:ended_date (new java.sql.Timestamp (.getTime (new java.util.Date)))}
            ["pomodoro_id = ? AND started_date IS NOT NULL" (util/get-long pomodoro-id)])))

(defn get-completed-pomodoros [task-id]
    (sql/with-db-transaction [conn db/db-connection]
        (sql/query conn
            ["SELECT * FROM pomodoro WHERE task_id = ? AND started_date IS NOT NULL AND ended_date IS NOT NULL" (util/get-long task-id)])))

(def custom-formatter (f/formatter "yyyy-MM-dd"))

; date : org.joda.time.LocalDate
(defn get-completed-pomodoros-per-day [date]
    (sql/with-db-transaction [conn db/db-connection]
        (sql/query conn
            [(str "SELECT * FROM pomodoro WHERE started_date BETWEEN '"
                (f/unparse custom-formatter (.toDateTimeAtStartOfDay date)) " 00:00:00' AND '"
                (f/unparse custom-formatter (.toDateTimeAtStartOfDay date)) " 23:59:59' AND "
                "ended_date IS NOT NULL")])))

; date : org.joda.time.LocalDate
(defn get-pomodoros-per-day [date]
    (sql/with-db-transaction [conn db/db-connection]
        (let [pomodoros (sql/query conn
                [(str "SELECT * FROM pomodoro WHERE started_date BETWEEN '"
                    (f/unparse custom-formatter (.toDateTimeAtStartOfDay date)) " 00:00:00' AND '"
                    (f/unparse custom-formatter (.toDateTimeAtStartOfDay date)) " 23:59:59'")])]
            (vec (map #(assoc % :task (get-task (:task_id %))) pomodoros)))))

(defn get-task-with-pomodoros [task-id]
    (let [task (get-task task-id) pomodoros (get-completed-pomodoros task-id)]
        (assoc task :pomodoros pomodoros)))