(ns dxrt.core
  (:require [dxrt.db :as db]))


(defn proc-state [{:keys [processed state] :as a} model opts]
  (if-not processed
    (-> a
        (prn (db/get-task "Common-wait" opts))
        (assoc :processed true))
    a))

(defn proc-agent [model {:keys [id group ndx] :as loc}]
  (-> model
      id
      group
      (get ndx)
      :proc))

;; ________________________________________________________________________
;; start
;; ________________________________________________________________________
(defn start-loop-future
  "Starts a loop in a future and stores the reference under the key
  `:loop-future`."
  [a f! {:keys [heartbeat]}]
  (assoc a :loop-future (future (loop []
                                  (f!)
                                  (Thread/sleep heartbeat)
                                  (recur)))))

(defn start
  "Every `:Container`/ `:Definitions` element got his own `loop-recur`
  `future` which periodically checks the state and launches new
  worker. It executes the `:all-exec-hooks` in case of `:all-exec`
  e.g. in order to realize the `ctrl_mp` and `select_definition`
  actions."
  [model {:keys [launchshift] :as opts}]
    (mapv (fn [[id {:keys [Container Definitions] :as mp}]]
            
            (mapv (fn [{a :proc}]
                    (Thread/sleep launchshift)
                    (send a start-loop-future
                          (fn [] (send a proc-state model opts)) opts))
                  Container)
            
            #_(mapv (fn [{a :proc}]
                      (Thread/sleep launchshift)
                      (send a start-loop-future
                            (fn [] (send a proc-state model))  opts))
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

          #_(mapv (fn [{a :proc}]
                  (send a stop-loop-future))
                Definitions))
        
        model))

