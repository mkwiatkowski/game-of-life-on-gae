(ns game-of-life-on-gae.app_servlet
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use game-of-life-on-gae.core)
  (:use [appengine-magic.servlet :only [make-servlet-service-method]]))

(defn -service [this request response]
  ((make-servlet-service-method game-of-life-on-gae-app) this request response))
