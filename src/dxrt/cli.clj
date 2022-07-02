(ns dxrt.cli
  ^{:author "Thomas Bock <thomas.bock@ptb.de>"
    :doc "The dxrt cli."}
  (:require [libcdb.configure :as db]
           [integrant.core :as ig]))


(def system (atom {}))

(defn config [system id]
  {:mpd/doc {:id id
             :db "vl_db"}})

(defmethod ig/init-key :mpd/doc [_ {:keys [id db] :as opts}]
  {:mpd id :db db})

(defmethod ig/halt-key! :mpd/doc [_ image]
  ;; stop agents
  (prn image))

(defn up [id]
  (swap! system assoc id (ig/init (config id) [:mpd/doc])))

(defn down [id] (ig/halt! (-> @system id)))
