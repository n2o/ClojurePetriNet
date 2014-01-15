(ns petri-net.core
  (:gen-class))

(use '[clojure.set :only [union]])
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

(defn reset-nets
  "Clear all nets and restore it to defaults."
  []
  (reset! nets {}))

(defn- prefix-string
  "Add the network as a prefix for the input variable"
  [net input]
  (str net "#" input))

(defn add-place
  "Adds a new place into an existing petri net."
  [net name tokens]
  (swap! nets assoc-in [net :places (prefix-string net name)] tokens))

(defn add-transition
  "Adds a new transition into an existing petri net."
  [net name]
  (swap! nets update-in [net :transitions] #(union % #{(prefix-string net name)})))

(defn add-edge-to-transition
  "Add an edge from a place to a transition."
  [net from to tokens]
  (swap! nets assoc-in [net :edges-to-trans (prefix-string net from) (prefix-string net to)] tokens))

(defn add-edge-from-transition
  "Add an edge from a transition to a place."
  [net from to tokens]
  (swap! nets assoc-in [net :edges-from-trans (prefix-string net from) (prefix-string net to)] tokens))

;;;; Merging two nets

(defn merge-net
  "Merging two nets and define which places / transitions should be merged.
   Places and Transitions must be key-value pairs."
  [net1 net2 places transitions]
  (let [places-net1 (prewalk-replace places ((@nets net1) :places))
        places-net2 (prewalk-replace places ((@nets net2) :places))
        merged-places      (merge ((@nets net1) :places)      ((@nets net2) :places))
        merged-transitions (union ((@nets net1) :transitions) ((@nets net2) :transitions))]
    (println places-net1)
    (println merged-places)
    (println merged-transitions)))

(merge-with max {} {})
(merge-net :test :second {} {})

;;(prewalk-replace {:a :b} {:a {:bombe 41}, :p {:bombe2 43, :bombe 41}})


;;;; Testing area
@nets
(new-net :test)
(reset-nets)
(add-transition :test :bombe)
(add-transition :test :bombi)
(add-place :test :p 44)
(add-edge-to-transition :test :p :bombe 41)
(add-edge-to-transition :test :p :bombe2 43)
(add-edge-to-transition :test :a :bombe 41)
(add-edge-from-transition :test :bombe2 :p 22)

(new-net :second)
(add-transition :second :foo)
(add-place :second :q 22)
(add-edge-to-transition :second :q :foo 1)
