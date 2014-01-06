(ns petri-net.core
  (:gen-class))

;; Store all nets into nets
;; Each entry symbolizes one net in this program. It is structured as
;; follows:
;; {:net1
;;    {:edges-from   {:from {:to Tokens}}
;;     :edges-to     {:to {:from Tokens}}
;;     :places       {:place1 Tokens, :place2 42, ...}
;;     :transitions #{:name1, :name2, ...}}
;;
;; edges-from : key is :from, seems to be a good idea
;; edges-to   : key is :to, seems to be a good idea
;; places     : adds a new place into the net and stores tokens
;; transitions: a set of transitions 

(def nets (atom {}))

(defn new-net
  "Adds a new net to nets and associates it with the name."
  [net]
  (swap! nets assoc net {:edges-from   {}
                         :edges-to     {}
                         :places       {}
                         :transitions #{}}))
(new-net :test)

(defn reset-net
  "Clear all nets and restore it to defaults."
  []
  (reset! nets {}))
(reset-net)

(defn add-place
  "Adds a new place into an existing petri net."
  [net name tokens]
  (let [new-places (assoc ((@nets net) :places) name tokens)
        new-net    (assoc  (@nets net) :places  new-places)]
    (swap! nets assoc net new-net)))
(add-place :test :p 43)

(defn add-transition
  "Adds a new transition into an existing petri net."
  [net name]
  (let [new-trans (conj ((@nets net) :transitions) name)
        new-net   (assoc (@nets net) :transitions  new-trans)]
    (swap! nets assoc net new-net)))
(add-transition :test :bombe4)

(defn add-edge
  "Add an edge between a place and a transition."
  [net from to tokens]
  (when (or
         (and (from ((@nets net) :places))
              (to   ((@nets net) :transitions)))
         (and (from ((@nets net) :transitions))
              (to   ((@nets net) :places))))
    (let [new-edges-to   (assoc ((@nets net) :edges-to)   to   {from tokens})
          new-edges-from (assoc ((@nets net) :edges-from) from {to   tokens})
          new-net        (assoc  (@nets net) :edges-to new-edges-to :edges-from new-edges-from)]
      (swap! nets assoc net new-net))))
(add-edge :test :p :bombe 4)
