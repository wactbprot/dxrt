(ns dxrt.scheduler)


(defn proc-state [{:keys [processed state] :as a}]
  (if-not processed
    (-> a
        (assoc :processed true))
    a))
    

;; ________________________________________________________________________
;; start
;; ________________________________________________________________________
(defn start-loop-future
  "Starts a loop in a future and stores the reference under the key
  `:loop-future`."
  [a proc-fn! {:keys [heartbeat]}]
  (assoc a :loop-future (future (loop []
                                  (proc-fn!)
                                  (Thread/sleep heartbeat)
                                  (recur)))))

(defn start
  "Every `:Container`/`:Definitions` element got his own `loop-recur`
  `future` which periodically checks the state and launches new
  worker. It executes the `:all-exec-hooks` in case of `:all-exec`
  e.g. in order to realize the `ctrl_mp` and `select_definition`
  actions."
  [model {:keys [launchshift] :as opts}]
  (mapv (fn [[id {:keys [Container Definitions] :as mp}]]
          (pmap (fn [{a :proc}]
                  (Thread/sleep launchshift)
                  (send a start-loop-future (fn [] (send a proc-state))  opts))
                Container)
          (pmap (fn [{a :proc}]
                  (Thread/sleep launchshift)
                  (send a start-loop-future (fn [] (send a proc-state))  opts))
                Definitions))
        model)
  model)

;; ________________________________________________________________________
;; stop
;; ________________________________________________________________________
(defn stop-loop-future
  "Stops the loop under the key `:loop-future`."
  [a]
  (assoc a :loop-future (future-cancel (-> a :loop-future))))
  
(defn stop [model]
  (mapv (fn [[id {:keys [Container Definitions] :as mp}]]
          (mapv (fn [{a :proc}]
                  (send a stop-loop-future))
                Container)
          (mapv (fn [{a :proc}]
                  (send a stop-loop-future))
                Definitions))
        model))
      
