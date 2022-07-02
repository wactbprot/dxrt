(ns dxrt.cli
  ^{:author "Thomas Bock <thomas.bock@ptb.de>"
    :doc "The dxrt cli."}
  (:require [dxrt.db :as db]
            [dxrt.model :as model]
            [integrant.core :as ig]))


(def images (atom {}))


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
   :image/model {:doc (ig/ref :doc/mpd)}})

;; ________________________________________________________________________
;; init key
;; ________________________________________________________________________
(defmethod ig/init-key :db/couch [_ opts]
  (db/config opts))

(defmethod ig/init-key :doc/mpd [_ {:keys [id] :as opts}]
  (db/get-mpd opts))

(defmethod ig/init-key :image/model [_ {:keys [doc] :as opts}]
  (model/up images doc opts))



;; ________________________________________________________________________
;; halt key
;; ________________________________________________________________________
(defmethod ig/halt-key! :doc/mpd [_ opts]
  ;; stop agents
  (prn image))


(def system (ig/init (config "mpd-se3-calib"))) 
