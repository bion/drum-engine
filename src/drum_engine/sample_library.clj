(ns drum-engine.sample-library
  [:require [overtone.core :as ot]])

(if-not (ot/server-connected?) (ot/boot-server))

;; load drum samples into map with k/v pairs:
;; { :sample-name #<buffer[live]: the-sample.aiff> }
;;
;; each sample has keys:
;; :id :size :n-channels :rate :status :path
;; :args :name :rate-scale :duration :n-samples
;; :spectral-group
(def drum-samples
  (reduce
   (fn [sample-map samp]
     (let [sample-name (-> samp
                           :name
                           (clojure.string/split  #"\.")
                           first
                           keyword)
           spectral-group (-> samp
                              :path
                              (clojure.string/split #"\/")
                              reverse
                              second
                              keyword)
           samp (assoc samp
                       :spectral-group spectral-group
                       :name sample-name)]

       (assoc sample-map sample-name samp)))
   {}
   (ot/load-samples "resources/samples/**/*")))

(ot/defsynth play-buf-simple [buf 0 amp 1]
  (let [sig (ot/play-buf:ar :num-channels 1
                            :bufnum buf
                            :rate (ot/buf-rate-scale:kr buf)
                            :action 2)]
    (ot/out:ar 0 (* amp sig))))
