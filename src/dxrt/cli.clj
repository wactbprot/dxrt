(ns dxrt.cli
  ^{:author "Thomas Bock <thomas.bock@ptb.de>"
    :doc "The dxrt cli."}
  (:require [dxrt.db :as db]
            [integrant.core :as ig]))


(def image (atom {}))


(defn config [id]
  {:db/couch {:prot "http",
              :host "localhost",
              :port 5984,
              :usr (System/getenv "CAL_USR")
              :pwd (System/getenv "CAL_PWD")
              :id id
              :name "vl_db_work"}
   :doc/mpd {:id id :db (ig/ref :db/couch)}
   :image/container {:doc (ig/ref :doc/mpd)}})

;; ________________________________________________________________________
;; init key
;; ________________________________________________________________________
(defmethod ig/init-key :db/couch [_ opts]
  (db/config opts))

(defmethod ig/init-key :doc/mpd [_ {:keys [id] :as opts}]
  (db/get-mpd opts))

(defmethod ig/init-key :image/container [_ {:keys [doc] :as opts}]
  (prn doc))



;; ________________________________________________________________________
;; halt key
;; ________________________________________________________________________
(defmethod ig/halt-key! :doc/mpd [_ opts]
  ;; stop agents
  (prn image))


(def system (ig/init (config "mpd-se3-calib"))) 
