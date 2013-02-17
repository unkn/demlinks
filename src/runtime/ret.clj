; Copyright (c) 2012-2013, AtKaaZ
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.

(ns ^{:doc "random text2 namespace meta test, need repl restart if changed" :author "whatever"} 
  runtime.ret
  (:refer-clojure :exclude [sorted?])
  (:require [runtime.q :as q] )
  (:refer-clojure :exclude [sorted?])
  ;(:use [clojure.core :as c])
  ;(:use clojure.tools.trace)
  )
;thanks to ChongLi which triggered the "in-ns" in me by simply being there, listening.
;but I can't use in-ns because ccw won't see it as a namespace
;so, so far I'll be using what Raynes suggested :require
;even if that means I'll have to qualify like crazy

(set! 
  *warn-on-reflection*
  true)

(println *file*)
;TODO: maybe move all tests into test folder ? because lein test only executes those



(defn beforeTests []
  
  )



#_(q/deftest test_nonsymbolkey ;this happens at compile time
  (q/isthrown? q/exceptionThrownBy_assumedPred (defSym2Key 1 :b))
  )

(q/deftest test_alreadyexisting
  
  )



(defn getRetField [returnedMap keywordField]
  {:pre [(map? returnedMap)]
   }
  (let [found (find returnedMap keywordField);ie. non nil, it's a [key value] vector
        ;_ (assumedTrue found)
        _ (q/assumedNotNil [found 
                        "you tried to access field `" keywordField 
                        "` that didn't exist in map `" returnedMap
                        "`, you should've checked before!"])
        ]
    (second found)
    )
  )

(defn afterTests []

  )

(defn testsFixture [testsHere]
  (try
    (do
      (beforeTests)
      (testsHere)
      )
    (finally 
      (afterTests)
      )
    )
  )

(q/use-fixtures :once testsFixture)

(q/show_state)
(q/gotests)

;last line: (but this means, (run-tests) will fail
