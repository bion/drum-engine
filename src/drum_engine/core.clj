(ns drum-engine.core
  [:use
   [seesaw.core]
   [drum-engine.storage]
   [drum-engine.gui]
   [drum-engine.sample-library]])

(def store
  (let [config (if (db-present?)
                 (load-pad-samples)
                 (do (create-db!) (load-pad-samples)))]
    (atom config)))

(def midi-sample-manager-frame
  (frame :title "MIDI Sample Manager"
         :size [910 :by 200]))

(declare render)
(defn sample-selected [pad-name sample-name]
  (swap! store (fn [state]
                 (assoc state pad-name (keyword sample-name))))
  (update-pad-sample! pad-name sample-name)
  (println @store)
  (render))

(defn render []
  (config! midi-sample-manager-frame :content (gui-content @store sample-selected)))

(native!) ;; lolwut

(defn -main [& args]
  (invoke-later (show! midi-sample-manager-frame)
                (render)))
;; (-main)
;; (render)
