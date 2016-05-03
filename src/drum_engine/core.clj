(ns drum-engine.core
  [:use
   [seesaw.core]
   [drum-engine.storage]
   [drum-engine.gui]
   [drum-engine.sample-library]])

;; TODO: default-state should reflect real num pads
(def default-state {:0 :nuggs_song3_clap_snare
                    :1 :nuggs_song1_rev_clap})

;; TODO: Initialize store based on config data num pads
(def store
  (let [config (if (db-present?)
                 (load-pad-samples)
                 default-state)]
    (atom config)))

(def midi-sample-manager-frame
  (frame :title "MIDI Sample Manager"
         :size [910 :by 200]))

(declare render)
(defn sample-selected [pad-name sample-name]
  (swap! store (fn [state]
                 (assoc state pad-name (keyword sample-name))))
  (println @store)
  (render))

(defn render []
  (config! midi-sample-manager-frame :content (gui-content @store sample-selected)))

(native!) ;; lolwut

(defn -main [& args]
  (invoke-later (show! midi-sample-manager-frame)
                (render)))
(-main)
(render)
