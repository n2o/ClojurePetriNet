(ns petri-net.simulator
  (:require [petri-net.api :as api]))

(defn vec-to-map
  "Takes an vector of vectors and translates it into a hash-map.
  For example: [[:a 2] [:b 3]] => {:a 2, :b 3}"
  [input]
  (apply merge (map #(hash-map (first %) (second %)) input)))

(defn fireable
  "Checks if place has enough tokens to fire."
  [net [place tokens]]
  (let [available (api/get-tokens net place)]
    (<= 0 (- available tokens))))

;TODO Simplify!
(defn transition-alive
  "Takes the current net and a variable number of transitions and checks if one of the transitions are alive."
  ([net t]
     (let [trans (api/get-edges-to-trans net)
           place-token (apply hash-map
                              (remove nil?
                                      (flatten
                                       (for [[k v] trans]
                                         (for [[foo tokens] v]
                                           (when (= t foo)
                                             [k tokens]))))))]
       (every? identity (for [check place-token] (fireable net check)))))
  ([net t & ts]
     (some true?
             (concat (list (transition-alive net t))
                     (for [this ts] (transition-alive net this))))))
(transition-alive :first :bombe :bombi)
 
@api/get-nets

(def wowlist {:z {:bombe 4 :bombi 42}, :p {:bombi 43}})
