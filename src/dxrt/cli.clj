(ns dxrt.cli
  ^{:author "Thomas Bock <thomas.bock@ptb.de>"
    :doc "The dxrt cli."}
  (:require [dxrt.db :as db]
            [dxrt.model :as model]
            [dxrt.scheduler :as scd]
            [integrant.core :as ig]))


(def images (atom {}))
(def system (atom {}))

(defn config [id]
  {:db/couch {:prot "http",
              :host "localhost",
              :port 5984,
              :usr (System/getenv "CAL_USR")
              :pwd (System/getenv "CAL_PWD")
              :id id
              :name "vl_db_work"}
   :doc/mpd {:id id
             :db (ig/ref :db/couch)}
   :mpd/model {:doc (ig/ref :doc/mpd)}
   :image/scheduler {:id id
                     :heartbeat 1000 ; ms
                     :model (ig/ref :mpd/model)}})

;; ________________________________________________________________________
;; init key
;; ________________________________________________________________________
(defmethod ig/init-key :db/couch [_ opts]
  (db/config opts))

(defmethod ig/init-key :doc/mpd [_ {:keys [id] :as opts}]
  (db/get-mpd opts))

(defmethod ig/init-key :mpd/model [_ {:keys [doc] :as opts}]
  (model/up doc))

(defmethod ig/init-key :image/scheduler [_ {:keys [id model] :as opts}]
  (swap! images assoc id (scd/up model)))



;; ________________________________________________________________________
;; halt key
;; ________________________________________________________________________
(defmethod ig/halt-key! :doc/mpd [_ opts]
  (prn images))


(defn up [id]
  (swap! system assoc id (ig/init (config id))))


(defn down [id]
  (ig/halt! (get @system id))
  (swap! dissoc system id))
