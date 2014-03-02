(ns petri-net.api-test
  (:require [petri-net.api :as api])
  (:use [midje.sweet])) 

(facts "Testing Getter..."
  (fact
    (api/get-edges-to-trans :first) => {:z {:bombe 4}, :p {:bombi 43, :bombe 41}}
    (api/get-edges-from-trans :first) => {:bombe {:a 20}, :bombi {:b 10, :a 22}}
    (api/get-tokens :first :a) => 100))
