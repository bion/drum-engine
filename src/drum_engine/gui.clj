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

(defn sample-select [label-prefix [id {:keys [sample-name pad-name]}]]
  (let [label (str label-prefix " pad " (name pad-name) ":")
        box (combobox-for id pad-name sample-name)
        button (sample-button-for sample-name)]
    [label box button]))

(defn pad-sorter [[id-one pad-one] [id-two pad-two]]
  (let [pad-name-one (:pad-name pad-one)
        pad-name-two (:pad-name pad-two)]
    (compare pad-name-one pad-name-two)))

(defn build-items [store sample-selected]
  (binding [*sample-selected* sample-selected]
    (let [{:keys [andrew_drumkat marshall_alesis]}
          (group-by (fn [[id {:keys [device-name]}]] device-name) store)

          andrew_drumkat (sort pad-sorter andrew_drumkat)
          marshall_alesis (sort pad-sorter marshall_alesis)]
      (flatten
       (concat
        (map (partial sample-select "Nugs") andrew_drumkat)
        (map (partial sample-select "Mars") marshall_alesis))))))

(defn gui-content [store sample-selected]
  (flow-panel ;; using dumbest possible layout for now
   :align :left
   :hgap 20
   :items (build-items store sample-selected)))
