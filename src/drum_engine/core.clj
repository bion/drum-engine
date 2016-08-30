(ns drum-engine.core
  [:use
   [seesaw.core]
   [drum-engine.storage]
   [drum-engine.gui]
   [drum-engine.play]
   [drum-engine.sample-library]])

(declare store)
(declare db-path)
(declare render)

(def default-db-name ".midi-sample-manager-storage.db")
(def default-db-path
  (str user-home-dir "/" default-db-name))

(defn get-store []
  (let [config (if (db-present? db-path)
                 (load-pad-samples db-path)
                 (do (create-db! db-path) (load-pad-samples db-path)))]
    (atom config)))

(def midi-sample-manager-frame
  (frame :title "MIDI Sample Manager"
         :size [1000 :by 1300]
         :on-close :exit))

(defn sample-selected [id sample-name]
  (swap! store (fn [state]
                 (update-in state [id]
                            assoc :sample-name (keyword sample-name))))
  (update-pad-sample! db-path id sample-name)
  (println (get @store id))
  (render))

(defn render []
  (current-sample-set! @store)
  (config! midi-sample-manager-frame :content (gui-content @store sample-selected)))

(defn start
  ([] (start default-db-path))
  ([path]
   (def db-path path)
   (def store (get-store))
   (render)))

(native!) ;; lolwut

(defn -main [& args]
  (invoke-later (show! midi-sample-manager-frame)
                (start)))

;; (-main)
;; (render)
