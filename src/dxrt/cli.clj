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

(defstruct a-loc :id :group :ndx)

(defstruct s-loc :id :group :ndx :sdx :pdx :is)

(defn proc [loc]
  (let [a (dx/proc-agent (model->)  loc)
        f (fn [a] (assoc a :processed false))]
    (await a)
    (send a f))) 


(defn state [loc]
  (let [a (dx/proc-agent (model->) loc)]
    (send a dx/state loc)))

(comment
  (proc (struct a-loc :mpd-ppc-gas_dosing :Container 0))
  (state (struct s-loc :mpd-ppc-gas_dosing :Container 0 0 0 :error)))

(comment
  (def p (p/open))
  (add-tap #'p/submit)
  
  (tap> @sys/system))
