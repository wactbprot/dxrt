(ns dxrt.system
  ^{:author "Thomas Bock <thomas.bock@ptb.de>"
    :doc "The dxrt cli."}
  (:require [dxrt.db :as db]
            [dxrt.model :as model]
            [dxrt.core :as dx]
            [integrant.core :as ig]))

(defn config [ids]
  {:db/couch {:prot "http",
              :host "localhost",
              :port 5984,
              :usr (System/getenv "CAL_USR")
              :pwd (System/getenv "CAL_PWD")
              :name "vl_db_work"
              :view "tasks"
              :design "dbmp"
}
   :doc/mpds {:ids ids
              :db (ig/ref :db/couch)}
   :mpd/model {:docs (ig/ref :doc/mpds)}
   :model/core {:db  (ig/ref :db/couch)
                :launchshift 200 ; ms
                :heartbeat 1000 ; ms
                :model (ig/ref :mpd/model)}})


;; ________________________________________________________________________
;; init key
;; ________________________________________________________________________
(defmethod ig/init-key :db/couch [_ opts]
  (db/config opts))

(defmethod ig/init-key :doc/mpds [_ {:keys [ids] :as opts}]
  (db/get-mpds opts))

(defmethod ig/init-key :mpd/model [_ {:keys [docs] :as opts}]
  (model/build docs))

(defmethod ig/init-key :model/core [_  sys]
  (dx/start sys))

;; ________________________________________________________________________
;; halt key
;; ________________________________________________________________________
(defmethod ig/halt-key! :model/core [_ model]
  (dx/stop model))

;; ________________________________________________________________________
;; start/stop image
;; ________________________________________________________________________
(def system (atom nil))

(defn start [ids]
  (keys (reset! system (ig/init (config ids)))))

(defn stop []
  (ig/halt! @system)
  (reset! system {}))

;; ________________________________________________________________________
;; playground
;; ________________________________________________________________________
(comment
  
  (def sys (ig/init (config ["mpd-ppc-gas_dosing"
                                "mpd-se3-calib"])))

  (ig/halt! sys))

