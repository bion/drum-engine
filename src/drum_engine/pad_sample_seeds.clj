(ns drum-engine.pad-sample-seeds)

(def andrew-drumkat-names-notes
  ;; pad name -> midi note
  [1 62
   2 61
   3 64
   4 63
   5 66
   6 68
   7 70
   8 69
   9 65
   10 78
   11 56
   12 36])

(def pad-sample-db-seeds
  (concat
   (for [[name note] (partition 2 andrew-drumkat-names-notes)]
     {:device-name :andrew_drumkat
      :pad-name (str name)
      :sample-name :ClickPop
      :pad_midi_note note
      :pad_channel_num 9})

  (for [note (range 7)]
    {:device-name :marshall_alesis
     :pad-name (str (+ 1 note))
     :sample-name :ClickPop
     :pad_midi_note note
     :pad_channel_num 0})))
