(ns dxrt.model)

(defn flattenv [v] (into [] (flatten v)))

(defn struct->struct-model
  "Whats new:
  
  * `all-exec-hooks` container of functions to invoke if all states
  are `executed` and `:ctrl` is set back to `:ready` or `mon`
  * only porocess state if not already `:processed` which means: a
  set-executed or set-error fn also should make `:processed` false
  * state agent is now called `:proc`
  * `:proc` contains all info: id, structname, task, (wow about repeat flag?)

  TODO: tasks replace structs have to be guarded: `:@waittime` --> `:%waittime`" 
  [id struct struct-name]
  (mapv (fn [{:keys [Ctrl Definition Title Description Element]} i]
          {:title Title  :elem Element :descr Description :ndx i
           :proc (agent {:all-exec-hooks []
                         :processed true ; run on start or not
                         :ctrl (or (keyword Ctrl) :ready)
                         :state (flattenv
                                 (mapv (fn [s j] 
                                         (mapv (fn [t k] 
                                                 {:id id :struct struct-name
                                                  :ndx i :sdx j :pdx k
                                                  :is :ready
                                                  :task t})
                                               s (range)))
                                       Definition (range)))})})
        struct (range)))

(defn image
  "Note: The takedown fn is **not** `(shutdown-agents)`:
    `(shutdown-agents)` closes ''two global
  executors and CIDER uses those global threadpools''"
  [{id :_id  {:keys [Exchange Container Definitions] :as mp} :Mp}]
  {(keyword id) (-> mp
                    (assoc :Container (struct->struct-model id Container :cont))
                    (assoc :Definitions (struct->struct-model id Definitions :defs))
                    (assoc :Documents (agent []))
                    (assoc :Exchange (agent Exchange))
                    (dissoc :Task))})

(defn merge-image [res doc] (merge res (image doc)))

(defn build [docs] (reduce merge-image {} docs))
