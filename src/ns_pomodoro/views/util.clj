(ns ns-pomodoro.views.util
    (:require [noir.session :as session]))

(defn flash-danger [msg & [strong-msg]]
    {:danger {:message {:strong strong-msg :text msg}}})

(defn flash-success [msg & [strong-msg]]
    {:success {:message {:strong strong-msg :text msg}}})

(defn flash-info [msg & [strong-msg]]
    {:info {:message {:strong strong-msg :text msg}}})

(defn flash-warning [msg & [strong-msg]]
    {:warning {:message {:strong strong-msg :text msg}}})

(defn get-flash! []
    (let [flash (session/get :flash) trash (session/remove! :flash)]
        (if (= nil flash) {}
            (cond
                (= :success (:type flash)) (flash-success (:message flash))
                (= :info (:type flash)) (flash-info (:message flash))
                (= :warning (:type flash)) (flash-warning (:message flash))
                :else (flash-danger (:message flash))))))

(defn set-flash! [type message]
    (session/put! :flash {:type type :message message}))

(defn set-warning-flash! [message]
    (set-flash! :warning message))

(defn set-success-flash! [message]
    (set-flash! :success message))

(defn user-id []
    (:user-id (session/get :user)))

(defn username []
    (:username (session/get :user)))