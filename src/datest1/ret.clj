; Copyright (c) AtKaaZ and contributors.
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

(defmacro assumedTrue
  "will throw if any of the passed expressions evaluate to false or nil"
  [& all]
  `(do 
     (let [myname# '~(first &form) allp# '~(rest &form)]
       (cond (<= (count allp#) 0)
         (throw 
            (AssertionError. 
              (str "you passed no parameters to `" myname# "`"
                   )
              )
            )
         )
     (loop [allparams# allp#]
   (let [
         ;f# ~x eva# (eval f#) 
         ;allparams# '~(rest &form) 
         exactform# (first allparams#)
         evalled# (eval exactform#)
         ]
     ;(prn "all params:" '~(rest &form))
     (prn "exactform#:" exactform#)
     (prn "evalled:" evalled# "rest count:" (count allparams#))
     ;(prn "third:" evalled#)
     ;true)
      ;(prn "aT:" (quote ~x) f# eva#)
      (when-not evalled#
        (do
          (throw 
            (AssertionError. 
              (str 
                myname# " failed "
                exactform#
                " was "
                evalled#
;                (prn-str 
;                  exactform#
;                  "is" 
;                  evalled#)
                )
              )
            )
          )
        )
;      ~(prn "teh:" all)
;      true

      (cond (<= (count allparams#) 1);aka no more ;~@(empty? all)
        true
        :else
        (do 
          (prn "COUNT:" (count allparams#) "rest:" (rest allparams#))
          ( recur (rest allparams#))
          ;'(assumedTrue (rest allparams#))
          )
        )
     )
  )
)))

;TODO: make tests for this macro
;(assumedTrue 1 2 3 (> 2 1) (= :a :a) (= 1 2))
;(assumedTrue)

;(println *file*)

(defn get [key ret_object]
  (println key ret_object)
  )


(defn comparator_AZ_order [key1 key2]
  {:pre [ (assumedTrue (keyword? key1) (keyword? key2)) ]
   } 
  (prn "comparatorAZ:" key1 key2 (keyword? key1) (keyword? key2)) 
  (compare key1 key2)
  )

(def 
  ^{:private true
    :doc "keeps map between symbol name and keyword
ie. KEY_lines_count --> :lines_count"} 
-allkeys
  (atom (sorted-map-by comparator_AZ_order)
        :validator #(do (println %&) 1)
        )
  )



(defmacro defkey [keyname thekey]
  {:pre [ (assumedTrue (keyword? thekey)) ]
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

