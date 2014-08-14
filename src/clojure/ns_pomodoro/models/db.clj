(ns ns-pomodoro.models.db
    (:import java.sql.DriverManager)
    (:require [environ.core :refer [env]]))

(def db-connection {:classname "org.sqlite.JDBC",
                    :subprotocol "postgresql",
                    :subname (env :db-url)
                    :user "rgutierrez"
                    :password ""})