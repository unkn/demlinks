(ns hermes.stuff.hermesutil
  (:require [runtime.q :as q] :reload-all)
  (:require [hermes.core :as g]
            [hermes.type :as t]
            [hermes.vertex :as v])
  (:require [datest1.ret :as r])
  (:import  
    (com.thinkaurelius.titan.graphdb.database   StandardTitanGraph)
    (com.thinkaurelius.titan.graphdb.vertices   PersistStandardTitanVertex)
    (com.tinkerpop.blueprints Graph)
    )
  )

(defn assumedNonNilGraph [graph]
  (q/assumedNotNil [graph "you passed nil graph"])
  )

(defn shutdown
  [graph]
  {:pre [(assumedNonNilGraph graph)]}
  (.shutdown 
    ;^StandardTitanGraph 
    ;^com.tinkerpop.blueprints.Graph 
    ^Graph graph)
  graph
  )

(defn assumeNotLeakedGraph
  []
  (q/assumedNil [
               g/*graph*
               "something bad happened and the graph leaked to *graph* 
ie. it's non-nil now; so you could've just used hermes functions and that's why"] )
  )

(defn pacifyGlobalGraphVarRoot
  []
  (alter-var-root #'g/*graph* (constantly nil))
  )

(defmacro hermesCall [ & forms ]
  (q/assumedFalse [
                   (empty? forms)
                   "you must pass something"
                   ])
  `(let [
        _# (assumeNotLeakedGraph)
        ret# ~@forms
        _# (pacifyGlobalGraphVarRoot)
        _# (assumeNotLeakedGraph)
        ]
    ret#
    )
  )

;(def memGraph :memory)
(r/defSym2Key KEY_InMemoryGraph :memory)
(r/defSym2Key KEY_BerkeleyDB :bdbje)

(defn
  open
  [& params]
  #_{:pre [
         (assumeNotLeakedGraph)
         ] 
   :post [(assumeNotLeakedGraph)]
   }
  (hermesCall
    (apply g/open params);XXX: (g/open params) would pass nil if no params on call
    )
;  {:pre [
;         (assumeNotLeakedGraph)
;         ] 
;   :post [(assumeNotLeakedGraph)]
;   }
;  
;  (let [ret 
;        _ (pacifyGlobalGraphVarRoot)
;        ]
;    ret
;    )
  )

#_(defn x []
  (binding [g/*graph* nil]
    (println g/*graph*)
    (alter-var-root #'g/*graph* (constantly 3)) ;changes root
    (println g/*graph*) ;not seen root
    g/*graph* ;always nil due to binding
    )
  )



#_(defn
  open
  [^Graph graph]
  )

(defn isOpen? [^Graph graph]
  {:pre [(assumedNonNilGraph graph)] }
  (.isOpen graph)
  )

(q/deftest test_originalOpenClose
  (let [g (g/open)]
    (= StandardTitanGraph (type g))
    (q/is (not (nil? g)))
    (q/is (.isOpen g))
    (q/is (nil? (.shutdown g)))
    (= StandardTitanGraph (type g))
    #_(q/isnot (isOpen? g));FIXME: re-enable this after titan fixed this: https://github.com/thinkaurelius/titan/issues/156
    )
  (q/isnot (= g/*graph* (g/open)))
  (q/is (= (g/open) g/*graph*))
  )

(q/deftest test_newOpenClose
  (let [
        _ (pacifyGlobalGraphVarRoot)
        g (open)]
    (q/isnot (nil? g))
    (q/is (isOpen? g))
    (let [sameG (shutdown g)]
      (q/is (identical? sameG g))
      #_(q/isnot (isOpen? sameG));FIXME: re-enable this after titan fixed this: https://github.com/thinkaurelius/titan/issues/156
      )
    )
  )

#_(q/deftest test_vertex1;TODO: uncomment this when fixed above open
  (let [vertex1 (v/create!)]
    (q/is 
      (= vertex1 
        (v/find-by-id (v/get-id vertex1))
        )
      )
    )
  )

;last lines:
(q/show_state)
(q/gotests)
;(q/showLocation)
;(defn x []
;  (q/showLocation)
;  (q/showHere 0 :info 121122 "some")
;  (q/log :info "something")
;  (q/logShift 1 :info "the call position of our function")
;  (q/logCaller :info "the call position of our function")
;  )
;
;(x)
