(ns dxrt.scheduler)

(defn up
  "Every `:Container`/`:Definitions` element got his own loop recur future
  which periodically checks the state and launches new worker. It executes the
  `:all-exec-hooks` in case of `:all-exec` e.g. in order to realize the
  `ctrl_mp` and `select_definition` actions.
  "
  [model] model)
