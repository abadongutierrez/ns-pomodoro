(ns ns-pomodoro.models.user
    (:require [clojure.java.jdbc :as sql]
              [ns-pomodoro.models.db :as db]
              [ns-pomodoro.models.util :as util]))

(defn create-user [username email password]
    (try
        (sql/with-db-transaction [conn db/db-connection]
          (sql/execute! conn
              [(str "INSERT INTO nsp_user(username, email, password)"
                  " VALUES(?, ?, crypt(?, gen_salt('bf')))") username email password]))
        ; execute! throws java.sql.BatchUpdateException and since we are executing just
        ; one insert we just get the next exception
        (catch java.sql.BatchUpdateException ex
            (throw (.getNextException ex)))))

(defn get-user [username]
    (first
        (sql/with-db-transaction [conn db/db-connection]
            (sql/query conn
                ["SELECT * FROM nsp_user WHERE username = ?" username]))))

(defn valid-user? [username password]
    (let [total (first 
        (sql/with-db-transaction [conn db/db-connection]
            (sql/query conn
                [(str "SELECT count(*) as total"
                      " FROM nsp_user" 
                      " WHERE username = ?"
                      " AND password is NOT NULL"
                      " AND password = crypt(?, password)") username password])))]
        (if (= (:total total) 1) true false)))