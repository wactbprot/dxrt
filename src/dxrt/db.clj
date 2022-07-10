(ns dxrt.db
  (:require [libcdb.core :as db]
            [libcdb.configure :as cf]))


(defn config [opts]
  (cf/config opts))

(defn get-mpd [{:keys [id db]}]
  (db/get-doc id db))

(defn get-mpds [{:keys [ids] :as opts}]
  (mapv #(get-mpd (assoc opts :id %)) ids))

(defn get-task [taskname {:keys [task-view task-design db] :as opts}]
      (db/get-view  (assoc db :key taskname :view task-view :design task-design)))
