(ns ns-pomodoro.routes.api
  (:require [compojure.core :refer :all]
            [liberator.core :refer [defresource resource request-method-in]]
            [cheshire.core :refer [generate-string parse-string]]
            [cheshire.generate :refer [add-encoder encode-str remove-encoder]]
            [ns-pomodoro.models.task :as tasks]
            [ns-pomodoro.models.work :as work]
            [ns-pomodoro.models.util :as mutil]
            [ns-pomodoro.views.util :as util]
            [clj-time.core :as t]
            [clojure.java.io :as io])
  (:import (java.net URL)))

(add-encoder java.sql.Timestamp
             (fn [c jsonGenerator]
               (.writeString jsonGenerator (str c))))

(add-encoder org.joda.time.LocalDate
             (fn [c jsonGenerator]
               (.writeString jsonGenerator (str c))))

;; convert the body to a reader. Useful for testing in the repl
;; where setting the body to a string is much simpler.
(defn body-as-string [ctx]
    (if-let [body (get-in ctx [:request :body])]
        (condp instance? body
            java.lang.String body
            (slurp (io/reader body)))))

;; For PUT and POST parse the body as json and store in the context
;; under the given key.
(defn parse-json [context key]
    (when (#{:put :post} (get-in context [:request :request-method]))
        (try
            (if-let [body (body-as-string context)]
                (let [data (parse-string body true)]
                     [false {key data}])
                {:message "No body"})
        (catch Exception e
            (.printStackTrace e)
            {:message (format "IOException: %s" (.getMessage e))}))))

;; For PUT and POST check if the content type is json.
(defn check-content-type [ctx content-types]
    (if (#{:put :post} (get-in ctx [:request :request-method]))
        (or
            (some #{(get-in ctx [:request :headers "content-type"])} content-types)
            [false {:message "Unsupported Content-Type"}])
        true))

;; a helper to create a absolute url for the entry with the given id
(defn build-entry-url [request id]
  (URL. (format "%s://%s:%s%s/%s"
                (name (:scheme request))
                (:server-name request)
                (:server-port request)
                (:uri request)
                (str id))))

(defn contains-all-tags? [task tags-to-filter]
    (let [task-tags (:tags task)
          mapping (map #(some (fn [tag] (= % tag)) task-tags) tags-to-filter)]
        (every? #(= true %) mapping)))

;; SIDE-NOTE: You can use #() when you use % inside the function, if not you have to use (fn ...)

(defresource list-tasks
    ;; Allowed method GET /tasks and POST /tasks
    :allowed-methods [:get :post]
    :available-media-types ["application/json"]
    ;; Request must contain Header Content-Type "application/json"
    :known-content-type? #(check-content-type % ["application/json" "application/json; charset=UTF-8"])
    ;; Body must be valid json (TODO and contain required attributes!)
    :malformed? #(parse-json % ::data)
    ;; GET
    :handle-ok 
        (fn [ctx]
            (let [tags-to-filter (get-in ctx [:request :params :filterTags])
                  tasks (tasks/get-not-finished-tasks (util/user-id))
                  filtered-tasks (vec (filter #(= true (contains-all-tags? % tags-to-filter)) tasks))]
                (.println System/out (str tags-to-filter))
                (.println System/out (str filtered-tasks))
                (generate-string {:tasks filtered-tasks :meta {:total (count filtered-tasks)}})))
    ;; POST
    :post!
        #(let [data (::data %)
               ;; because payload is coming as { task: { ... } }
               new-task-name (get-in data [:task :name])
               new-task (tasks/create-task new-task-name (util/user-id))]
            {::id (:task_id new-task)})
    ;; Instead of responding 201 (“Created”) we redirect to GET the new resource
    :post-redirect? true
    ;; This is the location of the new resource (GET /tasks/::id)
    :location #(build-entry-url (get % :request) (get % ::id)))

(defresource entry-task [task-id]
    :allowed-methods [:get :put :delete]
    :available-media-types ["application/json"]
    :known-content-type? #(check-content-type % ["application/json" "application/json; charset=UTF-8"])
    :exists?
        (fn [_]
            (let [task (tasks/get-task task-id)]
                (if-not (nil? task)
                    {::entry task})))
    :existed?
        (fn [_] (nil? (tasks/get-task task-id)))
    :malformed? #(parse-json % ::data)
    :handle-ok #(generate-string {:task (::entry %)})
    ;; DELETE
    :delete!
        (fn [_]
            (do
                (try
                    (tasks/delete-task task-id)
                (catch java.sql.BatchUpdateException e
                    (.println System/out (str "error:" (.getMessage e) ", " (.getNextException e)))
                    (throw e)))
                ;; (204 No Content)
                {::respond-with-entity false}))
    :can-put-to-missing? false
    ;; PUT
    :put!
        #(let [data (::data %)
               task (:task data)
               name (:name task)
               is_done (:is_done task)
               tags (if (nil? (:tags task)) [] (:tags task))]
            (do
                (tasks/remove-all-tags-in-task task-id)
                (tasks/add-tags-to-task tags task-id)
                (if-not (nil? name)
                  (tasks/update-task-name name task-id))
                (if (= true is_done)
                  (tasks/finish-task task-id)))
                (.println System/out (tasks/get-task task-id))
                ;; Reset ::entry in context
                {::entry (tasks/get-task task-id)}
                ;; Include the updated entity in the response
                {::respond-with-entity true})
    ;; Indicates if in the response the entity (::entry) is going to be included
    :respond-with-entity? #(::respond-with-entity %)
    :new?
        (fn [_] (nil? (tasks/get-task task-id)))
    ;; Verify if the user is owner of the task he/she is trying to get
    ;;:authorized?
    ;;    #(let [task (tasks/get-task task-id)]
    ;;        (= (util/user-id) (if-not (nil? task) (:user_id task) -1))))
    )

(defresource list-pomodoros
    ;; Allowed method GET /tasks and POST /tasks
    :allowed-methods [:get :post]
    :available-media-types ["application/json"]
    ;; Request must contain Header Content-Type "application/json"
    :known-content-type? #(check-content-type % ["application/json" "application/json; charset=UTF-8"])
    ;; Body must be valid json (TODO and contain required attributes!)
    :malformed? #(parse-json % ::data)
    ;; GET
    :handle-ok 
        (fn [_]
            (let [tasks (tasks/get-not-finished-tasks (util/user-id))]
                (generate-string {:tasks tasks :meta {:total (count tasks)}})))
    ;; POST
    ; :post!
    ;     #(let [data (::data %)
    ;            ;; because payload is coming as { task: { ... } }
    ;            new-task-name (get-in data [:task :name])
    ;            new-task (tasks/create-task new-task-name (util/user-id))]
    ;         {::id (:task_id new-task)})
    ; ;; Instead of responding 201 (“Created”) we redirect to GET the new resource
    ; :post-redirect? true
    ; ;; This is the location of the new resource (GET /tasks/::id)
    ; :location #(build-entry-url (get % :request) (get % ::id))
    )

; Resource to start a new pomodoro
(defresource start-pomodoro [task-id]
    :allowed-methods [:post]
    :available-media-types ["application/json"]
    :post!
        (fn [context]
            (let [new-pomodoro (tasks/create-pomodoro task-id)]
                {::id (:pomodoro_id new-pomodoro)}))
    :handle-created
        (fn [context]
            (generate-string { ; TODO extract this to a function to create the success message
                    :meta {
                        :error false
                        :message ""
                    }
                    :data {
                        :pomodoro (tasks/get-pomodoro (::id context))
                        :work_time_left (tasks/get-pomodoro-worktime-length)
                        :rest_time_left (tasks/get-pomodoro-resttime-length)
                    }
                })))

; Resource to end a existing pomodoro
(defresource end-pomodoro [task-id pomodoro-id]
    :allowed-methods [:post]
    :available-media-types ["application/json"]
    :post!
        (fn [_]
            (tasks/end-pomodoro pomodoro-id))
    :handle-created
        (fn [context]
            (generate-string { ; TODO extract this to a function to create the success message
                    :meta {
                        :error false
                        :message ""
                    }
                    :data {}
                })))

(defresource list-work
    :allowed-methods [:get]
    :available-media-types ["application/json"]
    :known-content-type? #(check-content-type % ["application/json" "application/json; charset=UTF-8"])
    ;; GET
    :handle-ok 
        (fn [ctx]
            (let [offset-param (get-in ctx [:request :params :offset])
                  offset (if (nil? offset-param) 0 (mutil/get-int offset-param)) 
                  work (work/get-week-pomodoros offset (util/user-id))]
                (generate-string {:work work :meta {:total (count work)}}))))

(defresource play-with-params
    :allowed-methods [:get]
    :available-media-types ["application/json"]
    :known-content-type? #(check-content-type % ["application/json" "application/json; charset=UTF-8"])
    ;; GET
    :handle-ok 
        (fn [ctx] 
            (let [params (get-in ctx [:request :params])
                  specific-param (get-in params [:name])]
                (generate-string {:params params :specific-param specific-param}))))

(defroutes api-routes
  (ANY "/api/v1/tasks" [] list-tasks)
  (ANY "/api/v1/tasks/:task-id" [task-id] (entry-task task-id))
  (ANY "/api/v1/tasks/:task-id/pomodoros/start" [task-id] (start-pomodoro task-id))
  (ANY "/api/v1/tasks/:task-id/pomodoros/:pomodoro-id/end" [task-id pomodoro-id] (end-pomodoro task-id pomodoro-id))
  (ANY "/api/v1/work" [] list-work)
  (ANY "/api/play/params" [] play-with-params))

