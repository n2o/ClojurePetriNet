(ns petri-net.simulator-test
  (:require [petri-net.api :as api])
  (:require [petri-net.simulator :as simulator])
  (:use [midje.sweet]))

;;;; Facts about the attributes

(facts "Trying transition-alive"
  (fact
    (simulator/transition-alive :first :bombe) => true))
