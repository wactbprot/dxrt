(ns dxrt.db
  (:require [libcdb.core :as db]))

(defn get-mpd [{:keys [id] :as opts}]
  (prn id)
  (prn opts)
  (db/get-doc id opts))
