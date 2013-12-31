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
  [name] (swap! nets assoc name {:edges-from  {}
                                 :edges-to    {}
                                 :places      {}
                                 :transitions #{}}))
(new-net test)
@nets

(defn reset-net
  "Clear all nets and restore it to defaults"
  [] (reset! nets {}))
(reset-net)

(get @nets test)


(doc new-net)
(doc reset-net)
