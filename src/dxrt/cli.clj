(ns dxrt.cli
  ^{:author "Thomas Bock <thomas.bock@ptb.de>"
    :doc "The dxrt cli."}
  (:require [dxrt.core :as dx]
            [dxrt.system :as sys]
            [portal.api :as p]))

(comment
  (sys/start ["mpd-ppc-gas_dosing" "mpd-se3-calib"])
  (sys/stop))

(defn model-> [] (-> @sys/system :model/core))

(defstruct loc :id :group :ndx)

(defn proc [loc]
  (let [a (dx/proc-agent (model->)  loc)
        f (fn [a] (assoc a :processed false))]
    (await a)
    (send a f))) 
  
#_(proc (struct loc :mpd-ppc-gas_dosing :Container 0))


(comment
  (def p (p/open))
  (add-tap #'p/submit)
  
  (tap> sys))
