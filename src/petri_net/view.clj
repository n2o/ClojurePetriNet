(ns petri-net.view
  (:gen-class)
  (:use [seesaw.core])
  (:require [petri-net.core :as core]))

(defn -main [& args]
  (-> (frame :title "Wow") pack! show!))
