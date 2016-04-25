(ns drum-engine.gui
  [:use
   [seesaw.core]
   [drum-engine.sample-library]])

(def int->keyword (comp keyword str))

(defn combobox-for [num]
  (let [samples (vals drum-samples)
        names (map (comp name :name) samples)]
    (combobox :model names :id ((comp keyword str) num))))

(defn play-sample-at [get-content num event]
  (let [box-key (int->keyword num)
        sample-name (keyword (box-key (get-content)))
        sample-buf (sample-name drum-samples)]
    (play-buf-simple sample-buf)))

(defn sample-button-for [get-content num]
  (let [action (partial play-sample-at get-content num)]
    (button :text ">" :listen [:action action])))

(defn sample-select [get-content num]
  (let [label (str "Pad " num ":")
        box (combobox-for num)
        button (sample-button-for get-content num)]
    [label box button]))
