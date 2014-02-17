(ns petri-net.api
  (:require [petri-net.core :as controller]))

controller/nets
(def get-nets controller/nets)
get-nets
(defn get-net [net] (println net))
(get-net :first)
