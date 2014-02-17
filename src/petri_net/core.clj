(ns petri-net.core
  (:gen-class)
  (:require [clojure.set]))

;; Store all nets into nets
;; Each entry symbolizes one net in this program. It is structured as
;; follows:
;; {:net1
;;    {:edges-from-trans   {:from {:to tokens}}
;;     :edges-to-trans     {:from {:to tokens}}
;;     :places             {:place1 Tokens, :place2 42, ...}
;;     :transitions       #{:name1, :name2, ...}}
;;
;; edges-from-trans : key is :from, seems to be a good idea
;; edges-to-trans   : key is :to, seems to be a good idea
;; places           : adds a new place into the net and stores tokens
;; transitions      : a set of transitions 

(def nets (atom {}))

;;;; Basic functions for nets

(defn new-net
  "Adds a new net to 'nets' and associates it with the name."
  [net]
  (swap! nets assoc net {:edges-from-trans {}
                         :edges-to-trans   {}
                         :places           {}
                         :transitions     #{}}))

(defn delete-net
  "Removes one net including all data from the database 'nets'."
  [net]
  (swap! nets dissoc net))

(defn save-net
  "Saves the database 'nets' into the file."
  [file]
  (spit file @nets))

(defn load-net
  ""
  [file]
  (swap! nets conj (read-string (slurp file))))


;;;; Auxiliary functions
(defn- prefix-string
  "Add the network as a prefix for the input variable."
  [net input]
  (str net "#" input))

(defn- vec-to-map
  "Takes an vector of vectors and translates it into a hash-map.
  For example: [[:a 2] [:b 3]] => {:a 2, :b 3}"
  [input]
  (apply merge (map #(hash-map (first %) (second %)) input)))

(defn- vec-to-map-prefix
  "Takes an vector of vectors and translates it into a hash-map with prefixed keys.
  For example: [[:a 2] [:b 3]] => {:prefix#:a 2, :prefix#:b 3}"
  [net input]
  (apply merge (map #(hash-map (prefix-string net (first %)) (second %)) input)))

(defn- prefix-unmatched
  "Takes a set 'set' and a map 'equal' which addresses the places to be merged.
  Then filter all the places, which are NOT in equal to prefix them with the net name."
  [net set equal]
  (let [filtered (vec (filter (fn [[k v]] (not (contains? equal k))) set))
        nofilter (vec (filter (fn [[k v]] (contains? equal k)) set))]
    (merge (vec-to-map nofilter) (vec-to-map-prefix net filtered))))

;;;; Manipulate specific net/nets

(defn add-place
  "Adds a new place into an existing petri net."
  [net name tokens]
  (swap! nets assoc-in [net :places name] tokens))

(defn add-transition
  "Adds a new transition into an existing petri net."
  [net name]
  (swap! nets update-in [net :transitions] #(clojure.set/union % #{name})))

(defn add-edge-to-transition
  "Add an edge from a place to a transition."
  [net from to]
  (swap! nets assoc-in [net :edges-to-trans from] to))

(defn add-edge-from-transition
  "Add an edge from a transition to a place."
  [net from to]
  (swap! nets assoc-in [net :edges-from-trans from] to))


;;;; Merging two nets

(defn- merge-places
  "Takes the places from two nets and a map of places to be merged. Prefixes unmatched places.
  For example:
    equal:      {:a :q}
    first net:  {:a 100, :p 4}
    second net: {:a 55,  :q 22}
  returns: {:first#:p 4, :a 55, :q 100}"
  [net1 set-net1 set-net2 equal]
  (let [prefix-set-net1 (prefix-unmatched net1 set-net1 equal)
        rename-set-net1 (clojure.set/rename-keys prefix-set-net1 equal)]
    (merge-with max rename-set-net1 set-net2)))

(defn- merge-edges-to-trans
  "Merging the edges from places to transitions.
  Takes net1 for prefix and the edges-to-trans from both nets."
  [net1 edges-net1 edges-net2 equal-places equal-trans]
  (let [prefix-edges-net1 (prefix-unmatched net1 edges-net1 equal-places)
        rename-edges-net1 (clojure.set/rename-keys prefix-edges-net1 equal-places)
        ready-edges-net1  (vec-to-map
                           (for [[k v] rename-edges-net1]
                             [k (clojure.set/rename-keys (prefix-unmatched net1 v equal-trans) equal-trans)]))]
    (merge-with merge ready-edges-net1 edges-net2)))

(defn- merge-edges-from-trans
  "Merges edges from transitions to places from two nets. Prefixes those places and transitions which
  should be merged."
  [net1 edges-net1 edges-net2 equal-places equal-trans]
  (let [prefix-edges-net1 (prefix-unmatched net1 edges-net1 equal-trans)
        rename-edges-net1 (clojure.set/rename-keys prefix-edges-net1 equal-trans)
        ready-edges-net1  (vec-to-map (for [[k v] rename-edges-net1] [k (prefix-unmatched net1 v equal-places)]))]
    (merge-with merge ready-edges-net1 edges-net2)))

(defn- merge-transitions
  "Takes sets of transitions from two nets and merges them. I translate the set from net1 into
  a hash map to use the function merge-places. Then I reduce them again to normal sets."
  [net1 trans-net1 trans-net2 equal]
  (let [map-net1      (apply merge (map #(hash-map % 0) trans-net1))
        prefixed-net1 (merge-places net1 map-net1 {} equal)]
    (clojure.set/union (set (keys prefixed-net1)) trans-net2)))

;(merge-transitions :first ((@nets :first) :transitions) ((@nets :second) :transitions) {})


(defn merge-net
  "Merging two nets and define which places / transitions should be merged.
   Places and Transitions must be key-value pairs."
  [net1 net2 equal-places equal-trans]
  (let [name            (str net1 "#" net2)
        places-net1     ((@nets net1) :places)
        places-net2     ((@nets net2) :places)
        edges-to-net1   ((@nets net1) :edges-to-trans)
        edges-to-net2   ((@nets net2) :edges-to-trans)
        edges-from-net1 ((@nets net1) :edges-from-trans)
        edges-from-net2 ((@nets net2) :edges-from-trans)
        trans-net1      ((@nets net1) :transitions)
        trans-net2      ((@nets net2) :transitions)
        merged-places           (merge-places net1 places-net1 places-net2 equal-places)
        merged-edges-to-trans   (merge-edges-to-trans   net1 edges-to-net1   edges-to-net2   equal-places equal-trans)
        merged-edges-from-trans (merge-edges-from-trans net1 edges-from-net1 edges-from-net2 equal-places equal-trans)
        merged-transitions      (merge-transitions net1 trans-net1 trans-net2 equal-trans)]
    (new-net name)
    (doall (map (fn [trans] (add-transition name trans)) merged-transitions))
    (doall (map (fn [[k v]] (add-place name k v)) merged-places))
    (doall (map (fn [[k v]] (add-edge-to-transition name k v)) merged-edges-to-trans))
    (doall (map (fn [[k v]] (add-edge-from-transition name k v)) merged-edges-from-trans))))


;;;; Testing area
(def init-two-nets (do (new-net :first)
                       (add-transition :first :bombe)
                       (add-transition :first :bombi)
                       (add-place :first :p 44)
                       (add-place :first :a 100)
                       (add-place :first :z 42)
                       (add-edge-to-transition :first :p {:bombe 41})
                       (add-edge-to-transition :first :p {:bombi 43})
                       (add-edge-to-transition :first :z {:bombe 4})
                       (add-edge-from-transition :first :bombi {:a 22})

                       (new-net :second)
                       (add-transition :second :foo)
                       (add-place :second :q 22)
                       (add-place :second :a 55)
                       (add-edge-to-transition :second :q {:foo 1})
                       (add-edge-from-transition :second :foo {:a 3})))

;; Merge two nets, get a new merged one added to 'nets'
;(do
;  (merge-net :first :second {} {:bombe :foo})
;  (pprint @nets))
