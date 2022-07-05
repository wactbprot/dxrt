(ns dxrt.model)

(defn flattenv [v] (into [] (flatten v)))

(defn struct->struct-model [v]
  (mapv (fn [{:keys [Ctrl Definition Title Description Element]} i]
          {:title Title
           :elem Element
           :descr Description
           :agent (agent {:all-exec-hooks []
                          :ctrl (or (keyword Ctrl) :ready)
                          :state (flattenv
                                  (mapv (fn [s j] 
                                          (mapv (fn [t k] 
                                                  {:ndx i :sdx j :pdx k
                                                   :is :ready
                                                   :task t})
                                                s (range)))
                                        Definition (range)))})})
        v (range)))

(defn up
  [{id :_id  {exch :Exchange cont :Container defis :Definitions :as mp} :Mp}]
  (-> mp
      (assoc :id (keyword id))
      (dissoc :Task)
      (assoc :Container (struct->struct-model cont))
      (assoc :Definitions (struct->struct-model defis))
      (assoc :Documents (agent []))
      (assoc :Exchange (agent exch))))

(defn down [] (shutdown-agents))


