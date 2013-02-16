; Copyright (c) 2012-2013, AtKaaZ
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.


(ns random.learning.hermes.graphtests1
  (:require [runtime.q :as q] :reload-all)
  (:require [clojure.java.io :as io])
  (:require [hermes.core :as g]
            [hermes.type :as t]
            [hermes.vertex :as v])
  (:require [hermes.stuff.hermesutil :as h])
  (:require [runtime.futils :as f] :reload-all)
  (:require [taoensso.timbre :as timbre 
         :only (trace debug info warn error fatal spy)])
  )

(set! 
  *warn-on-reflection*
  true)

(defn beforeTests [aVar graphVar]
  (var-set aVar (f/getUniqueFolder))
  (let [^java.io.File fdir @aVar]
    (q/assumedNotNil fdir)
    (timbre/info "using temporary folder: `\n" fdir "\n`")
    
    ;not explicit that we're using bdb:
    ;(g/open (.getAbsolutePath fdir))
    
    ;more explicit (that's we're using bdb:
    (var-set graphVar 
      (g/open {:storage {:backend "berkeleyje"
                         :directory (.getAbsolutePath fdir)}})
      )
    
    )
  )

(defn afterTests [aVar graphVar]
  (q/assumedNotNil @graphVar)
  (h/shutdown @graphVar)
  (q/assumedNotNil @aVar)
  (q/assumedTrue
    [
     (f/deleteFolderRecursively @aVar true)
     "failed to delete temporary folder: `"
     @aVar
     "`"
     ])
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


;the following test taken from hermes.persistent.core-test (it may be slightly modified by now) anyway credit goes to those people from here: https://github.com/gameclosure/hermes
(q/deftest test-dueling-transactions
  (q/testing "Without retries"
    (g/transact!
      (t/create-vertex-key-once :vertex-id Long {:indexed true
                                                 :unique true}))
    (let [random-long (long (rand-int 100000))
          f1 (future (g/transact! (v/upsert! :vertex-id {:vertex-id random-long})))
          f2 (future (g/transact! (v/upsert! :vertex-id {:vertex-id random-long})))]

      (q/is (thrown? java.util.concurrent.ExecutionException
        (do @f1 @f2)) "The futures throw errors.")))

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
(q/gotests)