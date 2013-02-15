; Copyright (c) 2012-2013, AtKaaZ
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.

;thanks to ChongLi which triggered the "in-ns" in me by simply being there, listening.
;but I can't use in-ns because ccw won't see it as a namespace

(ns random.learning.hermes.graphtests1
  (:use [runtime.q :as q] :reload-all)
  (:require [hermes.core :as g]
            [hermes.type :as t]
            [hermes.vertex :as v])
  )


(defn beforeTests []
  ;(g/open (q/))
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

(use-fixtures :once testsFixture)



;last lines:
(show_state)
(gotests)