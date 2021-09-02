(ns api.core
  (:require [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-body]]
            [ring.util.json-response :refer [json-response]]
            [compojure.core :refer [defroutes POST GET]]
            [ring.adapter.jetty :as jetty]
            [api.calculator :as calculator]))

(def history (atom (sorted-map)))
(defn now [] (System/currentTimeMillis))

(defn append-history
  [entry]
  (swap! history assoc (now) entry)
  entry)

(defroutes app
  (POST "/calc" {body :body}
        (->> body
             :expression
             calculator/calculate
             append-history
             (hash-map :result)
             json-response))
  (GET "/history" []
        (->> @history
             json-response))

  (route/resources "/")
  (route/not-found "Not Found"))

(defn run-server [{port :port}]
  (-> app
      (wrap-json-body {:keywords? true})
      (jetty/run-jetty {:port (or port 9500)})))
