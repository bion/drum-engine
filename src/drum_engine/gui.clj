(ns drum-engine.gui
  [:use
   [seesaw.core]
   [drum-engine.sample-library]
   [drum-engine.play]])

(def ^:dynamic *sample-selected* nil)
(def int->keyword (comp keyword str))

(defn combobox-for [id pad-name current-sample-name]
  (let [samples (vals drum-samples)
        names (map (comp name :name) samples)
        box (combobox :model names :id id)
        callback *sample-selected*]
    (selection! box (name current-sample-name))
    (listen box :selection (fn [e] (callback id (selection e))))
    box))

(defn play-sample-at [sample-name event]
  (let [sample-buf (sample-name drum-samples)]
    (play-buf-simple sample-buf)))

(defn sample-button-for [sample-name]
  (let [action (partial play-sample-at sample-name)]
    (button :text ">" :listen [:action action])))

(defn sample-select [[id {:keys [sample-name pad-name]}]]
  (let [label (str "Pad " (name pad-name) ":")
        box (combobox-for id pad-name sample-name)
        button (sample-button-for sample-name)]
    [label box button]))

(defn build-items [store sample-selected]
  (binding [*sample-selected* sample-selected]
    (flatten (map sample-select store))))

;; TODO
;; add a 'connect' button that calls
;; current-sample-set!
;; with the current pad-samples
(defn gui-content [store sample-selected]
  (flow-panel ;; using dumbest possible layout for now
   :align :left
   :hgap 20
   :items (build-items store sample-selected)))
