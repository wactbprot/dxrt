(ns dxrt.cli
  ^{:author "Thomas Bock <thomas.bock@ptb.de>"
    :doc "The dxrt cli."}
  (:require [dxrt.db :as db]
            [integrant.core :as ig]))


(def image (atom {}))


(defn config [image id]
  {:mpd/db {:prot "http",
            :host "localhost",
            :port 5984,
            :usr (System/getenv "CAL_USR")
            :pwd (System/getenv "CAL_PWD")
            :opt {:query-params {},
                  :pool {:threads 1, :default-per-route 1}}
            :id id
            :name "vl_db"}
   :image/container {:doc (ig/ref :mpd/db) :image image}})

;; ________________________________________________________________________
;; init key
;; ________________________________________________________________________
(defmethod ig/init-key :mpd/db [_ opts]
  (db/get-mpd opts))

(defmethod ig/init-key :image/container [_ {:keys [doc] :as opts}]
  (prn doc))



;; ________________________________________________________________________
;; halt key
;; ________________________________________________________________________
(defmethod ig/halt-key! :mpd/doc [_ image]
  ;; stop agents
  (prn image))


(def system (ig/init (config image "mpd-se3-calib"))) 
