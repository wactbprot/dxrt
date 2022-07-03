dxrt
----

# design ideas

## metis lessons learnd
[metis](https://gitlab1.ptb.de/vaclab/metis) uses redis as the state
image. An unpleasant side effect is the need of a constant `loc` (map)- `key`
transformation (`map->key`, `key->map`).

The hope that the redis database will be perceived
as a platform for docking further programs (GUI, Metic, evaluation,
data mining, alarm system, bots, ki) is wishful thinking. so why all
the `map->key`, `key->map` time loss.

Part of metis debuging was related to a redis gui with all it pros and
cons. The _dxrt_ system should be inspectable in total with the clojure REPL.

Furthermore, [portal](https://github.com/djblue/portal) is a realy
nice option to understand the system during runtime.

* no use of an (in-mem)-database at the first place
* state, exchange, model **can be pulled into an inmutable (clock ordered) database system snapshots
* a loop recur makes the progress (turn based) which simplifies a lot

## image

* The image is a collection of `mpd`s running at `localhost`
* image is a atom
* `(build image)` 
* `(up image)` starts a loop recur over the state agents in a future, returns the future -> integrant

## loc param (location map)

* `loc` is a map of the location (or position) of a piece of informaion in the image
* keys as in previous systems:
  * `mp-id`
  * `struct`
  * `no-idx` here (cooler) `ndx`
  * `seq-idx`, ...  here  `sdx` `pdx` 


## task ns

* task building in a namespace with (read only) access to database (CouchDB, or also clock ordered ?)
* no state: get task always fresh from db and build it up on the fly like in previous system(s)
* task ns should be rewritten

## state ns

* state is an agent since it is mutated async uncoord
*  `(gen-state mpd config)` which means (same as in dx system):
  `(assoc-in @image [:mpd-ref :container 0] (agent {:state [{:par 0 :seq 0 :state :ready} ...] :ctrl ready})`


## exchange

* exchange is an agent since it is mutated async uncoord
* exchange is an agent `(assoc-in @image [:mpd-ref :exchange] (agent {:GetDefaultsFromMPD :all})`
* worker got data for exchange: `(exchange/to data loc config)` which schould be `(send (image/exchange loc) data)`

## document ns

* for every document an agent `(assoc-in @image [:mpd-ref :ids :cal-foo] (agent {rev-1-bar})`
* https://www.thattommyhall.com/2014/02/24/concurrency-and-parallelism-in-clojure/
* https://stackoverflow.com/questions/4768592/use-of-agents-to-complete-side-effects-in-stm-transactions
* worker got data to write to document: `(document/to data loc)` which schould be `(send (image/document loc) data)` location contains `:id :cal-foo`


## worker ns

* worker call: e.g. `(worker/wait task loc config)`
* worker completed: `(state/executed loc)` which schould be `(send  (image/state loc) :executed)` where `(image/state loc)` is an agent
  
  
