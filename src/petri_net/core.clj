(ns petri-net.core
  (:gen-class))

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

(defn new-net
  "Adds a new net to nets and associates it with the name."
  [net]
  (swap! nets assoc net {:edges-from-trans {}
                         :edges-to-trans   {}
                         :places           {}
                         :transitions     #{}}))

(defn reset-net
  "Clear all nets and restore it to defaults."
  []
  (reset! nets {}))

(defn add-place
  "Adds a new place into an existing petri net."
  [net name tokens]
  (swap! nets assoc-in [net :places name] tokens))

(defn add-transition
  "Adds a new transition into an existing petri net."
  [net name]
  (let [new-trans (conj ((@nets net) :transitions) name)
        new-net   (assoc (@nets net) :transitions  new-trans)]
    (swap! nets assoc net new-net)))

(defn add-edge-to-transition
  "Add an edge from a place to a transition."
  [net from to tokens]
  (when (and (from ((@nets net) :places))
             (to   ((@nets net) :transitions)))
    (swap! nets assoc-in [net :edges-to-trans from] {to tokens})))

(defn add-edge-from-transition
  "Add an edge from a transition to a place."
  [net from to tokens]
  (when (and (from ((@nets net) :transitions))
             (to   ((@nets net) :places)))
    (swap! nets assoc-in [net :edges-from-trans from] {to tokens})))

;;
(new-net :test)
(reset-net)
(add-transition :test :bombe)
(add-place :test :p 44)
(add-edge-to-transition :test :p :bombe 41)
(add-edge-from-transition :test :bombe5 :p 21)

@nets
