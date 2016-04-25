(ns drum-engine.core
  [:require
   [overtone.core :as ot]]
  [:use [seesaw.core]])

(def nugs-num-samples 10)

(if-not (ot/server-connected?) (ot/boot-server))

;; load drum samples into map with k/v pairs:
;; { :sample-name #<buffer[live]: the-sample.aiff> }
;;
;; each sample has keys:
;; :id :size :n-channels :rate :status :path
;; :args :name :rate-scale :duration :n-samples
;; :spectral-group
(def drum-samples
  (reduce
   (fn [sample-map samp]
     (let [sample-name (-> samp
                           :name
                           (clojure.string/split  #"\.")
                           first
                           keyword)
           spectral-group (-> samp
                              :path
                              (clojure.string/split #"\/")
                              reverse
                              second
                              keyword)
           samp (assoc samp :spectral-group spectral-group)]

       (assoc sample-map sample-name samp)))
   {}
   (ot/load-samples "resources/samples/**/*")))

(ot/defsynth play-buf-simple [buf 0 amp 1]
  (let [sig (ot/play-buf:ar :num-channels 1
                            :bufnum buf
                            :rate (ot/buf-rate-scale:kr buf)
                            :action 2)]
    (ot/out:ar 0 (* amp sig))))

(native!)

(defn combobox-for [spectral-group]
  (let [samples (filter #(= spectral-group (:spectral-group %)) drum-samples)
        names (map :name samples)]
    (combobox :model names)))

(defn sample-select [num]
  (let [label (str "Pad " num ":")
        boxes (combobox-for :med)]
    [label boxes]))

(def gui-content
  (flow-panel
   :align :left
   :hgap 20
   :items (flatten (map sample-select (range nugs-num-samples)))))

(def seesaw-frame
  (frame :title "MIDI Sample Manager"
         :size [800 :by 600]
         :on-close :exit))

(invoke-later
 (-> seesaw-frame
     pack!
     show!))

(config! seesaw-frame :size [800 :by 600])
(config! seesaw-frame :content gui-content)
