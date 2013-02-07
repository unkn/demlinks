; Copyright (c) 2012-2013, AtKaaZ
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.

(ns datest1.ret
  (:use [runtime.q :as q] :reload-all)
  (:refer-clojure :exclude [get])
    )

;(println *file*)

(defn get [key ret_object]
  (println key ret_object)
  )


(defn comparator_AZ_order [key1 key2]
  ;{:pre [ (q/assumedTrue (keyword? key1) (keyword? key2)) ]} 
  {:pre [ (q/assumedFalse (nil? key1) (nil? key2)) ]}
  ;(prn "comparatorAZ:" key1 key2 (keyword? key1) (keyword? key2)) 
  (compare (str key1) (str key2))
  )

(def 
  ^{:private true
    :doc "keeps map between symbol name and keyword
ie. KEY_lines_count --> :lines_count"} 
-allkeys
  (atom (sorted-map-by comparator_AZ_order)
        :validator #(do (println %&) 1 2 3)
        )
  )



(defmacro defkey [keyname thekey]
  {:pre [ (q/assumedTrue (keyword? thekey)) ]
   }
  (prn "current: " -allkeys)
  (prn "passed : " keyname thekey)
  `(add_new_key (quote ~keyname) ~thekey)
  )

(defn assoc2 [zmap key val]
  (assoc zmap
      key val) 
  )

(defn 
  ^:private
add_new_key [quoted_key_name thekey]
  ( println "add_new_key: " quoted_key_name thekey)
  (swap! -allkeys 
         assoc2
         quoted_key_name thekey
         )
  )



(defkey 'a :a)
(defkey 'b :b)

