(ns petri-net.core
  (:gen-class))

;; Kill this and add better solution later on
(use '[clojure.walk :only [prewalk-replace]])

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

;;;; Basic functions

(defn new-net
  "Adds a new net to nets and associates it with the name."
  [net]
  (swap! nets assoc net {:edges-from-trans {}
                         :edges-to-trans   {}
                         :places           {}
                         :transitions     #{}}))

(defn- prefix-string
  "Add the network as a prefix for the input variable"
  [net input]
  (str net "#" input))

(defn- prefix-string-vec
  "Add the network as a prefix for each entry in a vector"
  [net input]
  (for [entry input] (str net "#" entry)))

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
  [net from to tokens]
  (swap! nets assoc-in [net :edges-to-trans from to] tokens))

(defn add-edge-from-transition
  "Add an edge from a transition to a place."
  [net from to tokens]
  (swap! nets assoc-in [net :edges-from-trans from to] tokens))

;;;; Merging two nets

(defn merge-net
  "Merging two nets and define which places / transitions should be merged.
   Places and Transitions must be key-value pairs."
  [net1 net2 equal-places equal-transitions]
  (let [places-net1   ((@nets net1) :places)
        places-net2   ((@nets net2) :places)
        merged-places (merge-places net1 places-net1 places-net2 equal-places)
        merged-edges-from-trans 42
        merged-edges-to-trans   24]
    (println merged-places)))

(merge-net :first :second {:a :q} {})
(@nets :first)
(println (for [[k v] ((@nets :first) :edges-from-trans)] v))

(defn merge-edges-to-trans
  "Merging the edges from places to transitions. Takes net1 for prefix and the edges-to-trans
  from both nets."
  [net edges-to-net1 edges-to-net2 equal-places equal-trans]
  
  )

(defn merge-places
  "Takes the places from two nets and a map of places to be merged. Prefixes unmatched places.
  For example:
    first net:  {:a 100, :p 4}
    second net: {:a 55,  :q 22}
  returns: {:first#:p 4, :a 55, :q 100}"
  [net1 pl-net1 pl-net2 equal]
  (let [prefix-pl-net1 (prefix-unmatched net1 pl-net1 equal)
        rename-pl-net1 (clojure.set/rename-keys prefix-pl-net1 equal)]
    (merge-with max rename-pl-net1 pl-net2)))

(merge-places :first ((@nets :first) :places) ((@nets :second) :places) {:a :q})

(defn prefix-unmatched
  "Takes a set of 'places' and a map 'equal' which addresses the places to be merged.
  Then filter all the places, which are NOT in equal to prefix them with the net name."
  [net places equal]
  (let [filtered (vec (filter (fn [[k v]] (not (contains? equal k))) places))
        nofilter (vec (filter (fn [[k v]] (contains? equal k)) places))]
    (merge (vec-to-map nofilter) (vec-to-map-prefix net filtered))))

(prefix-places :first ((@nets :first) :edges-from-trans) {})

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






;;(prewalk-replace {:a :b} {:a {:bombe 41}, :p {:bombe2 43, :bombe 41}})


;;;; Testing area
@nets
(new-net :first)
(add-transition :first :bombe)
(add-transition :first :bombi)
(add-place :first :p 44)
(add-place :first :a 100)
(add-edge-to-transition :first :p :bombe 41)
(add-edge-to-transition :first :p :bombi 43)
(add-edge-from-transition :first :bombi :a 22)

(new-net :second)
(add-transition :second :foo)
(add-place :second :q 22)
(add-place :second :a 55)
(add-edge-to-transition :second :q :foo 1)
