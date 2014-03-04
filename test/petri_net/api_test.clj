(ns petri-net.api-test
  (:require [petri-net.api :as api])
  (:use [midje.sweet])) 

(facts "Testing Getter..."
  (fact
    (api/get-edges-to-trans :first) => {:z {:bombe 4}, :p {:bombi 43, :bombe 41}}
    (api/get-edges-to-trans :nilnet) => nil
    (api/get-edges-from-trans :first) => {:bombe {:a 20}, :bombi {:b 10, :a 22}}
    (api/get-tokens :first :a) => 100
    (api/get-transitions :first) => #{:bombe :bombi}
    (api/get-transitions :nilnet) => nil
    (api/get-places-to-transition :first :bombe) => {:p 41, :z 4}
    (api/get-places-to-transition :first :niltrans) => {}
    (api/get-places-from-transition :first :bombe) => {:a 20}
    (api/get-places-from-transition :first :niltrans) => nil))
