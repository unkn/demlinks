; Copyright (c) 2012-2013, AtKaaZ
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.

(ns titan.coree
  (:require [runtime.q :as q])
  (:require [clojure.java.io :as io])
  (:import 
    (com.thinkaurelius.titan.core TitanFactory)
    (com.thinkaurelius.titan.graphdb.database   StandardTitanGraph)
    (com.thinkaurelius.titan.graphdb.vertices   PersistStandardTitanVertex)
    (com.thinkaurelius.titan.graphdb.blueprints TitanInMemoryBlueprintsGraph)
    (com.tinkerpop.blueprints Graph)
    )
  )


(defn assumedNonNilGraph [graph]
  (q/assumedNotNil [graph "you passed nil graph"])
  )

(defn open [id & [conf]]
  (condp = id
    :memory (do
              (when-not (nil? conf)
                (q/log :warn "ignored conf parameter `" conf "`")
                )
              (TitanFactory/openInMemoryGraph)
              )
    :bdbje (cond
             (string? conf) (TitanFactory/open conf)
             (map? conf) (q/ni "not implemented" id conf)
             :else
             (q/throBadParams "you pass wrong conf type `" conf "`")
             )
    (q/throBadParams 
      "you pass wrong params" 
      id 
      "optional: " 
      conf 
      )
    )
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

(defn isOpen? 
  [^Graph graph]
  {:pre [(assumedNonNilGraph graph)]}
  (.isOpen graph)
  )

(defn getGraphType
  [^Graph graph]
  {:pre [(assumedNonNilGraph graph)]}
  (condp = (type graph)
    StandardTitanGraph {:type :bdbje}
    TitanInMemoryBlueprintsGraph {:type :memory}
    (q/throUnexpected "wrong graph type" (type graph) graph)
    )
  )

(q/deftest test_originalOpenClose
  (let [g (open)]
    (= StandardTitanGraph (type g))
    (q/is (not (nil? g)))
    (q/is (.isOpen g))
    (q/is (nil? (.shutdown g)))
    (= StandardTitanGraph (type g))
    #_(q/isnot (isOpen? g));FIXME: re-enable this after titan fixed this: https://github.com/thinkaurelius/titan/issues/156
    )
  )

(q/deftest test_newOpenClose
  (let [
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