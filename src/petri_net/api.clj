(ns petri-net.api
  (:require [petri-net.core :as controller])
  (:use clojure.algo.monads))

;;;; Retrieve information from controller

(def get-nets controller/nets)

(defn get-net
  "Returns the net and all his attributes."
  [net]
  (when (and (not (nil? net)) (net @get-nets))
    (net @get-nets)))

(defn get-net-names
  "Returns all net-names in the database"
  []
  (keys @get-nets))

(defn net?
  "Returns true if the net exists in the database and falsey if not."
  [net]
  (when-not (number? net)
    (domonad maybe-m [ret (get-net net)] ret)))

(defn get-edges-to-trans
  "Returns edges from places to transitions for a specific net if possible, else nil."
  [net]
  (when (net? net)
    ((get-net net) :edges-to-trans)))

(defn get-edges-from-trans
  "Returns edges from transitions to places for a specific net if possible, else nil."
  [net]
  (when (net? net)
    ((get-net net) :edges-from-trans)))

(defn get-places
  "Returns all places incl. costs for a specific net if possible, else nil."
  [net]
  (when (net? net)
    ((get-net net) :places)))

(defn get-tokens
  "Returns the tokens from a specific place."
  [net place]
  (when (place (get-places net))
    ((get-places net) place)))

(defn get-transitions
  "Returns all transitions for a spec. net if possible, else nil."
  [net]
  (when (net? net)
    ((get-net net) :transitions)))

(defn get-places-to-transition
  "Returns the places which have an edge to a transition.
   Represented in a hashmap concluding {place cost-of-edge, ...}."
  [net t]
  (let [trans (get-edges-to-trans net)]
    (apply hash-map (remove nil? (flatten
                                  (for [[k v] trans] (for [[foo tokens] v]
                                                       (when (= t foo) [k tokens]))))))))

(defn get-places-from-transition
  "Returns the places which have an edge from a transition.
   Represented in a hashmap concluding {place cost-of-edge, ...}."
  [net t]
  (let [edges-from-trans (get-edges-from-trans net)]
    (when-not (nil? (edges-from-trans t))
      (apply identity (vals (filter #(= (first %) t) edges-from-trans))))))


;;;; Section to manipulate the nets

(defn new-net
  "Initializes new net, if it is NOT already in the database."
  [net]
  (when-not (net? net)
    (controller/new-net net)))

(defn delete-net
  "Deletes a net if it exists."
  [net]
  (when (net? net)
    (controller/delete-net net)))

(defn merge-net
  "Merging two nets and define which places / transitions should be merged.
   Places and Transitions must be key-value pairs."
  [net1 net2 equal-places equal-transitions]
  (when (and (net? net1) (net? net2))
    (controller/merge-net net1 net2 equal-places equal-transitions)))

(defn save-net
  "Saves one net to the specified file."
  [net]
  (controller/save-net net))

(defn load-net
  "Loads a net from file into the database."
  [file]
  (controller/load-net file))

(defn save-db
  "Saves the whole database into the file."
  []
  (controller/save-db))

(defn load-db
  "Loads a database. Replaces the old one."
  [file]
  (controller/load-db file))


;;;; Manipulate one net, for example add a place, transition, ...

(defn add-place
  "Add a place to a net. Checks if the name placename is already taken. If yes, update entry in database."
  [net place tokens]
  (when (and (net? net) (number? tokens) (< 0 tokens))
    (controller/add-place net place tokens)))

(defn update-place
  "Calculates the difference of provided tokens and currently available tokens. Then sets new value to place."
  [net place tokens]
  (let [current (get-tokens net place)
        diff (- current tokens)]
    (when (<= 0 diff)
      (add-place net place diff))))

(defn add-transition
  "Adds a new transition into an existing petri net."
  [net trans]
  (when (and (net? net)
             (not (contains? (get-transitions net) trans)))
    (controller/add-transition net trans)))

(defn add-edge-to-transition
  "Add an edge from a place to a transition. If the edge exists, update the entry."
  [net from to tokens]
  (when (and (net? net) (number? tokens) (< 0 tokens))
    (when-not (or (nil? ((get-places net) from))
                  (nil? ((get-transitions net) to)))
      (controller/add-edge-to-transition net from to tokens))))

(defn add-edge-from-transition
  "Add an edge from a transition to a place. Update entry if exists."
  [net from to tokens]
  (when (and (net? net) (number? tokens) (< 0 tokens))
    (when-not (or (nil? ((get-transitions net) from))
                  (nil? ((get-places net) to)))
      (controller/add-edge-from-transition net from to tokens))))

(defn merge-net
  "Merging two nets and define which places / transitions should be merged.
   Places and Transitions must be key-value pairs."
  [net1 net2 equal-places equal-trans]
  (when (and (net? net1) (net? net2))
    (controller/merge-net net1 net2 equal-places equal-trans)))


;;;; Edit properties

(defn add-property
  "Adds a new transition into a petri net."
  [net name]
  (when (net? net)
    (controller/add-property net name)))

(defn delete-property
  "Adds a new transition into a petri net."
  [net name]
  (when (net? net)
    (controller/delete-property net name)))
