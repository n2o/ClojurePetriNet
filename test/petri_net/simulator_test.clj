(ns petri-net.simulator-test
  (:require [petri-net.api :as api])
  (:require [petri-net.simulator :as simulator])
  (:use [midje.sweet]))

;;;; Facts about the attributes

(facts "Fireable...?"
  (fact
    (simulator/fireable :first [:p 44]) => true
    (simulator/fireable :first [:p 45]) => false))

(facts "Trying transition-alive."
  (fact
    (simulator/transition-alive :first :bombe) => true
    (simulator/transition-alive :first :bombe :bombi) => true))
