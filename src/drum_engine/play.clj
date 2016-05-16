(ns drum-engine.play
  [:use [drum-engine.sample-library]]
  [:require [overtone.core :as ot]])

(if-not (ot/server-connected?) (ot/boot-server))

(def LISTENER_NAME ::dispatcher)

;; (on-event [:midi :note-on]
;;                 #(if (and (= (-> % :device :description) "Samson Graphite M25")
;;                           (= (:note %) 40)
;;                           (= (:channel %) 9))
;;                    (sl/play-buf-simple (:nuggs_song1_heavy_snare sl/drum-samples) (:velocity-f %)))
;;                 ::play-sample)

(ot/defsynth play-buf-simple [buf 0 lin-amp 0.5]
  (let [amp (* 0.25 (ot/lin-exp:kr lin-amp 0 1 (ot/db->amp -22)))
        sig (ot/play-buf:ar :num-channels 1
                            :bufnum buf
                            :rate (ot/buf-rate-scale:kr buf)
                            :action 2)]
    (ot/out:ar 0 (* amp sig))))

(defn pad-for-sample-map [pad-samples]
  (reduce (fn [memo [id pad-sample]]
            (let [device-name (:device-name pad-sample)
                  key {:channel (:pad-channel-num pad-sample)
                       :note (:pad-midi-note pad-sample)
                       :device-description
                       (cond
                         (= device-name :marshall_alesis) "mio"
                         (= device-name :andrew_drumkat) "USB2.0-MIDI Port 1"
                         :else (println (str "Unknown device - " device-name)))}
                  buffer ((:sample-name pad-sample) drum-samples)]
              (assoc memo key buffer)))
          {}
          pad-samples))

(defn handler-for [pad-samples]
  (let [lookup-map (pad-for-sample-map pad-samples)]

    (fn [event]
      (let [lookup-key (select-keys event '(:note :channel))
            lookup-key (assoc lookup-key
                              :device-description (-> event :device :description))
            buffer (get lookup-map lookup-key)
            lin-amp (:velocity-f event)]

        (if-not buffer (println "No sample found for" lookup-key))
        (play-buf-simple buffer lin-amp)))))

(defn current-sample-set! [pad-samples]
  (ot/remove-event-handler LISTENER_NAME)
  (ot/on-event [:midi :note-on]
               (handler-for pad-samples)
               LISTENER_NAME))
