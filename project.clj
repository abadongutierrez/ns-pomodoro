(defproject ns-pomodoro "0.1.0-SNAPSHOT"
    :description "FIXME: write description"
    :url "http://example.com/FIXME"
    :dependencies [[org.clojure/clojure "1.6.0"]
                   [compojure "1.1.6"]
                   [hiccup "1.0.5"]
                   [ring-server "0.3.1"]
                   [org.postgresql/postgresql "9.3-1101-jdbc41"]
                   [org.clojure/java.jdbc "0.3.3"]
                   [lib-noir "0.8.3"]
                   [de.ubercode.clostache/clostache "1.4.0"]
                   [clj-time "0.7.0"]
                   [liberator "0.11.0"]
                   [cheshire "5.3.1"]]
    :source-paths ["src/clojure"]
    :plugins [[lein-ring "0.8.10"]]
    :ring {
        :handler ns-pomodoro.handler/app
        :init ns-pomodoro.handler/init
        :destroy ns-pomodoro.handler/destroy
    }
    :aot :all
    :profiles {
        :production {
            :ring {
                :open-browser? false, 
                :stacktraces? false, 
                :auto-reload? false
            }
        }
        :dev {
            :open-browser? false,
            :dependencies [[ring-mock "0.1.5"]
                           [ring/ring-devel "1.2.1"]]
        }
    })
