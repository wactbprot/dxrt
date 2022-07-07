(ns dxrt.scheduler)

(defn start-loop-future [a proc-fn! {:keys [heartbeat]}]
  (assoc a :loop-future (future (loop []
                                  (proc-fn!)
                                  (Thread/sleep heartbeat)
                                  (recur)))))

(defn stop-loop-future [a]
  (assoc a :loop-future (future-cancel (-> a :loop-future))))


(defn apply-struct-proc [model f]
  (mapv (fn [[id {:keys [Container Definitions] :as mp}]]
          (mapv (fn [{a :proc}]
                  (f a))
                Container))
        model))

(defn up
  "Every `:Container`/`:Definitions` element got his own `loop-recur`
  `future` which periodically checks the state and launches new
  worker. It executes the `:all-exec-hooks` in case of `:all-exec`
  e.g. in order to realize the `ctrl_mp` and `select_definition`
  actions.
  "
  [model opts]
  (apply-struct-proc model
                     (fn [a] (send a start-loop-future
                                  (fn [] (prn "."))  opts)))

  model)
  
(defn down [model]
  (apply-struct-proc model (fn [a] (send a stop-loop-future))))
      
