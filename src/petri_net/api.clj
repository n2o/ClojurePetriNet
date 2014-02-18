(ns petri-net.api
  (:require [petri-net.core :as controller]))

;;;; Retrieve information from controller

(defn net?
  "Returns true if the net exists in the database and falsey if not."
  [net]
  (when-not (number? net)
    (not (nil? (get-net net)))))

(def get-nets controller/nets)
get-nets

(defn get-net
  "Returns the net and all his attributes."
  [net]
  (when (net @get-nets) (net @get-nets)))
(get-net :first)

(defn get-edges-to-trans
  "Returns edges from places to transitions for a specific net if possible, else nil."
  [net]
  (when (net? net)
    ((get-net net) :edges-to-trans)))
(get-edges-to-trans :first)

(defn get-edges-from-trans
  "Returns edges from transitions to places for a specific net if possible, else nil."
  [net]
  (when (net? net)
    ((get-net net) :edges-from-trans)))
(get-edges-from-trans :first)

(defn get-places
  "Returns all places incl. costs for a specific net if possible, else nil."
  [net]
  (when (net? net)
    ((get-net net) :places)))
(get-places :first)

(defn get-transitions
  "Returns all transitions for a spec. net if possible, else nil."
  [net]
  (when (net? net)
    ((get-net net) :transitions)))
(get-transitions :first)


;;;; Section to manipulate the nets

(defn new-net
  "Initializes new net, if it is NOT already in the database."
  [net]
  (when-not (net? net)
    (controller/new-net net)))
(new-net :second)

(defn delete-net
  "Deletes a net if it exists."
  [net]
  (when (net? net)
    (controller/delete-net net)))
(delete-net :second)

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
  (when (net? net)
    (controller/add-place net place tokens)))
(add-place :first :z 50)

(defn add-transition
  "Adds a new transition into an existing petri net."
  [net trans]
  (when (and (net? net)
             (not (contains? (get-transitions net) trans)))
    (controller/add-transition net trans)))
(add-transition :first :42)

(defn add-edge-to-transition
  "Add an edge from a place to a transition. If the edge exists, update the entry."
  [net from to]
  (when (net? net)
    (controller/add-edge-to-transition net from to)))

(defn add-edge-from-transition
  "Add an edge from a transition to a place. Update entry if exists."
  [net from to]
  (when (net? net)
    (controller/add-edge-from-transition net from to)))

(defn merge-net
  "Merging two nets and define which places / transitions should be merged.
   Places and Transitions must be key-value pairs."
  [net1 net2 equal-places equal-trans]
  (when (and (net? net1) (net? net2))
    (controller/merge-net net1 net2 equal-places equal-trans)))
