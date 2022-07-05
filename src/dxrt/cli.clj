(ns dxrt.cli
  ^{:author "Thomas Bock <thomas.bock@ptb.de>"
    :doc "The dxrt cli."}
  (:require [dxrt.db :as db]
            [dxrt.model :as model]
            [dxrt.scheduler :as scd]
            [integrant.core :as ig]))



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
  (model/build doc))

(defmethod ig/init-key :image/scheduler [_ {:keys [id model] :as opts}]
  (scd/up model opts))


;; ________________________________________________________________________
;; halt key
;; ________________________________________________________________________
(defmethod ig/halt-key! :image/scheduler [_ impl]
  (prn "shed halt")
    (prn (class impl)))

(defn up [id]
  (swap! system assoc id (ig/init (config id)))
  (prn (keys @system)))


(defn down [id]
  (ig/halt! (get @system id))
  (swap! system dissoc  id)
  (prn (keys @system)))


;; ________________________________________________________________________
;; playground
;; ________________________________________________________________________
(comment
  (up "mpd-ppc-gas_dosing")
  (up "mpd-se3-calib"))

(comment
  (require '[portal.api :as p])
  (def p (p/open))
  (add-tap #'p/submit)
  (tap> system))

(comment
  (down "mpd-ppc-gas_dosing")
  (down "mpd-se3-calib"))
