(ns drum-engine.core-test
  (:use [clojure.test]
        [drum-engine.core]
        [drum-engine.storage]
        [seesaw.core]))

(defn test-setup []
  (let [db-path "/tmp/drum-engine-test-db"]
    (if (db-present? db-path) (clojure.java.io/delete-file db-path))
    (start db-path)))

(deftest sample-is-selected-in-combobox-on-load-test
  (test-setup)
  (let [children (seesaw.util/children (.getContentPane midi-sample-manager-frame))
        first-sample-label (text (first children))]
    (is (= "Nugs pad 1:" first-sample-label))))
