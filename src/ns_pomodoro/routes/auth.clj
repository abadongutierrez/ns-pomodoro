(ns ns-pomodoro.routes.auth
    (:require [compojure.core :refer :all]
              [noir.validation :as validation]
              [noir.session :as session]
              [noir.response :as resp]
              [noir.util.crypt :as crypt]
              [ns-pomodoro.views.layout :as layout]))

(defn error-map [[error]]
    {:is true :text error})

(defn build-error [field]
    (validation/on-error field error-map))

(defn show-registration [& [username email]]
    (layout/render-layout "register/form" {
        :title "New User" 
        :username username 
        :email email
        :errors {
            :username (build-error :username) 
            :email (build-error :email) 
            :password (build-error :password)}}))

(defn valid? [username email pass pass2]
    (validation/rule (validation/has-value? username)
        [:username "Username is required"])
    (validation/rule (validation/min-length? username 8)
        [:username "Username should be at least 8 characters long"])
    (validation/rule (validation/has-value? email)
        [:email "Email is required"])
    (validation/rule (validation/is-email? email)
        [:email "Email is not a valid email"])
    (validation/rule (validation/has-value? pass)
        [:password "Password is obviously required"])
    (validation/rule (validation/min-length? pass 10)
        [:password "Password should be at least 10 characters long"])
    (validation/rule (= pass pass2)
        [:password "Passwords do not match"])
    (not (validation/errors? :username :email :password)))

(defn handle-registration [username email pass pass2]
    (if (valid? username email pass pass2)
        (do
            (session/put! :user username)
            (resp/redirect "/tasks"))
        (show-registration username email)))

(defroutes auth-routes
    (GET "/register" [] (show-registration))
    (POST "/register" [username email password password2] (handle-registration username email password password2)))