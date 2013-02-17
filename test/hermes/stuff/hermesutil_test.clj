; Copyright (c) 2012-2013, AtKaaZ
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.


(ns hermes.stuff.hermesutil-test
  (:require [runtime.q :as q]); :reload-all)
  (:require [clojure.java.io :as io])
  (:require [hermes.core :as g]
            [hermes.type :as t]
            [hermes.vertex :as v])
  (:require [datest1.ret :as r])
  (:require [hermes.stuff.hermesutil :as h])
  (:require [runtime.futils :as f]); :reload-all)
  (:import  
    (com.thinkaurelius.titan.graphdb.database   StandardTitanGraph)
    (com.thinkaurelius.titan.graphdb.vertices   PersistStandardTitanVertex)
    (com.thinkaurelius.titan.graphdb.blueprints TitanInMemoryBlueprintsGraph)
    )
  )

(set! 
  *warn-on-reflection*
  true)

(r/defSym2Key KEY_InMemoryGraph :memory)
(r/defSym2Key KEY_BerkeleyDB :bdbje)
(r/defSym2Key KEY_Cassandra :cassandra)
(r/defSym2Key KEY_HBase :hbase)

(def ^:dynamic *conf* (r/getExistingKey KEY_InMemoryGraph))

(defn beforeTests [aVar graphVar]
  (condp = *conf*
    ;case1
    (r/getExistingKey KEY_InMemoryGraph)
    (do
      (q/log :debug "memory")
      (var-set graphVar (g/open))
      )
    
    ;case2
    (r/getExistingKey KEY_BerkeleyDB) 
    (do
      (var-set aVar (f/getUniqueFolder))
      (let [^java.io.File fdir @aVar]
        (q/assumedNotNil fdir)
        (q/log :debug "using temporary folder: `\n" fdir "\n`")
        
        ;not explicit that we're using bdb:
        ;(g/open (.getAbsolutePath fdir))
        
        ;more explicit (that's we're using bdb:
        (var-set graphVar 
          (g/open {:storage {:backend "berkeleyje"
                             :directory (.getAbsolutePath fdir)}})
          )
        
        )
      );do

    ;none of the above:
    (q/thro "unexpected *conf* value=`" *conf* "`")
    )
  )

(defn afterTests [aVar graphVar]
  (q/assumedNotNil @graphVar)
  (h/shutdown @graphVar)
  (condp = *conf*
    ;case1
    (r/getExistingKey KEY_InMemoryGraph)
    (do
      (q/log :debug "memory graph afterTests fixture")
      )
    
    ;case2
    (r/getExistingKey KEY_BerkeleyDB) 
    (do
      (q/assumedNotNil @aVar)
      (q/assumedTrue
        [
         (f/deleteFolderRecursively @aVar true)
         "failed to delete temporary folder: `"
         @aVar
         "`"
         ])
      )
    
    ;none of the above:
    (q/thro "unexpected *conf* value=`" *conf* "`")
    )
  )

(defn testsFixture [testsHere]
  ;XXX: tests better cannot be run on parallel or stuff will fail
  (with-local-vars [avar1 nil avar2 nil];(q/getUniqueFolder)] 
    (try
      (do
        (beforeTests avar1 avar2)
        (testsHere)
        )
      (finally 
        (afterTests avar1 avar2)
        )
      )
    )
  )

(q/use-fixtures :once testsFixture)

(q/deftest test_vertex1
  (let [vertex1 (v/create!)]
    (q/is 
      (= vertex1 
        (v/find-by-id (v/get-id vertex1))
        )
      )
    )
  )

(q/deftest test_alreadyOpenForTests
  (let [g g/*graph*]
    (q/is (not (nil? g)))
    (q/is (h/isOpen? g))
    (q/is (contains? #{StandardTitanGraph 
                       TitanInMemoryBlueprintsGraph}
            (type g)))
    )
  )

(q/deftest test_openclose
  
  (q/is (not (nil? g/*graph*)))
  
  (let [g 
        ;g/*graph*
        (g/open)
        ]
    (q/is (not (nil? g)))
    (q/is (h/isOpen? g))
    (q/is (nil? (h/shutdown g)))
    (q/is (contains? #{StandardTitanGraph 
                       TitanInMemoryBlueprintsGraph}
            (type g)))
    (q/is (identical? g/*graph* g))
    (q/is (not (nil? g/*graph*)))
    (q/isnot (h/isOpen? g))
    )
  )




;the following test taken from hermes.persistent.core-test (it may be slightly modified by now) anyway, credit goes to those people who originally wrote it here: https://github.com/gameclosure/hermes
#_(q/deftest test-dueling-transactions
  (q/testing "Without retries"
    (g/transact!
      (t/create-vertex-key-once :vertex-id Long {:indexed true
                                                 :unique true}))
    (let [random-long (long (rand-int 100000))
          f1 (future (g/transact! (v/upsert! :vertex-id {:vertex-id random-long})))
          f2 (future (g/transact! (v/upsert! :vertex-id {:vertex-id random-long})))
          ]

      ;XXX: this doesn't happen in bdbje, dno why; but should work with cassandra
      #_(q/is (thrown? java.util.concurrent.ExecutionException
        (do @f1 @f2)) "The futures throw errors.")
      
      (q/is (= random-long
             (g/transact!
               (v/get-property (v/refresh (first @f1)) :vertex-id))
             (g/transact!
               (v/get-property (v/refresh (first @f2)) :vertex-id))) "The futures have the correct values.")

      (q/is (= 1 (count
        (g/transact! (v/find-by-kv :vertex-id random-long))))
        "*graph* has only one vertex with the specified vertex-id")
      )
    )

  (q/testing "With retries"
    (g/transact!
      (t/create-vertex-key-once :vertex-id Long {:indexed true
                                                 :unique true}))
    (let [random-long (long (rand-int 100000))
          f1 (future (g/retry-transact! 3 100 (v/upsert! :vertex-id {:vertex-id random-long})))
          f2 (future (g/retry-transact! 3 100 (v/upsert! :vertex-id {:vertex-id random-long})))]

      (q/is (= random-long
             (g/transact!
               (v/get-property (v/refresh (first @f1)) :vertex-id))
             (g/transact!
               (v/get-property (v/refresh (first @f2)) :vertex-id))) "The futures have the correct values.")

      (q/is (= 1 (count
        (g/transact! (v/find-by-kv :vertex-id random-long))))
        "*graph* has only one vertex with the specified vertex-id")))

  (q/testing "With retries and an exponential backoff function"
    (g/transact!
      (t/create-vertex-key-once :vertex-id Long {:indexed true
                                                 :unique true}))
    (let [backoff-fn (fn [try-count] (+ (Math/pow 10 try-count) (* try-count (rand-int 100))))
          random-long (long (rand-int 100000))
          f1 (future (g/retry-transact! 3 backoff-fn (v/upsert! :vertex-id {:vertex-id random-long})))
          f2 (future (g/retry-transact! 3 backoff-fn (v/upsert! :vertex-id {:vertex-id random-long})))]

      (q/is (= random-long
             (g/transact!
               (v/get-property (v/refresh (first @f1)) :vertex-id))
             (g/transact!
               (v/get-property (v/refresh (first @f2)) :vertex-id))) "The futures have the correct values.")

      (q/is (= 1 (count
        (g/transact! (v/find-by-kv :vertex-id random-long))))
        "*graph* has only one vertex with the specified vertex-id"))))




;last lines:
(q/show_state)
;(q/gotests)

(doall (for [everyGraphType (list 
                       (r/getExistingKey KEY_InMemoryGraph)
                       (r/getExistingKey KEY_BerkeleyDB)
                       ;(r/getExistingKey KEY_Cassandra)
                       ;(r/getExistingKey KEY_HBase)
                       )]
  (binding [
            *conf* everyGraphType
            ]
    (let [ret (doall (q/gotests))
          ;errors (:error ret)
          ;fails (:fail ret)
          ]
      ret
      #_(cond 
        (or errors fails) ret
        :else
        (q/log :info ret)
        )
      )
    )
  )
)

