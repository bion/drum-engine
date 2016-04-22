(ns drum-engine.core
  [:require
   [overtone.core :as ot]])

(if-not (ot/server-connected?) (ot/boot-server))

;; load drum samples into map with k/v pairs:
;; { :sample-name #<buffer[live]: the-sample.aiff> }
;;
;; each sample has keys
;; :id :size :n-channels :rate :status :path
;; :args :name :rate-scale :duration :n-samples
(def drum-samples
  (reduce
   (fn [sample-map samp]
     (let [sample-name (-> samp
                           :name
                           (clojure.string/split  #"\.")
                           first
                           keyword)]
       (if (contains? sample-map sample-name)
         (throw (Exception. "not cool, duplicate sample name: " sample-name)))
       (assoc sample-map sample-name samp)))
   {}
   (ot/load-samples "resources/samples/**/*")))
