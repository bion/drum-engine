(ns drum-engine.core
  [:use
   [seesaw.core]
   [drum-engine.storage]
   [drum-engine.gui]
   [drum-engine.play]
   [drum-engine.sample-library]])

(def store
  (let [config (if (db-present?)
                 (load-pad-samples)
                 (do (create-db!) (load-pad-samples)))]
    (atom config)))

(def midi-sample-manager-frame
  (frame :title "MIDI Sample Manager"
         :size [910 :by 200]
         :on-close :exit))

(declare render)
(defn sample-selected [id sample-name]
  (swap! store (fn [state]
                 (update-in state [id]
                            assoc :sample-name (keyword sample-name))))
  (update-pad-sample! id sample-name)
  (println (get @store id))
  (render))

(defn render []
  (current-sample-set! @store)
  (config! midi-sample-manager-frame :content (gui-content @store sample-selected)))

(native!) ;; lolwut

(defn -main [& args]
  (invoke-later (show! midi-sample-manager-frame)
                (render)))

;; (-main)
;; (render)
