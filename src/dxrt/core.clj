(ns dxrt.core
  (:require [dxrt.db :as db]
            [dxrt.task :as task]))
(comment
  ;; a proc agent:
  #<Agent@4610e509: 
  {:all-exec-hooks [],
   :processed true,
   :ctrl :load,
   :state
   [{:id :mpd-ppc-gas_dosing,
     :group :Container,
     :ndx 0,
     :sdx 0,
     :pdx 0,
     :is :error,
     :task {:TaskName "PPC_MaxiGauge-ini"}}
    {:id :mpd-ppc-gas_dosing,
     :group :Container,
     :ndx 0,
     :sdx 1,
     :pdx 0,
     :is :ready,
     :task {:TaskName "PPC_DualGauge-ini"}}
    {:id :mpd-ppc-gas_dosing,
     :group :Container,
     :ndx 0,
     :sdx 2,
     :pdx 0,
     :is :ready,
     :task {:TaskName "PPC_VAT_DOSING_VALVE-ini"}}
    {:id :mpd-ppc-gas_dosing,
     :group :Container,
     :ndx 0,
     :sdx 3,
     :pdx 0,
     :is :ready,
     :task {:TaskName "PPC_Faulhaber_Servo-comp_ini"}}],
   :loop-future #<Future@a62753c: :pending>}>)

(defn state [{:keys [state] :as a} {:keys [id group ndx sdx pdx is]}]
  (assoc a :state (mapv (fn [s] (if (and (= id (:id s))
                                        (= sdx (:sdx s))
                                        (= pdx (:pdx s))
                                        (= ndx (:ndx s))
                                        (= group (:group s)))
                                 (assoc s :is is)
                                 s)) state)))


(defn check-error [a]
  (let [err (count (filterv #(= :error (:is %)) (:state a)))]
    (if (pos? err)
      (assoc a :ctrl :error)
      a)))
      
(defn proc-state [{:keys [processed] :as a} opts]
  (if-not processed
    (-> a
        check-error
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
  [{:keys [model launchshift] :as opts}]
  (def o opts)
  (mapv (fn [[id {:keys [Container Definitions] :as mp}]]
            
            (mapv (fn [{a :proc}]
                    (Thread/sleep launchshift)
                    (send a start-loop-future
                          (fn [] (send a proc-state opts)) opts))
                  Container)
            
            #_(mapv (fn [{a :proc}]
                      (Thread/sleep launchshift)
                      (send a start-loop-future
                            (fn [] (send a proc-state opts))  opts))
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

