(ns dxrt.model)

(defn struct-def->state-ctrl [v]
  (mapv (fn [{c :Ctrl d :Definition} i]
          {:all-exec-hooks []
           :ctrl (or (keyword c) :ready)
           :state (into [] (flatten
                            (mapv (fn [s j] 
                                    (mapv (fn [p k] 
                                            {:ndx i :sdx j :pdx k :is :ready})
                                          s (range)))
                                  d (range))))})
        v (range)))


(defn up
  [{id :_id  {exch :Exchange cont :Container defis :Definitions :as mp} :Mp}]
  (-> mp
      (dissoc :Task)
      (assoc :Container (mapv
                         (fn [c s]
                           (merge c {:agent (agent s)}))
                         cont (struct-def->state-ctrl cont)))

      (assoc :Definitions (mapv
                           (fn [d s]
                             (merge d {:agent (agent s)}))
                           defis (struct-def->state-ctrl defis)))
      
      (assoc :Documents (agent []))
      (assoc :Exchange (agent exch))))

(defn down [] (shutdown-agents))


