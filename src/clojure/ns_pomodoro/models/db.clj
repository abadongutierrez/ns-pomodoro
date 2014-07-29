(ns ns-pomodoro.models.db
    (:import java.sql.DriverManager))

(def db-connection {:classname "org.sqlite.JDBC",
                    :subprotocol "postgresql",
                    :subname "//localhost/ns_pomodoro"
                    :user "rgutierrez"
                    :password ""})