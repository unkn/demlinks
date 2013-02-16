(ns hermes.stuff.hermesutil
  (:require [runtime.q :as q] :reload-all)
  (:require [hermes.core :as g]
            [hermes.type :as t]
            [hermes.vertex :as v])
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
  )

(defn isOpen? [^Graph graph]
  {:pre [(assumedNonNilGraph graph)] }
  (.isOpen graph)
  )

(q/deftest test_openclose
  (let [g (g/open)]
    (= StandardTitanGraph (type g))
    (q/is (not (nil? g)))
    (q/is (isOpen? g))
    (q/is (nil? (shutdown g)))
    (= StandardTitanGraph (type g))
    (q/isnot (isOpen? g))
    )
  )

(q/deftest test_vertex1
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