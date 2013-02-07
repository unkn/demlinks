; Copyright (c) 2012-2013, AtKaaZ
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.

(ns ^{:doc "random text2 namespace meta test, need repl restart if changed" :author "whatever"} 
  datest1.ret
  (:refer-clojure :exclude [get])
  (:use [runtime.q :as q] :reload-all)
  )

(println *file*)

(ns-unmap *ns* 'get)
(defn get [key ret_object]
  (println key ret_object)
  ;(clojure.core/get key ret_object)
  )


(defn- comparator_AZ_order [key1 key2]
  ;{:pre [ (q/assumedTrue (keyword? key1) (keyword? key2)) ]} 
  {:pre [ (q/assumedFalse (nil? key1) (nil? key2)) ]}
  ;(prn "comparatorAZ:" key1 key2 (keyword? key1) (keyword? key2)) 
  (compare (str key1) (str key2))
  )

(def 
  ^{:private true
    :doc "keeps map between symbol name and keyword
ie. KEY_lines_count --> :lines_count
the key is paradoxically not the keyword
"} 
-allkeys
  (atom (sorted-map-by comparator_AZ_order)
        :validator #(do (println %&) 1 2 3)
        )
  )



(defmacro defkey [keyname thekey]
  {:pre [ (q/assumedTrue (keyword? thekey)) ]
   ;:post [ (do (prn "after : " -allkeys ) true) ]
   }
  (prn "current: " -allkeys)
  (prn "passed : " keyname thekey)
  `(add_new_key (quote ~keyname) ~thekey)
  )

(defn-
  assoc2
  [zmap key val]
  (cond (clojure.core/get zmap key)
    ;XXX: yep this check has to be inside the swap! or else we can't use atom
    (throw
      (new Exception;TODO: make BugError exception or similar
        (str "already defined/exists key:`" key "` val:`" val "` in map `" zmap "`")
        )
      )
    :else ;it's nil aka not already existing, so
    (assoc zmap key val)
    )
  )

(defn 
  ^:private
add_new_key [quoted_key_name thekey]
  (println "add_new_key: " quoted_key_name thekey)
  (swap! -allkeys
    assoc2
    quoted_key_name thekey
    )
  (println "after add_new_key: " -allkeys)
  )



(defkey 'a :a)
(defkey 'b :b)
(defkey 'a :c)

