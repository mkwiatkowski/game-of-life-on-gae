(ns game-of-life-on-gae.core
  (:require [appengine-magic.core :as ae]))

(defn game-of-life-on-gae-app-handler [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello, world!"})

(ae/def-appengine-app game-of-life-on-gae-app #'game-of-life-on-gae-app-handler)
