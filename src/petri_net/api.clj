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
    (place (get-places net))))

(defn get-transitions
  "Returns all transitions for a spec. net if possible, else nil."
  [net]
  (when (net? net)
    ((get-net net) :transitions)))

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
  "Saves the whole database to the specified file."
  [file]
  (controller/save-net file))

(defn load-net
  "Loads a DSL from file into the database. Overwrites the old one."
  [file]
  (controller/load-net file))


;;;; Manipulate one net, for example add a place, transition, ...

(defn add-place
  "Add a place to a net. Checks if the name placename is already taken. If yes, update entry in database."
  [net place tokens]
  (when (and (net? net) (number? tokens))
    (controller/add-place net place tokens)))

(defn add-transition
  "Adds a new transition into an existing petri net."
  [net trans]
  (when (and (net? net)
             (not (contains? (get-transitions net) trans)))
    (controller/add-transition net trans)))

(defn add-edge-to-transition
  "Add an edge from a place to a transition. If the edge exists, update the entry."
  [net from to tokens]
  (when (net? net)
    (when-not (or (nil? ((get-places net) from))
                  (nil? ((get-transitions net) to)))
      (controller/add-edge-to-transition net from to tokens))))

(defn add-edge-from-transition
  "Add an edge from a transition to a place. Update entry if exists."
  [net from to tokens]
  (when (net? net)
    (when-not (or (nil? ((get-transitions net) from))
                  (nil? ((get-places net) to)))
      (controller/add-edge-from-transition net from to tokens))))

(defn merge-net
  "Merging two nets and define which places / transitions should be merged.
   Places and Transitions must be key-value pairs."
  [net1 net2 equal-places equal-trans]
  (when (and (net? net1) (net? net2))
    (controller/merge-net net1 net2 equal-places equal-trans)))
