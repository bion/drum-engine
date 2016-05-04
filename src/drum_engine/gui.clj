(ns drum-engine.gui
  [:use
   [seesaw.core]
   [drum-engine.sample-library]])

(def ^:dynamic *sample-selected* nil)
(def int->keyword (comp keyword str))

(defn combobox-for [pad-name current-sample-name]
  (let [samples (vals drum-samples)
        names (map (comp name :name) samples)
        box (combobox :model names :id ((comp keyword str) num))
        callback *sample-selected*]
    (selection! box (name current-sample-name))
    (listen box :selection (fn [e] (callback pad-name (selection e))))
    box))

(defn play-sample-at [sample-name event]
  (let [sample-buf (sample-name drum-samples)]
    (play-buf-simple sample-buf)))

(defn sample-button-for [sample-name]
  (let [action (partial play-sample-at sample-name)]
    (button :text ">" :listen [:action action])))

(defn sample-select [[pad-name current-sample-name]]
  (let [label (str "Pad " (name pad-name) ":")
        box (combobox-for pad-name current-sample-name)
        button (sample-button-for current-sample-name)]
    [label box button]))

(defn build-items [store sample-selected]
  (binding [*sample-selected* sample-selected]
    (flatten (map sample-select store))))

(defn gui-content [store sample-selected]
  (flow-panel ;; using dumbest possible layout for now
   :align :left
   :hgap 20
   :items (build-items store sample-selected)))
