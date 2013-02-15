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
  (:require [runtime.q :as q] :reload-all)
  (:require [clojure.java.io :as io])
  (:require [hermes.core :as g]
            [hermes.type :as t]
            [hermes.vertex :as v])
  
  (:require [taoensso.timbre :as timbre 
         :only (trace debug info warn error fatal spy)])
  )

(set! 
  *warn-on-reflection*
  true)

(def conf {:storage {:backend "berkeleydb"
                     :hostname "127.0.0.1"}})

(defn beforeTests [aVar graphVar]
  (var-set aVar (q/getUniqueFolder))
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
  (.shutdown ^com.thinkaurelius.titan.graphdb.database.StandardTitanGraph @graphVar)
  (q/assumedNotNil @aVar)
  (q/assumedTrue
    [
     (q/deleteFolderRecursively @aVar true)
     "failed to delete temporary folder: `"
     @aVar
     "`"
     ])
  )

(defn testsFixture [testsHere]
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



;last lines:
(q/show_state)
(q/gotests)