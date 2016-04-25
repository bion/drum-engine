(ns drum-engine.core
  [:require
   [overtone.core :as ot]]
  [:use
   [seesaw.core]
   [drum-engine.sample-library]])

(def nugs-num-samples 10)

(if-not (ot/server-connected?) (ot/boot-server))

(defn keyword->int [arg]
  (Integer. ((comp str name) arg)))

(def int->keyword (comp keyword str))

(ot/defsynth play-buf-simple [buf 0 amp 1]
  (let [sig (ot/play-buf:ar :num-channels 1
                            :bufnum buf
                            :rate (ot/buf-rate-scale:kr buf)
                            :action 2)]
    (ot/out:ar 0 (* amp sig))))

(declare gui-content)

(defn get-current-state []
  (value gui-content))

(defn combobox-for [num]
  (let [samples (vals drum-samples)
        names (map (comp name :name) samples)]
    (combobox :model names :id ((comp keyword str) num))))

(defn play-sample-at [num _]
  (let [box-key (int->keyword num)
        sample-name (keyword (box-key (get-current-state)))
        sample-buf (sample-name drum-samples)]
    (play-buf-simple sample-buf)))

(defn sample-button-for [num]
  (let [action (partial play-sample-at num)]
    (button :text ">" :listen [:action action])))

(defn sample-select [num]
  (let [label (str "Pad " num ":")
        box (combobox-for num)
        button (sample-button-for num)]
    [label box button]))

(def gui-content
  (flow-panel
   :align :left
   :hgap 20
   :items (flatten (map sample-select (range nugs-num-samples)))))

(defonce seesaw-frame
  (frame :title "MIDI Sample Manager"
         :size [800 :by 600]
         :on-close :exit))

(native!) ;; lolwut
(invoke-later
 (-> seesaw-frame
     pack!
     show!))

(config! seesaw-frame :size [800 :by 600])
(config! seesaw-frame :content gui-content)
