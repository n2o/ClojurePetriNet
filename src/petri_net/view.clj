(ns petri-net.view
  (:gen-class)
  (:use [seesaw.core])
  (:use [seesaw.mig])
  (:require [petri-net.api :as api]))

;;;; Data from Database

(def nets (listbox :model api/get-net-names))
(def places (listbox))
(def edges-to-trans (listbox))
(def edges-from-trans (listbox))
(def transitions (listbox))


;;;; Buttons

(def b-add-place
  (button :text "Add Place" :enabled? false))

(def b-add-transition
  (button :text "Add Transition" :enabled? false))

;;;; Setting up the layout

(def left-grid (grid-panel :border "Database"
                           :columns 1
                           :items [(scrollable nets :border "Nets")
                                   (scrollable places :border "Places")
                                   (scrollable edges-to-trans :border "Edges from Places to Transitions")
                                   (scrollable edges-from-trans :border "Edges from Transitions to Places")
                                   (scrollable transitions :border "Transitions")]
                           :vgap 5 :hgap 5))

(def mid-grid (grid-panel :border "Mid-Grid"
                          :columns 1
                          :items []
                          :vgap 5 :hgap 5))

(def right-grid (grid-panel :border "Right-Grid"
                            :columns 1
                            :items [b-add-place
                                    b-add-transition]
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

(defn update-boxes! []
  (config! places :model (api/get-places (selection nets)))
  (config! edges-to-trans :model (api/get-edges-to-trans (selection nets)))
  (config! edges-from-trans :model (api/get-edges-from-trans (selection nets)))
  (config! transitions :model (api/get-transitions (selection nets))))

(defn activate-buttons! []
  (let [buttons [b-add-place b-add-transition]]
    (if (nil? (selection nets))
      (config! buttons :enabled? false)
      (config! buttons :enabled? true))))

;;;; Defining listener

(defn l-boxes [e]
  (update-boxes!)
  (activate-buttons!))

(defn l-add-place [e]
  (let [name (read-string (input "Name of place:"))
        tokens (read-string (input "Number of tokens for initialization:"))]
    (api/add-place (selection nets) name tokens)
    (update-boxes!)))


;;;; Designing the UI

(defn -main [& args]
  (native!)
  (-> main-frame
      pack!
      show!)
  (listen nets :selection l-boxes)
  (listen b-add-place :action l-add-place))
