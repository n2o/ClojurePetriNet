(ns petri-net.core
  (:gen-class)
  (:use [seesaw core mig]))
 
(defn make-button [row col]
  (button :text (format "(%d, %d)" row col)
          :listen [:action
                   (fn [e] (alert (format "Hi, you clicked on (%d, %d)!" row col)))]))
 
(defn make-content []
  (mig-panel
   :constraints ["fill"]
   :items
   (let [rows 4 cols 4]
     (for [row (range rows) col (range cols)]
       [(make-button row col)
        (if (and (< row (dec rows)) (= col (dec cols))) "grow, wrap" "grow")]))))
 
(defn -main [& args]
  (native!)
  (-> (frame :title "com.icyrock.clojure.csjws.core"
             :content (make-content)
             :width 400
             :height 400
             :on-close :exit)
      show!))

(-main)
