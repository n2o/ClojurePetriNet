(ns petri-net.simulator-test
  (:require [petri-net.api :as api])
  (:use [midje.sweet])) 

(def nets
  {:second
   {:edges-from-trans {:foo {:a 3}},
    :edges-to-trans {:q {:foo 1}},
    :places {:a 55, :q 22},
    :transitions #{:foo}},
   :first
   {:edges-from-trans {:bombi {:a 22}},
    :edges-to-trans {:z {:bombe 4}, :p {:bombi 43}},
    :places {:z 42, :a 100, :p 44},
    :transitions #{:bombe :bombi}}})

;;;; Facts about the attributes


