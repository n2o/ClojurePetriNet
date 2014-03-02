(ns petri-net.simulator
  (:require [petri-net.api :as api]))

(defn vec-to-map
  "Takes an vector of vectors and translates it into a hash-map.
  For example: [[:a 2] [:b 3]] => {:a 2, :b 3}"
  [input]
  (apply merge (map #(hash-map (first %) (second %)) input)))

(defn transition-alive
  "Takes the current net and a variable number of transitions and checks if one of the transitions are alive."
  [net t & ts]
  (let [trans (api/get-edges-to-trans net)]
    (println trans)
    (for [[k v] trans]
      (for [[foo tokens] v]
        (when (= t foo)
          (println "Res:" [k foo]))
        )
      )
    ))

(defn firable
  "Checks if place has enough token to get fired"
  [place]
  
  )


(transition-alive :first :bombe)
@api/get-nets

(def wowlist {:z {:bombe 4 :bombi 42}, :p {:bombi 43}})
