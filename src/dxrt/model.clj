(ns dxrt.model)

(defn flattenv [v] (into [] (flatten v)))

(defn struct->struct-model
  "Whats new:
  
  * `all-exec-hooks` container of functions to invoke if all states
  are `executed` and `:ctrl` is set back to `:ready` or `mon`
  * only porocess state if not already `:processed` which means: a
  set-executed or set-error fn also should make `:processed` false" 
  [v]
  (mapv (fn [{:keys [Ctrl Definition Title Description Element]} i]
          {:title Title  :elem Element :descr Description
           :proc (agent {:all-exec-hooks []
                         :processed true ; run on start or not
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

(defn build
  "Note:

  since this `build` model-fn simply returns a `map` it has no
  meaningful takedown function.  Why is this stated:
  * the takedown fn is **not** `(shutdown-agents)`
  * renamed from `up` to `build` because there is no `down`
  "
  [{id :_id  {:keys [Exchange Container Definitions] :as mp} :Mp}]
  (-> mp
      (assoc :id (keyword id))
      (assoc :Container (struct->struct-model Container))
      (assoc :Definitions (struct->struct-model Definitions))
      (assoc :Documents (agent []))
      (assoc :Exchange (agent Exchange))
      (dissoc :Task)))


