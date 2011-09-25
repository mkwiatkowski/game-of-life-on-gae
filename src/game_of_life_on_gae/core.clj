(ns game-of-life-on-gae.core
  (:use compojure.core)
  (:use [clojure.string :only (join)])
  (:require [appengine-magic.core :as ae]
            [appengine-magic.services.datastore :as ds]
            [compojure.route :as route]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn make-board
  ([width height val-func]
     (into {:width width :height height}
           (for [x (range 1 (+ width 1)) y (range 1 (+ height 1))] [[x y] (val-func x y)])))
  ([width height]
     (make-board width height (constantly false))))

(defn random-board [width height]
  (make-board width height (fn [x y] (rand-nth [true false]))))

(defn cell-at [board x y]
  (board [x y]))

(defn neighbours-alive [board x y]
  (count
   (filter
    true?
    (for [xp [(- x 1) x (+ x 1)]
          yp [(- y 1) y (+ y 1)]
          :when (not (and (= x xp) (= y yp)))]
      (cell-at board xp yp)))))

(defn new-cell-state [board x y]
  (let [neighbours (neighbours-alive board x y)]
    (cond
     (<= neighbours 1) false
     (= neighbours 2) (cell-at board x y)
     (= neighbours 3) true
     (>= neighbours 4) false)))

(defn new-board-state [board]
  (make-board
   (board :width)
   (board :height)
   (fn [x y] (new-cell-state board x y))))

(defn board-as-string [board]
  (join
   (for [y (range 1 (+ (board :height) 1))
         x (range 1 (+ (board :width) 1))]
     (join
      [(if (cell-at board x y) "#" ".")
       (if (= x (board :width)) "\n" "")]))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def *default-board-width* 200)
(def *default-board-height* 50)

(ds/defentity Board [^:key id, ^:clj value])

(defn get-board [id]
  (:value (ds/retrieve Board id)))

(defn board-exists? [id]
  (not (nil? (get-board id))))

(defn save-board! [id board]
  (ds/save! (Board. id board)))

(defn create-new-random-board! [id]
  (save-board! id (random-board *default-board-width* *default-board-height*)))

;; Return a map of string keys to game boards.
(defn get-all-boards []
  (apply hash-map
         (mapcat #(vector (:id %) (:value %))
                 (ds/query :kind Board))))

(defn do-one-step! [id]
  (save-board! id (new-board-state (get-board id))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defroutes game-of-life-on-gae-app-handler
  (GET "/" []
       (join ["<html><body><ul>"
              (join (map (fn [k] (join ["<li><a href='" k "'>" k "</a></li>"]))
                         (keys (get-all-boards))))
              "</ul></body></html>"]))
  (GET "/:id" [id]
       (if (not (board-exists? id))
         (create-new-random-board! id)
         (do-one-step! id))
       (join ["<html><head><meta http-equiv='refresh' content='3' /></head><body><pre>"
              (board-as-string (get-board id))
              "</pre></body></html>"]))
  (route/not-found
   "<h1>Page not found</h1>"))

(ae/def-appengine-app game-of-life-on-gae-app #'game-of-life-on-gae-app-handler)
