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
                      ;(println "setting ref " '-allSymbolsToKeys "to value:" %) 
                      (q/sortedMap? %)
                      )
        )
  )

(def
  ^{:doc "one to one mapping from keyword to symbol"}
-allKeysToSymbols
  (ref (sorted-map-by comparator_AZ_order)
    ;:validator #(q/sortedMap? %)
    :validator #(do 
                      ;(println "setting ref " '-allKeysToSymbols "to value:" %) 
                      (q/sortedMap? %)
                      )
    )
  )

(defn -cleanMaps []
  (dosync
    (ref-set -allKeysToSymbols (empty @-allKeysToSymbols))
    (ref-set -allSymbolsToKeys (empty @-allSymbolsToKeys))
    )
  nil
  )

(def exceptionThrownWhenKeyDoesNotExist
  RuntimeException
  )



(def exceptionThrownWhenMalformedInputPassed
  AssertionError)

(defn getVectorKeyIfExists
"
you may pass: symbol or keyword
ie.
(getVectorKeyIfExists a) where a evals to :a or 'a
(getVectorKeyIfExists 'a)
(getVectorKeyIfExists :a)
returns:
nil if not exists,
[key val] if exists, where key is the same as what you passed evaluated to
"
  ([key]
    (let [whichMap
          (cond
            (symbol? key) @-allSymbolsToKeys
            (keyword? key) @-allKeysToSymbols
            :else
            (q/thro exceptionThrownWhenMalformedInputPassed
              "must pass a symbol or keyword or form, but not what you passed aka `"
              key "` whatever you passed evaluated to this before the call")
            )
          ]
      (getVectorKeyIfExists whichMap key)
      )
    )
  ([ret_object key]
    ;(println @-allSymbolsToKeys @-allKeysToSymbols)
    #_(println ret_object
      (q/sortedMap? ret_object)
      (map? ret_object)
      (clojure.core/sorted? ret_object))
    ;(do
       ;(println key ret_object)
       (find ret_object key)
       ;)
    )
  )


(defn getExistingKeyVec
"
returns a vector [key val]
" 
  ([key]
    (let [whichMap
          (cond
            (symbol? key) @-allSymbolsToKeys
            (keyword? key) @-allKeysToSymbols
            :else
            (q/thro exceptionThrownWhenMalformedInputPassed
              "must pass a symbol or keyword or form, but not what you passed aka `"
              key "` whatever you passed evaluated to this before the call")
            )
          ]
      (getExistingKeyVec whichMap key)
      )
    )
  ([ret_object key]
    (let [existing (getVectorKeyIfExists ret_object key)]
      (cond (nil? existing)
        (q/thro exceptionThrownWhenKeyDoesNotExist "key `" key 
          "` doesn't exist in map `" ret_object "`")
        :else
        existing
        )
      )
    )
  )

(defn getExistingKey
  [key]
  (let [[k v] (getExistingKeyVec key)
        ]
    (q/assumedFalse 
      [(and (keyword? k) (keyword? v))
       "they are both keywords, so we don't know which ones is the real key"
       " : " k " " v
       ]
      )
    (cond (keyword? k)
      k
      :else
      v
      )
    )
  )

#_(defn getExistingKeySValue
  [key]
  (let [[k v] (getExistingKeyVec key)
        ]
    (assertFalse 
      [(and (keyword? k) (keyword? v))
       "they are both keywords, so we don't know which ones is the real key"
       " : " k " " v
       ]
      )
    (cond (keyword? k)
      v
      :else
      k
      )
    )
  )

(q/deftest test_get1
  (q/is (= [:a 1] (getExistingKeyVec {:a 1} :a )))
  (q/is (= [:a nil] (getExistingKeyVec {:a nil} :a )))
  (q/is (= [:a 1] (getVectorKeyIfExists {:a 1} :a )))
  (q/is (= [:a nil] (getVectorKeyIfExists {:a nil} :a )))
  (q/is (nil? (getVectorKeyIfExists {:a nil} :b )))
  (q/isthrown? exceptionThrownWhenKeyDoesNotExist 
    (getExistingKeyVec {:a nil} :b ))
  ;(q/is (= 5 (getExistingKeySValue {:a 5})))
  )




(def exceptionThrownWhenNonSymbolPassedAsKey
  AssertionError)

(defmacro defSym2Key [symbolKeyName thekey]
  {:pre [
         (q/assumedTrue (keyword? thekey))
         (binding [q/*exceptionThrownBy_assumedPred* exceptionThrownWhenNonSymbolPassedAsKey]
           (q/assumedTrue (symbol? symbolKeyName))
           )
         ]
   }
  ;(prn "current: " -allSymbolsToKeys -allKeysToSymbols)
  ;(prn "passed : " symbolKeyName thekey)
  `(do
     (add_new_key (quote ~symbolKeyName) ~thekey)
     (def ~symbolKeyName ~thekey);we also def this so it's avail.in ccw code completion once ran
     ;also if def-ed we could use functions instead of macros for the get calls
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
  ;(println "add_new_key: " quoted_key_name thekey)
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
  ;(println "after add_new_key: " -allSymbolsToKeys  -allKeysToSymbols)
  )


(defn beforeTests []
  (defSym2Key a :a)
  (defSym2Key b :b)
  (defSym2Key randomsymbo12892712391 :randomkey1)
  )



#_(q/deftest test_nonsymbolkey ;this happens at compile time
  (q/isthrown? q/exceptionThrownBy_assumedPred (defSym2Key 1 :b))
  )

(q/deftest test_alreadyexisting
  (q/isthrown? exceptionThrownWhenKeyAlreadyDefined (defSym2Key a :a))
  (q/isthrown? exceptionThrownWhenKeyAlreadyDefined (defSym2Key a :c))
  (q/isthrown? exceptionThrownWhenKeyAlreadyDefined (defSym2Key b :c))
  (q/isthrown? exceptionThrownWhenKeyAlreadyDefined (defSym2Key c :a))
  
  )

(q/deftest test_somegetkey
  (q/is (= [randomsymbo12892712391 'randomsymbo12892712391] 
        (getVectorKeyIfExists randomsymbo12892712391) 
        (getExistingKeyVec randomsymbo12892712391)))
  
  (q/is (= ['randomsymbo12892712391 randomsymbo12892712391] 
        (getVectorKeyIfExists 'randomsymbo12892712391) 
        (getExistingKeyVec 'randomsymbo12892712391)))
  (q/isthrown? exceptionThrownWhenMalformedInputPassed 
        (getVectorKeyIfExists 1) )
  (q/isthrown? exceptionThrownWhenMalformedInputPassed (getExistingKeyVec 1))
  
  (q/isthrown? exceptionThrownWhenKeyDoesNotExist
    (getExistingKeyVec (gensym 'inexistentsymbol))
    )
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
  ;(-cleanMaps)
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
