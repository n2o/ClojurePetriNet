(ns petri-net.api
  (:require [petri-net.core :as controller]))

controller/nets

(def nets controller/nets)
nets

(defn net [net] (net @nets))
(net :first)


