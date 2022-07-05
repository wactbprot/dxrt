(ns dxrt.scheduler)

(defonce continue  (atom {}))

(defn state-loop [{:keys [id struct ndx]} proc-fn! {:keys [heartbeat]}]
  (loop []
    (proc-fn!)
    (Thread/sleep heartbeat)
    (when (get-in @continue [id struct ndx]) (recur))))

(defn up
  "Every `:Container`/`:Definitions` element got his own `loop-recur`
  `future` which periodically checks the state and launches new
  worker. It executes the `:all-exec-hooks` in case of `:all-exec`
  e.g. in order to realize the `ctrl_mp` and `select_definition`
  actions.
  "
  [{:keys [id Container Definitions] :as model} opts]

  (state-loop {:id id :struct :Container :ndx 0} (fn [] (prn "check state")) opts)

  model)
