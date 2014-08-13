(ns ns-pomodoro.models.task
    (:require [clojure.java.jdbc :as sql]
              [clj-time.format :as f]
              [clj-time.core :as t]
              [ns-pomodoro.models.db :as db]
              [ns-pomodoro.models.util :as util]))

(defn get-pomodoros [task-id]
    (sql/with-db-transaction [conn db/db-connection]
        (sql/query conn
            ["SELECT * FROM pomodoro WHERE task_id = ?" (util/get-long task-id)])))

(defn get-pomodoros-id [task-id]
    (map #(:pomodoro_id %) (get-pomodoros task-id)))

(defn assoc-pomodoros-to-task [task]
    (let [pomodoros (get-pomodoros-id (:task_id task))]
        (assoc task :pomodoros pomodoros :total_pomodoros (count pomodoros))))

(defn get-not-finished-tasks [user-id]
    (let [tasks (sql/with-db-transaction [conn db/db-connection]
                    (sql/query conn 
                    ["SELECT * FROM task WHERE user_id = ? AND is_done = false ORDER BY entered_date DESC" (util/get-long user-id)]))]
        (map #(assoc-pomodoros-to-task %) tasks)))

(defn get-task [task-id]
    (let [task (first
                    (sql/with-db-transaction [conn db/db-connection]
                        (sql/query conn 
                        ["SELECT * FROM task WHERE task_id = ?" (util/get-long task-id)])))]
        (assoc-pomodoros-to-task task)))

(defn create-task [name user-id]
    (first
        (sql/with-db-transaction [conn db/db-connection]
            (sql/insert! conn :task
                {:name name :user_id user-id}))))

(defn delete-task
    "Delete a task and returns the total rows deleted"
    [task-id]
    (first
        (sql/with-db-transaction [conn db/db-connection]
            (sql/delete! conn :task
                ["task_id = ?" (util/get-long task-id)]))))

(defn finish-task [task-id]
    "Mask as done the task returning the total rows updated"
    (first
        (sql/with-db-transaction [conn db/db-connection]
            (sql/update! conn :task
                {:done_date (new java.sql.Timestamp (.getTime (new java.util.Date))) :is_done true}
                ["task_id = ?" (util/get-long task-id)]))))

(defn update-task-name [name task-id]
    "Update the task name and returns the total rows updated"
    (first
        (sql/with-db-transaction [conn db/db-connection]
            (sql/update! conn :task
                {:name name}
                ["task_id = ?" (util/get-long task-id)]))))

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

(defn between-date-str [date]
    (str "'"
        (f/unparse custom-formatter (.toDateTimeAtStartOfDay date)) " 00:00:00' AND '"
        (f/unparse custom-formatter (.toDateTimeAtStartOfDay date)) " 23:59:59'"))

; date : org.joda.time.LocalDate
(defn get-completed-pomodoros-per-day [date]
    (sql/with-db-transaction [conn db/db-connection]
        (sql/query conn
            [(str "SELECT * FROM pomodoro WHERE started_date BETWEEN "
                (between-date-str date)
                " AND ended_date IS NOT NULL")])))

; date : org.joda.time.LocalDate
(defn get-pomodoros-per-day [date user-id]
    (sql/with-db-transaction [conn db/db-connection]
        (let [pomodoros (sql/query conn
                [(str "SELECT pomodoro.* FROM pomodoro AS pomodoro"
                      " INNER JOIN task AS task ON (task.task_id = pomodoro.task_id)" 
                      " WHERE pomodoro.started_date BETWEEN "
                      (between-date-str date)
                      " AND task.user_id = ?") user-id])]
            (vec (map #(assoc % :task (get-task (:task_id %))) pomodoros)))))

(defn get-task-with-pomodoros [task-id]
    (let [task (get-task task-id) pomodoros (get-completed-pomodoros task-id)]
        (assoc task :pomodoros pomodoros :total_pomodoros (count pomodoros))))

(defn count-todays-pomodoros
    "Counts today's pomodoros" 
    []
    (let [pomodoros (get-completed-pomodoros-per-day (t/today))]
        (count pomodoros)))

(defn get-pomodoro-worktime-length
    "Returns the length of the working time for a Pomodoro"
    []
    (* 60 25))

(defn get-pomodoro-resttime-length
    "Returns the length of the resting time for a Pomodoro. Every 4 pomodoros the resting time is longer"
    []
    (let [count (count-todays-pomodoros)
          ; if count-pomodoros is 0 is because there are no pomodoros, so the resting time cannot be of 15 minutes
          total (if (= 0 count) 1 count)]
        (if (= 0 (mod total 4))
            (* 60 15)
            (* 60 5))))

(defn get-tag-with-name [name]
    (first
        (sql/with-db-transaction [conn db/db-connection]
            (sql/query conn
                ["SELECT * FROM tag WHERE name ILIKE ?" name]))))

(defn create-tag [name]
    (first
        (sql/with-db-transaction [conn db/db-connection]
            (sql/insert! conn :tag {:name name}))))

(defn get-or-create-tag
    "Search for the tag name ignoring case and either return the existing one or create a new one"
    [name]
    (let [tag (get-tag-with-name name)]
        (if (nil? tag) (create-tag name) tag)))

(defn get-tags-for-task [task-id]
    (sql/with-db-transaction [conn db/db-connection]
            (sql/query conn
                [(str "SELECT tag.name FROM tag "
                      "INNER JOIN task_tag ON (task_tag.tag_id = tag.tag_id) "
                      "WHERE task_tag.task_id = ?") (util/get-long task-id)])))

