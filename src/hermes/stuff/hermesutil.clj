(ns hermes.stuff.hermesutil
  (:require [runtime.q :as q] :reload-all)
  (:require [hermes.core :as g]
            [hermes.type :as t]
            [hermes.vertex :as v])
  (:import  
    (com.thinkaurelius.titan.graphdb.database   StandardTitanGraph)
    (com.thinkaurelius.titan.graphdb.vertices   PersistStandardTitanVertex))
  )

(defn shutdown
  [graph]
  {:pre [(q/assumedNotNil graph)]}
  (.shutdown 
    ;^StandardTitanGraph 
    ^com.tinkerpop.blueprints.Graph graph)
  )

(q/deftest test_openclose
  (let [g (g/open)]
    (q/is (not (nil? g)))
    (q/is (nil? (shutdown g)))
    (= StandardTitanGraph (type g))
    )
  )

;last lines:
(q/show_state)
(q/gotests)