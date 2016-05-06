(ns drum-engine.storage
  [:use
   [clojure.java.jdbc]])

(def user-home-dir (System/getenv "HOME"))
(def default-db-name ".midi-sample-manager-storage.db")
(def default-db-path
  (str user-home-dir "/" default-db-name))

(defn db [path]
  {:classname "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname path})

(defn create-db!
  ([] (create-db! default-db-path))
  ([path]
   (try (db-do-commands (db path)
                        (str "CREATE TABLE pad_samples "
                             "(id INTEGER PRIMARY KEY, "
                             "pad_name TEXT NOT NULL, "
                             "sample_name TEXT NOT NULL, "
                             "pad_midi_note INTEGER NOT NULL, "
                             "pad_channel_num INTEGER NOT NULL)"))
        (catch Exception e (println e)))))

(defn create-pad-sample!
  ([pad-name sample-name pad_midi_note pad_channel_num]
   (create-pad-sample! default-db-path pad-name sample-name pad_midi_note pad_channel_num))
  ([path pad-name sample-name pad_midi_note pad_channel_num]
   (insert! (db path) :pad_samples {:pad_name (name pad-name)
                                    :sample_name (name sample-name)
                                    :pad_midi_note pad_midi_note
                                    :pad_channel_num pad_channel_num})))

(defn load-pad-samples
  ([] (load-pad-samples default-db-path))
  ([path]
   (let [pad-samples (query (db path) "SELECT * FROM pad_samples")]
     (reduce
      (fn [coll sample-record]
        (let [key (:id sample-record)
              value (assoc sample-record
                           :sample-name (-> sample-record :sample_name keyword)
                           :pad-name (-> sample-record :pad_name keyword))]
          (assoc coll key value)))
      {}
      pad-samples))))

(defn update-pad-sample!
  ([id sample-name] (update-pad-sample! default-db-path id sample-name))
  ([path id sample-name]
   (update! (db path)
            :pad_samples
            {:sample_name (name sample-name)}
            ["id = ?" id])))

(defn file-exists? [path]
  (.exists (clojure.java.io/as-file path)))

(defn db-present?
  ([] (db-present? default-db-path))
  ([path] (file-exists? path)))
