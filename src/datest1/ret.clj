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
  (:refer-clojure :exclude [sorted?])
  (:use [runtime.q :as q] :reload-all)
  (:refer-clojure :exclude [sorted?])
  ;(:use [clojure.core :as c])
  ;(:use clojure.tools.trace)
  )

(println *file*)


(defn comparator_AZ_order [key1 key2]
  ;{:pre [ (q/assumedTrue (keyword? key1) (keyword? key2)) ]} 
  {:pre [ 
         (q/assumedFalse (nil? key1) (nil? key2))
         
         ;will fail when comparing keywords with symbols
         ;should always be symbol with symbol OR keywords with keywords
         ;so the bug is at the caller
         (or
           (and (symbol? key1) (symbol? key2))
           (and (keyword? key1) (keyword? key2) )
           )
         ]}
  ;(prn "comparatorAZ:" key1 key2 (keyword? key1) (keyword? key2))
           ;(q/assumedTrue 
  ;)
  (compare (str key1) (str key2))
  )


(def
  ^{:private true
    :doc "keeps a 1-to-1 map between symbol name and keyword
ie. KEY_lines_count --> :lines_count
the key is paradoxically not the keyword
"}
-allSymbolsToKeys
  (ref (sorted-map-by comparator_AZ_order)
        :validator #(do 
                      (println "setting ref to value:" %&) 
                      (q/sortedMap? %)
                      )
        )
  )

(def
  ^{:doc "one to one mapping from keyword to symbol"}
-allKeysToSymbols
  (ref (sorted-map-by comparator_AZ_order)
    :validator #(q/sortedMap? %)
    )
  )

;(ns-unmap *ns* 'get)
#_(defn getKeyIfExists
"
nil is not exists,
[key val] if exists
"
  ([key]
    (getKeyIfExists @-allSymbolsToKeys key)
    )
  ([ret_object key]
  {:pre [ (assumedTrue (q/sortedMap? ret_object) ) ] }
  ;(println "get:" key ret_object)
  (find ret_object key)
  #_(cond (not (contains? ret_object key))
    (thro RuntimeException "a")
    :else
    (clojure.core/get ret_object key)
    )
  )
  )

(def exceptionThrownWhenKeyDoesNotExist
  RuntimeException
  )

#_(defn getExistingKey
"
you can pass a symbol or a keyword
the (same)keyword is returned, after checked 
that the symbol and the keyword are indeed 1-to-1 mapped to eachother
"
  ([key]
    (getExistingKey @-allSymbolsToKeys key)
    )
  ([ret_object key ]
  {:pre [ (assumedTrue (q/sortedMap? ret_object)) ] }
  (let [existing (getKeyIfExists ret_object key)]
    (cond (nil? existing)
      (thro exceptionThrownWhenKeyDoesNotExist "key `" key 
        "` doesn't exist in map `" ret_object "`")
      :else
      existing 
      )
    )
  )
  )

(defmacro getKeyIfExists
"
nil is not exists,
[key val] if exists
"
  ([key]
    ;`(do
       (let [whichMap
             (cond
               (symbol? key) @-allSymbolsToKeys
               (keyword? key) @-allKeysToSymbols
               :else
               ;FIXME: allow a form if it evals to a symbol or keyword
               `(thro AssertionError 
                  "must pass a symbol or keyword not `" key "`")
               )
             ]
         `(getKeyIfExists ~whichMap ~key)
         )
       ;)
    )
  ([ret_object key]
    ;`(do
    (println @-allSymbolsToKeys @-allKeysToSymbols)
    (println ret_object 
      (q/sortedMap? ret_object) 
      (map? ret_object) 
      (clojure.core/sorted? ret_object))
    ;{:pre [ (assumedTrue (q/sortedMap? ret_object) ) ] }
    `(do
       (println ~key '~key (symbol? ~key) ~ret_object);ouchies subtle bug here? where I actually don't want the keys(which are symbols to get evaluated to keywords)
       (find ~ret_object ~key)
       )
    )
    ;)
  )



(defmacro getExistingKey
  ([key]
    `(getExistingKey @-allSymbolsToKeys ~key)
    )
  ([ret_object key]
    `(do
       (let [existing# (getKeyIfExists ~ret_object ~key)]
         (cond (nil? existing#)
           (thro exceptionThrownWhenKeyDoesNotExist "key `" ~key 
             "` doesn't exist in map `" ~ret_object "`")
           :else
           existing#
           )
         )
       )
    )
  )

(deftest test_get1
  (is (= [:a 1] (getExistingKey {:a 1} :a )))
  (is (= [:a nil] (getExistingKey {:a nil} :a )))
  (is (= [:a 1] (getKeyIfExists {:a 1} :a )))
  (is (= [:a nil] (getKeyIfExists {:a nil} :a )))
  (is (nil? (getKeyIfExists {:a nil} :b )))
  (isthrown? exceptionThrownWhenKeyDoesNotExist 
    (getExistingKey {:a nil} :b ))
  )




(def exceptionThrownWhenNonSymbolPassedAsKey
  AssertionError)

(defmacro defSym2Key [symbolKeyName thekey]
  {:pre [
         (q/assumedTrue (keyword? thekey))
         (binding [*exceptionThrownBy_assumedPred* exceptionThrownWhenNonSymbolPassedAsKey]
           (q/assumedTrue (symbol? symbolKeyName))
           )
         ]
   }
  (prn "current: " -allSymbolsToKeys -allKeysToSymbols)
  (prn "passed : " symbolKeyName thekey)
  `(do
     (add_new_key (quote ~symbolKeyName) ~thekey)
     (def ~symbolKeyName ~thekey);we also def this so it's avail.in ccw code completion once ran
     )
  )


(def exceptionThrownWhenKeyAlreadyDefined
  RuntimeException ;TODO: make BugError exception or similar
  )

(defn-
  assoc2
  [zmap key val]
  (let [existing (find zmap key)]
  (cond existing
    ;XXX: yep this check has to be inside the swap! or else we can't use atom
    (q/thro exceptionThrownWhenKeyAlreadyDefined
    "already defined/exists key:`" key 
    "` as `" existing 
    "` you wanted to set val: `" val 
    "` in map `" zmap "`"
          ;)
        ;)
      )
    :else ;it's nil aka not already existing, so
    (assoc zmap key val)
    )
  )
  )

(defn
  ^:public;rivate
add_new_key [quoted_key_name thekey]
  (println "add_new_key: " quoted_key_name thekey)
  (dosync
    (ref-set -allSymbolsToKeys
      (assoc2 @-allSymbolsToKeys quoted_key_name thekey)
      )
    (ref-set -allKeysToSymbols
      (assoc2 @-allKeysToSymbols thekey quoted_key_name)
      )
    #_(swap! -allSymbolsToKeys
        assoc2
        quoted_key_name thekey
        )
    )
  (println "after add_new_key: " -allSymbolsToKeys  -allKeysToSymbols)
  )



(defSym2Key a :a)
(defSym2Key b :b)
(defSym2Key randomsymbo12892712391 :randomkey1)

#_(deftest test_nonsymbolkey ;this happens at compile time
  (isthrown? q/exceptionThrownBy_assumedPred (defSym2Key 1 :b))
  )

(deftest test_alreadyexisting
  (isthrown? exceptionThrownWhenKeyAlreadyDefined (defSym2Key a :a))
  (isthrown? exceptionThrownWhenKeyAlreadyDefined (defSym2Key a :c))
  (isthrown? exceptionThrownWhenKeyAlreadyDefined (defSym2Key b :c))
  (isthrown? exceptionThrownWhenKeyAlreadyDefined (defSym2Key c :a))
  
  )

(deftest test_somegetkey
  (is (= randomsymbo12892712391 
        (getKeyIfExists randomsymbo12892712391) 
        (getExistingKey randomsymbo12892712391)))
  )

(show_state)
(gotests)


