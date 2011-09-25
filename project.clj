(defproject game-of-life-on-gae "1.0"
  :description "Game of Life on Google App Engine written in Clojure"
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [compojure "0.6.5"]]
  :dev-dependencies [[appengine-magic "0.4.5"]]
  :ring {:handler game-of-life-on-gae.core/app})
