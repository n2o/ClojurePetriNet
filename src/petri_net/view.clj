(ns petri-net.view
  (:gen-class)
  (:use [seesaw.core])
  (:use [seesaw.mig])
  (:require [petri-net.api :as api])
  (:require clojure.pprint))

;;;; Model

(def nets
  (scrollable (listbox :model api/get-net-names)
              :border "Nets"))

(def places
  (scrollable (listbox :model (for [net api/get-net-names] (api/get-places net)))
              :border "Places"))

(def database
  (scrollable (listbox :model @api/get-nets)))


;;;; Setting up the layout

(def left-grid (grid-panel :border "Database"
                           :columns 1
                           :items [nets
                                   places]
                           :vgap 5 :hgap 5))

(def mid-grid (grid-panel :border "Mid-Grid"
                          :columns 1
                          :items []
                          :vgap 5 :hgap 5))

(def right-grid (grid-panel :border "Right-Grid"
                            :columns 1
                            :items []
                            :vgap 5 :hgap 5))

(def main-frame
  (frame
   :title "Petri-Net Simulator"
   :minimum-size [640 :by 480]
   :on-close :exit
   :content (horizontal-panel
             :items [left-grid mid-grid right-grid])))


;;;; Auxiliary functions

(defn display [content]
  (config! main-frame :content content)
  content)

;;;; Designing the UI

(defn -main [& args]
  (native!)
  (-> main-frame
      pack!
      show!))
  
  ;;(display nets)
  ;;(display (left-right-split (scrollable nets) (scrollable places) :divider-location 1/2))
  ;;(display (left-right-split (scrollable nets) (scrollable places) :divider-location 1/2))


