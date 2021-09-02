(ns api-test
  (:require [clojure.test :refer [is deftest]]
            [clojure.data.json :as json]
            [api.core :as api]))

(deftest api-test
  (is (= (api/app {:request-method :post
                   :uri "/calc"
                   :content-type "application/json"
                   :body {:expression "10 + 5"}})
         {:status 200,
          :headers {"Content-Type" "application/json"},
          :body "{\"result\":15}"}))

  (is (let [history (api/app {:request-method :get
                              :uri "/history"})]
        (= (-> history :body json/read-str vals first)
           15))))
