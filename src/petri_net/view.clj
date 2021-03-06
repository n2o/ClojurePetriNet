(ns petri-net.view
  (:gen-class)
  (:use [seesaw.core])
  (:use [seesaw.mig])
  (:require [petri-net.api :as api])
  (:require [petri-net.simulator :as simulator])
  (:require [seesaw.chooser :as chooser]))

(native!)

;;;; Data from Database

(def nets (listbox :model (api/get-net-names)))
(def places (listbox))
(def edges-to-trans (listbox))
(def edges-from-trans (listbox))
(def transitions (listbox))
(def properties (listbox))

;;;; Buttons

(def b-add-place
  (button :text "Add place" :enabled? false))
(def b-add-transition
  (button :text "Add transition" :enabled? false))
(def b-add-edge-to-transition
  (button :text "Add edge from place to transition" :enabled? false))
(def b-add-edge-from-transition
  (button :text "Add edge from transition to place" :enabled? false))
(def b-load-db
  (button :text "Load database"))
(def b-save-db
  (button :text "Save database"))
(def b-load-net
  (button :text "Load net"))
(def b-save-net
  (button :text "Save net" :enabled? false))
(def b-new-net
  (button :text "New net"))
(def b-delete-net
  (button :text "Delete net" :enabled? false))
(def b-copy-net
  (button :text "Copy selected net" :enabled? false))
(def b-merge-net
  (button :text "Merge nets" :enabled? false))
(def b-sim-fire
  (button :text "Fire selected transition" :enabled? false))
(def b-sim-fire-random
  (button :text "Fire random transition" :enabled? false))
(def b-net-alive
  (button :text "Net alive?" :enabled? false))
(def b-transition-alive
  (button :text "Transition alive?" :enabled? false))
(def b-non-empty
  (button :text "Non empty?" :enabled? false))
(def b-delete-property
  (button :text "Delete property" :enabled? false))
(def b-negate-property
  (button :text "Negate property" :enabled? false))
(def b-combine-with-or
  (button :text "Combine properties with or" :enabled? false))


;;;; Textfields

(def t-sim-fire-n-times
  (text :text 1 :columns 5))

;;;; Setting up the layout

(def group-sim-automatic
  (grid-panel
   :border "Fire alive transition"
   :columns 2
   :items ["Do times (greater zero):" t-sim-fire-n-times
           " " b-sim-fire-random]))

(def group-net-actions
  (vertical-panel
   :border "Net Actions"
   :items [(flow-panel :align :left :items [b-new-net b-delete-net b-copy-net b-merge-net])
           (flow-panel :align :left :items [b-add-place b-add-transition])
           (flow-panel :align :left :items [b-add-edge-to-transition b-add-edge-from-transition])]))

(def group-sim
  (vertical-panel
   :border "Simulator"
   :items [(flow-panel :align :left :items [b-sim-fire])
           group-sim-automatic]))

(def group-properties
  (vertical-panel
   :border "Set up some properties"
   :items [(flow-panel :align :left :items [b-net-alive b-transition-alive b-non-empty])
           (horizontal-panel
            :border "Current"
            :items [(scrollable properties)])
           (flow-panel
            :align :left
            :border "Actions for selected properties"
            :items [b-delete-property b-negate-property b-combine-with-or])]))

(def left-grid
  (grid-panel :border "Database"
              :columns 1
              :items [(scrollable nets :border "Nets")
                      (scrollable places :border "Places")
                      (scrollable edges-to-trans :border "Edges from place to transition")
                      (scrollable edges-from-trans :border "Edges from transition to place")
                      (scrollable transitions :border "Transitions")]
              :vgap 0 :hgap 0))

(def mid-grid
  (grid-panel :columns 3
              :items []
              :vgap 5 :hgap 5))

(def right-grid
  (grid-panel
   :columns 1
   :items [group-net-actions
           group-sim
           group-properties]
   :vgap 5 :hgap 5))

;;;; Auxiliary functions

(defn selected-property
  "Get 'real' property and remove {true false} <= in front of property."
  [prop]
  (read-string (second (re-find #".+ <= (.+)" prop))))

;;;; Update textboxes and buttons

(defn update-properties!
  "Update the properties for selected net."
  []
  (when-let [net (selection nets)]
    (let [props (api/get-properties net)
          props-e (eval (api/get-properties net))
          output (map #(str %1 " <= " %2) props-e props)]
      (config! properties :model output))))

(defn update-boxes!
  "Updates all textboxes when something has changed."
  []
  (when-let [net (selection nets)]
    (config! places :model (api/get-places net))
    (config! edges-to-trans :model (api/get-edges-to-trans net))
    (config! edges-from-trans :model (api/get-edges-from-trans net))
    (config! transitions :model (api/get-transitions net))))

(defn update-nets!
  "Update listbox showing the nets."
  []
  (config! nets :model (api/get-net-names)))

(defn toggle-buttons!
  "Disables buttons if no net or transition is selected. Otherwise activate them."
  []
  (let [net [b-add-place b-add-transition b-add-edge-to-transition b-add-edge-from-transition
             b-save-net b-delete-net b-copy-net b-merge-net
             b-sim-fire-random
             b-net-alive]
        trans [b-sim-fire
               b-transition-alive]
        place [b-non-empty]
        props [b-delete-property b-negate-property b-combine-with-or]]
    (if (nil? (selection nets))
      (config! net :enabled? false)
      (config! net :enabled? true))
    (if (nil? (selection transitions))
      (config! trans :enabled? false)
      (config! trans :enabled? true))
    (if (nil? (selection places))
      (config! place :enabled? false)
      (config! place :enabled? true))
    (if (or (nil? (selection nets))
            (zero? (count (api/get-properties (selection nets)))))
      (config! props :enabled? false)
      (config! props :enabled? true))))

;;;; Defining listener

(defn l-boxes [e]
  (update-boxes!)
  (toggle-buttons!)
  (update-properties!))

(defn l-buttons [e]
  (toggle-buttons!)
  (update-properties!))

(defn l-add-place [e]
  (when-let [name (input "Name of place:")]
    (when-let [tokens (input "Number of tokens for initialization:" :value 0)]
      (api/add-place (selection nets) (read-string name) (read-string tokens))
      (update-boxes!)
      (update-properties!))))

(defn l-add-transition [e]
  (when-let [name (input "Name of transition:")]
    (api/add-transition (selection nets) (read-string name))
    (update-boxes!)
    (update-properties!)))

(defn l-add-edge-to-transition [e]
  (when-let     [from   (input "[From?]  Name of place:")]
    (when-let   [to     (input "[To?]    Name of transition:")]
      (when-let [tokens (input "[Costs?] Weight of edge:" :value 1)]
        (api/add-edge-to-transition (selection nets) (read-string from) (read-string to) (read-string tokens))
        (update-boxes!)
        (update-properties!)))))

(defn l-add-edge-from-transition [e]
  (when-let     [from   (input "[From?]  Name of transition:")]
    (when-let   [to     (input "[To?]    Name of place:")]
      (when-let [tokens (input "[Costs?] Weight of edge:" :value 1)]
        (api/add-edge-from-transition (selection nets) (read-string from) (read-string to) (read-string tokens))
        (update-boxes!)
        (update-properties!)))))

(defn l-new-net [e]
  (when-let [name (input "Name of new net:")]
    (api/new-net (read-string name))
    (update-nets!)
    (update-properties!)
    (selection! nets (read-string name))))

(defn l-delete-net [e]
  (api/delete-net (selection nets))
  (update-nets!)
  (update-boxes!)
  (update-properties!)
  (toggle-buttons!))

(defn l-copy-net [e]
  (when-let [name (input "Name of the new net:")]
    (api/copy-net (selection nets) (read-string name))
    (update-nets!)
    (update-boxes!)
    (update-properties!)
    (toggle-buttons!)
    (selection! nets (read-string name))))

(defn l-merge-net [e]
  (when-let       [net1   (input "[net1] Type first net to be merged:")]
    (when-let     [net2   (input "[net2] Type second net to be merged:")]
      (when-let   [equal-places (input "[equal-places] Which places should be merged?\nType a map like: {:a :b} to merge :a and :b"
                                       :value {})]
        (when-let [equal-trans  (input "[equal-trans] Which transitions should be merged?\nType a map like: {:a :b} to merge :a and :b"
                                       :value {})]
          (api/merge-net (read-string net1) (read-string net2) (read-string equal-places) (read-string equal-trans))
          (update-nets!))))))

(defn l-sim-fire [e]
  (when-let [select (selection transitions)]
    (simulator/fire (selection nets) select)
    (update-boxes!)
    (selection! transitions select)))

(defn l-sim-fire-random [e]
  (dotimes [n (read-string (value t-sim-fire-n-times))]
    (when-let [net (selection nets)]
      (simulator/fire net (simulator/get-random-live-transition net))
      (update-boxes!)
      (selection! nets net))))

(defn l-save-net [e]
  (when-let [net (selection nets)]
    (api/save-net net)
    (alert (str "Net saved as: " (name net) ".dsl"))))

(defn l-load-net [e]
  (when-let [file (chooser/choose-file)]
    (api/load-net (.getPath file))
    (update-nets!)))

(defn l-save-db [e]
  (api/save-db)
  (alert "Database saved as: database.dsl"))

(defn l-load-db [e]
  (when-let [file (chooser/choose-file)]
    (api/load-db (.getPath file))
    (update-nets!)))

(defn l-net-alive [e]
  (api/add-property (selection nets) `(simulator/net-alive? ~(selection nets)))
  (update-properties!)
  (toggle-buttons!))

(defn l-transition-alive [e]
  (api/add-property (selection nets)
                    `(simulator/transition-alive? ~(selection nets) ~@(selection transitions {:multi? true})))
  (update-properties!)
  (toggle-buttons!))

(defn l-non-empty [e]
  (api/add-property (selection nets)
                    `(simulator/non-empty? ~(selection nets) ~@(map first (selection places {:multi? true}))))
  (update-properties!)
  (toggle-buttons!))

(defn l-delete-property [e]
  (when-let [prop (selection properties {:multi? true})]
    (let [selected (map selected-property prop)]
      (doall
       (map #(api/delete-property (selection nets) `~%) selected))
      (update-properties!)
      (toggle-buttons!))))

(defn l-negate-property [e]
  (when-let [prop (selection properties)]
    (let [selected (selected-property prop)]
      (api/delete-property (selection nets) `~selected)
      (api/add-property    (selection nets) `(not ~selected))
      (update-properties!)
      (toggle-buttons!))))

(defn l-combine-with-or [e]
  (when-let [prop (selection properties {:multi? true})]
    (when (<= 2 (count prop))
      (let [selected (map selected-property prop)]
        (doall
         (map #(api/delete-property (selection nets) `~%) selected))
        (api/add-property (selection nets) `(or ~@selected))
        (update-properties!)
        (toggle-buttons!)))))


;;;; Menubar

(defn l-exit [e] (System/exit 0))

(def menus
  (let [a-save-db (action :handler l-save-db :name "Save Database")
        a-load-db (action :handler l-load-db :name "Load Database" :tip "Overwrites the old database!")
        a-save-net (action :handler l-save-net :name "Save net")
        a-load-net (action :handler l-load-net :name "Load net")
        a-exit (action :handler l-exit :name "Exit" :tip "Exit the editor.")]
    (menubar
     :items [(menu :text "File" :items [a-save-db a-load-db a-save-net a-load-net a-exit])])))


;;;; Mainfunction to initialize the frame and call all needed listeners

(defn listeners []
  (listen nets :selection l-boxes)
  (listen transitions :selection l-buttons)
  (listen places :selection l-buttons)
  (listen b-add-place :action l-add-place)
  (listen b-add-transition :action l-add-transition)
  (listen b-add-edge-to-transition :action l-add-edge-to-transition)
  (listen b-add-edge-from-transition :action l-add-edge-from-transition)
  (listen b-new-net :action l-new-net)
  (listen b-delete-net :action l-delete-net)
  (listen b-copy-net :action l-copy-net)
  (listen b-merge-net :action l-merge-net)
  (listen b-sim-fire :action l-sim-fire)
  (listen b-sim-fire-random :action l-sim-fire-random)
  (listen b-net-alive :action l-net-alive)
  (listen b-transition-alive :action l-transition-alive)
  (listen b-non-empty :action l-non-empty)
  (listen b-delete-property :action l-delete-property)
  (listen b-negate-property :action l-negate-property)
  (listen b-combine-with-or :action l-combine-with-or))

(def main-frame
  (frame
   :title "Petri-Net Simulator"
   :minimum-size [640 :by 480]
   :on-close :exit
   :menubar menus
   :content (horizontal-panel
             :items [left-grid mid-grid right-grid])))

(defn -main [& args]
  (-> main-frame
      pack!
      show!)
  (listeners))
