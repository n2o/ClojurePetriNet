(ns petri-net.simulator-test
  (:require [petri-net.api :as api])
  (:require [petri-net.simulator :as simulator])
  (:use [midje.sweet]))

;;;; Facts about the attributes

(facts "Fireable...?"
  (fact
    (simulator/fireable? :first [:p 44]) => true
    (simulator/fireable? :first [:p 45]) => false))

(facts "Trying transition-alive."
  (fact
    (simulator/transition-alive :first :bombe) => true
    (simulator/transition-alive :first :bombe :bombi) => true))

(facts "Check if there is at least one non-empty place"
  (fact
    (simulator/non-empty :first :p) => true
    (simulator/non-empty :first :p :a) => true
    (simulator/non-empty :first :p :a :z :nil) => true
    (simulator/non-empty :nilnet :nilplace) => nil
    (simulator/non-empty :first :nil) => nil))

(facts "Check if the net is alive"
  (fact
    (simulator/net-alive :first) => true
    (simulator/net-alive :nilnet) => nil))
