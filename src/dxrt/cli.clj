(ns dxrt.cli
  ^{:author "Thomas Bock <thomas.bock@ptb.de>"
    :doc "The dxrt cli."}
<<<<<<< HEAD
  (:require [libcdb.core :as db]
            [integrant.core :as ig]))
=======
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
>>>>>>> b979dc3962a8ca91bcd090b51c4e28feafea3e34


(defn state [loc]
  (let [a (dx/proc-agent (model->) loc)]
    (send a dx/state loc)))

<<<<<<< HEAD
(defn  config [id image]

  {:db/conn {:prot "http"
             :host "localhost"
             :port 5984
             :name "vl_db"}

   :db/mpd {:conn (ig/ref :db/conn)
            :design "dbmp"
            :view "mpdocs"}

   :db/tasks {:conn (ig/ref :db/conn)
              :design "dbmp"
              :view "tasks"}

   :image/container {:doc (ig/ref :mpd/doc) :image image}
   :image/definition {:doc (ig/ref :mpd/doc) :image image}
   :image/state {:doc (ig/ref :mpd/doc) :image image}
   :image/sheduler {:image (ig/ref :image/state)}})
=======
(comment
  (proc (struct a-loc :mpd-ppc-gas_dosing :Container 0))
  (state (struct s-loc :mpd-ppc-gas_dosing :Container 0 0 0 :error)))
>>>>>>> b979dc3962a8ca91bcd090b51c4e28feafea3e34

(comment
  (def p (p/open))
  (add-tap #'p/submit)
  
  (tap> @sys/system))
