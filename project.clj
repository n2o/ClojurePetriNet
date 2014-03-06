(defproject petri-net "1.0"
  :description "Simple Petri Net Simulator in Clojure"
  :license {:name "MIT License"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/algo.monads "0.1.4"]
                 [seesaw "1.4.2"]]
  :main ^:skip-aot petri-net.view
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[midje "1.6.2"]]
                   :plugins [[lein-midje "3.1.3"]]}})
