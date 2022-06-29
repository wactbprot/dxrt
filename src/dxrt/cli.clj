(ns dxrt.cli
  ^{:author "Thomas Bock <thomas.bock@ptb.de>"
    :doc "The dxrt cli."}
  (:require [libcdb.core :as db]
            [integrant.core :as ig]))


(def system (atom {}))

(defn  config [id image]

  {:db/conn {:prot "http"
             :host "localhost"
             :port 5984
             :name "vl_db"}

   :db/mpd {:conn (ig/ref :db/conn)
            :design "dbmp"
            :view "mpdocs"}

   :db/tasks {:conn (ig/ref :db/conn)
              :design "dbmp"
              :view "tasks"}

   :image/container {:doc (ig/ref :mpd/doc) :image image}
   :image/definition {:doc (ig/ref :mpd/doc) :image image}
   :image/state {:doc (ig/ref :mpd/doc) :image image}
   :image/sheduler {:image (ig/ref :image/state)}})

;; ________________________________________________________________________
;; init key
;; ________________________________________________________________________
(defmethod ig/init-key :mpd/doc [_ {:keys [id db] :as opts}]
  {:mpd id :db db})

(defmethod ig/init-key :image/definition [_ {:keys [doc image] :as opts}]
  {:mpd id :db db})

(defmethod ig/init-key :image/container [_ {:keys [doc image] :as opts}]
  {:mpd id :db db})

(defmethod ig/init-key :image/state [_ {:keys [doc image] :as opts}]
  {:mpd id :db db})

(defmethod ig/init-key :image/sheduler [_ {:keys [doc image] :as opts}]
  {:mpd id :db db})


;; ________________________________________________________________________
;; halt key
;; ________________________________________________________________________
(defmethod ig/halt-key! :mpd/doc [_ image]
  ;; stop agents
  (prn image))

(defmethod ig/halt-key! :image/definition [_ {:keys [doc image] :as opts}]
  (prn image))

(defmethod ig/halt-key! :image/container [_ {:keys [doc image] :as opts}]
  (prn image))

(defmethod ig/halt-key! :image/state [_ {:keys [doc image] :as opts}]
  (prn image))

(defmethod ig/halt-key! :image/sheduler [_ {:keys [doc image] :as opts}]
  (prn image))


;; ________________________________________________________________________
;; id up/down
;; ________________________________________________________________________
(defn up [id]
  (swap! system assoc id (ig/init (config id) [:mpd/doc])))

(defn down [id] (ig/halt! (-> @system id)))
