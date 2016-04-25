(ns drum-engine.core
  [:use
   [seesaw.core]
   [drum-engine.gui]])

(def nugs-num-samples 10)

(def gui-content
  (flow-panel ;; using dumbest possible layout for now
   :align :left
   :hgap 20
   :items (flatten (map (partial sample-select #(value gui-content))
                        (range nugs-num-samples)))))

(def midi-sample-manager-frame
  (frame :title "MIDI Sample Manager"
         :size [910 :by 200]
         :content gui-content
         :on-close :exit))

(native!) ;; lolwut

(defn -main [& args]
  (invoke-later (show! midi-sample-manager-frame)))
