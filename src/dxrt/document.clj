(ns dxrt.document)

(defn document-agent [model {:keys [id] loc}]
  (-> model id :Documents))
