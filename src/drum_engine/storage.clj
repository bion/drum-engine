(ns drum-engine.storage)

(def user-home-dir (System/getenv "HOME"))
(def default-datafile-name ".midi-sample-manager-storage.clj")
(def default-datafile-path
  (str user-home-dir "/" default-datafile-name))

(defn file-exists? [path]
  (.exists (clojure.java.io/as-file path)))

(defn datafile-present?
  ([] default-datafile-path)
  ([path] (file-exists? path)))

(defn read-datafile
  ([] default-datafile-path)

  ([path]
   (if (file-exists? path)
     (load-file path)
     (throw (Exception. (str "no datafile found at " path))))))
