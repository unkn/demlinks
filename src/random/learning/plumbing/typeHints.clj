(ns random.learning.plumbing.typeHints
  (:require [plumbing.graph :as graph])
  (:use plumbing.core)
  (:use clojure.test)
  )
;great typehints are working!

(set! *warn-on-reflection* true)

(defn stats 
  "Take a map {:xs xs} and return a map of simple statistics on xs"
  [{:keys [xs] :as m}]
  (assert (contains? m :xs))
  (let [n  (count xs)
        m  (/ (sum identity xs) n)
        m2 (/ (sum #(* % %) xs) n) 
        v  (- m2 (* m m))
        ^{:tag java.io.File} ffull (.getAbsoluteFile (clojure.java.io/as-file "flat-file"))
        ]
    {:n n   ; count   
     :m m   ; mean 
     :m2 m2 ; mean-square
     :v v   ; variance
     }))

(def stats-graph
  "A graph specifying the same computation as 'stats'"
  {:n  (fnk [xs]   (count xs))
   :m  (fnk [xs n] (/ (sum identity xs) n))
   :m2 (fnk [xs n] (/ (sum #(* % %) xs) n))
   :v  (fnk [m m2] (- m2 (* m m)))
   :ff (fnk [] (clojure.java.io/as-file "flat-file"))
   :ffull (fnk [
                ;^java.io.File
                ^{:tag java.io.File} 
                ff] ; ffull
            (.getAbsoluteFile ff);(clojure.java.io/as-file "flat-file"))
            )
   })

;(require '[plumbing.graph :as graph])
(def stats-eager (graph/eager-compile stats-graph))


;; Missing :xs key exception
(deftest t1
  #_(is (= {:n 4
          :m 3
          :m2 (/ 25 2)
          :v (/ 7 2)}
        (stats-eager {:xs [1 2 3 6]}))
    )
  (is (thrown? Throwable (stats-eager {:ys [1 2 3]})))
  )

;(println (.getAbsoluteFile ff))
(println (:ffull (stats-eager {:xs [1 2 3 6]})))

(def extended-stats  
  (graph/eager-compile 
    (assoc stats-graph
      :sd (fnk [^double v] (Math/sqrt v)))))

(deftest t2
  #_(is (= {:n 4
          :m 3
          :m2 (/ 25 2)
          :v (/ 7 2)
          :sd (Math/sqrt 3.5)}
        (extended-stats {:xs [1 2 3 6]})) )
  )

(run-tests)