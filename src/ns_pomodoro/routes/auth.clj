(ns ns-pomodoro.routes.auth
    (:require [compojure.core :refer :all]
              [noir.validation :as validation]
              [noir.session :as session]
              [noir.response :as resp]
              [noir.util.crypt :as crypt]
              [ns-pomodoro.views.layout :as layout]
              [ns-pomodoro.models.user :as user]
              [ns-pomodoro.views.util :as util]))

(defn error-map [[error]]
    {:is true :text error})

(defn build-error [field]
    (validation/on-error field error-map))

(defn show-registration [& [username email]]
    (layout/render-layout "register/form" {
        :title "New User" 
        :username username 
        :email email
        ;; TODO Change this to append the field only if there is an error
        :errors {
            :username (build-error :username) 
            :email (build-error :email) 
            :password (build-error :password)}
        :flash (if (validation/errors? :general :username :email :password)
                    (util/flash-danger "Something wrong with the data you entered, please review") {})}))

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

(defn format-error [username ex]
    (cond
        (and (instance? org.postgresql.util.PSQLException ex) (= 0 (.getErrorCode ex)))
            (str "User with that username already exists!")
        :else
            (str "An error has occured while processing the request")))

(defn welcome []
    (layout/render-layout "register/welcome" {
        :flash (util/flash-success "Now you can log in with the username and password you specified" "Welcome!")}))

(defn handle-registration [username email pass pass2]
    (if (valid? username email pass pass2)
        (try
            (user/create-user username email pass2)
            (welcome)
            (catch Exception ex
                (validation/rule false [:username (format-error username ex)])
                (show-registration)))
        (show-registration username email)))

(defn login [username password]
    (if (user/valid-user? username password)
        (let [user (user/get-user username)]
            (session/put! :user {:user-id (:user_id user) :username (:username user) :email (:email user)})
            ;; TODO IDEA Change the greeting randomly
            (util/set-success-flash! "Welcome back!")
            (resp/redirect "/tasks"))
        (do
            (util/set-warning-flash! "There is no user with that username/password")
            (resp/redirect "/"))))

(defn logout []
    (session/clear!)
    (resp/redirect "/"))

(defroutes auth-routes
    (GET "/register" [] (show-registration))
    (POST "/register" [username email password password2] (handle-registration username email password password2))
    (POST "/login" [username password] (login username password))
    (GET "/logout" [] (logout)))