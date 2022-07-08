(ns dxrt.cli
  ^{:author "Thomas Bock <thomas.bock@ptb.de>"
    :doc "The dxrt cli."}
  (:require [dxrt.system :as sys]
            [portal.api :as p]))


(comment
  (sys/start ["mpd-ppc-gas_dosing" "mpd-se3-calib"])
  (sys/stop sys))


(comment
  (def p (p/open))
  (add-tap #'p/submit)
  
  (tap> sys))
