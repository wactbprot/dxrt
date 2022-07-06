(ns dxrt.cli
  ^{:author "Thomas Bock <thomas.bock@ptb.de>"
    :doc "The dxrt cli."}
  (:require [dxrt.db :as db]
            [dxrt.model :as model]
            [dxrt.scheduler :as scd]
            [integrant.core :as ig]))



(def system (atom {}))

(defn config [ids]
  {:db/couch {:prot "http",
              :host "localhost",
              :port 5984,
              :usr (System/getenv "CAL_USR")
              :pwd (System/getenv "CAL_PWD")
              :name "vl_db_work"}
   :doc/mpds {:ids ids
              :db (ig/ref :db/couch)}
   :mpd/model {:docs (ig/ref :doc/mpds)}
   ;;:image/scheduler {:heartbeat 1000 ; ms
   ;;:model (ig/ref :mpd/model)}
  })



;; ________________________________________________________________________
;; init key
;; ________________________________________________________________________
(defmethod ig/init-key :db/couch [_ opts]
  (db/config opts))

(defmethod ig/init-key :doc/mpds [_ {:keys [ids] :as opts}]
  (db/get-mpds opts))

(defmethod ig/init-key :mpd/model [_ {:keys [docs] :as opts}]
  (model/build docs))

#_(defmethod ig/init-key :image/scheduler [_ {:keys [id model] :as opts}]
  (scd/up model opts))


;; ________________________________________________________________________
;; halt key
;; ________________________________________________________________________
(defmethod ig/halt-key! :doc/mpds [_ docs]
  (prn "shed halt"))

;; ________________________________________________________________________
;; playground
;; ________________________________________________________________________
(comment
  (def system (ig/init (config ["mpd-ppc-gas_dosing"
                                "mpd-se3-calib"])))

  (ig/halt! system))

(comment
  (require '[portal.api :as p])
  (def p (p/open))
  (add-tap #'p/submit)

  (tap> system))

