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
                             "(pad_name TEXT NOT NULL, "
                             "id INTEGER PRIMARY KEY, "
                             "sample_name NOT NULL)"))
        (catch Exception e (println e)))))

(defn create-pad-sample!
  ([pad-name sample-name] (create-pad-sample! default-db-path pad-name sample-name))
  ([path pad-name sample-name]
   (insert! (db path) :pad_samples {:pad_name (name pad-name)
                                    :sample_name (name sample-name)})))

(defn load-pad-samples
  ([] (load-pad-samples default-db-path))
  ([path]
   (let [pad-samples (query (db path) "SELECT * FROM pad_samples")]
     (reduce (fn [coll sample-record] (assoc coll
                                             (keyword (:pad_name sample-record))
                                             (keyword (:sample_name sample-record))))
             {}
             pad-samples))))

(defn update-pad-sample!
  ([pad-name sample-name] (update-pad-sample! default-db-path pad-name sample-name))
  ([path pad-name sample-name]
   (update! (db path)
            :pad_samples
            {:sample_name (name sample-name)}
            ["pad_name = ?" (name pad-name)])))

(defn file-exists? [path]
  (.exists (clojure.java.io/as-file path)))

(defn db-present?
  ([] (db-present? default-db-path))
  ([path] (file-exists? path)))
