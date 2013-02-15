;not bad, but ccw functionality lost: switching to this namespace(on load and on demand)
(clojure.core/in-ns 'random.learning.clojure.usetheoverridexns)
(clojure.core/require '[clojure.core :as cc])

;  (:refer-clojure :exclude [sorted?])
  (cc/use '[random.learning.clojure.overridex])
  ;(:refer-clojure :exclude [sorted?])
  ;(:require [random.learning.clojure.overridex])
  ;)

(cc/println clojure.core/*ns*)
;the following only works if pasted in REPL, cause ccw seems to be loading clojure.core after executing this file
#_(clojure.core/map 
  #(clojure.core/ns-unmap clojure.core/*ns* %)
  (clojure.core/keys 
    (clojure.core/ns-publics 'clojure.core)
    )
  )

;require
;(refer '[clojure.core :as cc])

(cc/and 
  (cc/= false (sorted? '(1 2)))
  (cc/= true (sorted? (cc/sorted-set 1 2))))

;;
;; Clojure 1.5.0-RC16
;; Switching to random.learning.clojure.usetheoverridexns namespace
;#<Namespace random.learning.clojure.usetheoverridexns>
;true
;IllegalStateException sorted? already refers to: #'random.learning.clojure.overridex/sorted? in namespace: random.learning.clojure.usetheoverridexns  clojure.lang.Namespace.warnOrFailOnReplace (Namespace.java:88)
;=> cc/*ns*
;CompilerException java.lang.RuntimeException: No such namespace: cc, compiling:(NO_SOURCE_PATH:1:42) 

