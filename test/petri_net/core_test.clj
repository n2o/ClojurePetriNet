(ns petri-net.core-test
  (:require [petri-net.core :as controller])
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

;;;; Auxiliary functions

(facts "Transforming a vector of vectors to a map."
  (fact "This should work."
    (controller/vec-to-map [[:a 2] [:b 3]]) => {:a 2, :b 3}
    (controller/vec-to-map [[:c 4]]) => {:c 4}
    (controller/vec-to-map '([:c 4])) => {:c 4}))

(facts "Transforming a vector of vectors to a map incl. prefixing."
  (fact "Working."
    (controller/vec-to-map-prefix :prefix [[:a 2] [:b 3]]) => {":prefix#:a" 2, ":prefix#:b" 3}))

;;;; All about merge

(facts "Merging places."
  (fact "Working."
    (controller/merge-places :prefix {:a 100 :p 4} {:a 55 :q 22} {}) => {":prefix#:a" 100 ":prefix#:p" 4 :a 55 :q 22}
    (controller/merge-places :prefix {:a 100 :p 4} {:a 55 :q 22} {:a :q}) => {":prefix#:p" 4 :a 55 :q 100}))

(facts "Merging edges to transitions."
  (fact "Working."
    (controller/merge-edges-to-trans :prefix {:z {:bombe 4}, :p {:bombi 43}} {:q {:foo 1}} {} {})
    => {:q {:foo 1}, ":prefix#:p" {":prefix#:bombi" 43}, ":prefix#:z" {":prefix#:bombe" 4}}
    
    (controller/merge-edges-to-trans :prefix {:z {:bombe 4}, :p {:bombi 43}} {:q {:foo 1}} {} {:bombe :foo})
    => {:q {:foo 1}, ":prefix#:p" {":prefix#:bombi" 43}, ":prefix#:z" {:foo 4}}
    
    (controller/merge-edges-to-trans :prefix {:z {:bombe 4}, :p {:bombi 43}} {:q {:foo 1}} {:p :q} {})
    => {:q {:foo 1, ":prefix#:bombi" 43}, ":prefix#:z" {":prefix#:bombe" 4}}))

(facts "Merging edges from transitions."
  (fact "Working."
    (controller/merge-edges-from-trans :prefix {:bombi {:a 22}} {:foo {:a 3}} {} {})
    => {:foo {:a 3}, ":prefix#:bombi" {":prefix#:a" 22}}

    (controller/merge-edges-from-trans :prefix {:bombi {:a 22}} {:foo {:a 3}} {} {:bombi :foo})
    => {:foo {:a 3, ":prefix#:a" 22}}

    (controller/merge-edges-from-trans :prefix {:bombi {:a 22}} {:foo {:a 3}} {:a :a} {})
    => {:foo {:a 3}, ":prefix#:bombi" {:a 22}}

    (controller/merge-edges-from-trans :prefix {:bombi {:a 22}} {:foo {:a 3}} {:a :a} {:bombi :foo})
    => {:foo {:a 3}}))

(facts "Merging transitions from two nets."
  (fact "Working."
    (controller/merge-transitions :first #{:bombe :bombi} #{:foo} {}) => #{:foo ":first#:bombe" ":first#:bombi"}
    (controller/merge-transitions :first #{:bombe :bombi} #{:foo} {:bombe :foo}) => #{:foo ":first#:bombi"}))

;; add facts about merge-net

;;;; Getting to the attributes

(facts "Check if transition is alive"
  (fact "Working"
    (controller/transition-alive :foo) => nil))
