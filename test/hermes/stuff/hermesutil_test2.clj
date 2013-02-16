delete me
; Copyright (c) 2012-2013, AtKaaZ
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.

(ns hermes.stuff.hermesutil-test
  (:require [hermes.stuff.hermesutil :as h])
  (:require [runtime.q :as q] :reload-all)
  (:require [hermes.core :as g]
            [hermes.type :as t]
            [hermes.vertex :as v])
  (:import  
    (com.thinkaurelius.titan.graphdb.database   StandardTitanGraph)
    (com.thinkaurelius.titan.graphdb.vertices   PersistStandardTitanVertex))
  )

(q/deftest test_openclose
  (let [g (g/open)]
    (q/is (not (nil? g)))
    (q/is (nil? (shutdown g)))
    (= StandardTitanGraph (type g))
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
