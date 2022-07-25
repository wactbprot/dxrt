(ns dxrt.exchange)

(defn exch-agent [model {:keys [id] loc}]
  (-> model id :Exchange))
