(ns petri-net.simulator
  (:require [petri-net.api :as api]))

(defn vec-to-map
  "Takes an vector of vectors and translates it into a hash-map.
  For example: [[:a 2] [:b 3]] => {:a 2, :b 3}"
  [input]
  (apply merge (map #(hash-map (first %) (second %)) input)))

(defn fireable?
  "Checks if place has enough tokens to fire."
  [net [place tokens]]
  (let [available (api/get-tokens net place)]
    (when-not (nil? available)
      (<= 0 (- available tokens)))))

(defn transition-alive?
  "Takes the current net and a variable number of transitions and checks if one of the transitions are alive."
  ([net t]
     (let [place-token (api/get-places-to-transition net t)]
       (when-not (empty? place-token)
         (every? identity (map #(fireable? net %) place-token)))))
  ([net t & ts]
     (if (transition-alive? net t)
       true
       (some true? (map #(transition-alive? net %) ts)))))

(defn non-empty?
  "There exists at least one token in the specified places."
  ([net p]
     (fireable? net [p 0]))
  ([net p & ps]
     (if (fireable? net [p 0])
       true
       (some true? (map #(fireable? net [% 0]) ps)))))

(defn net-alive?
  "Checks if there exists at least one fireable transition."
  [net]
  (let [ts (apply list (api/get-transitions net))]
    (when-not (empty? ts)
      (some true? (map #(transition-alive? net %) ts)))))

(defn fire
  "Fires a specified transition."
  [net t]
  (when (transition-alive? net t)
    (let [from-places (api/get-places-to-transition net t)
          to-places   (api/get-places-from-transition net t)]
      (doall (for [[place token] from-places] (api/update-place net place token)))
      (doall (for [[place token] to-places] (api/update-place net place (- token)))))))

(defn get-random-live-transition
  [net]
  (let [ts        (vec (api/get-transitions net))
        num-trans (count ts)]
    (when (< 0 num-trans)
      (loop [n 0
             t (ts (rand-int num-trans))]
        (when (< n 100)
          (if (transition-alive? net t)
            t
            (recur (inc n) (ts (rand-int num-trans)))))))))
