; Copyright (c) 2012-2013, AtKaaZ
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.

(ns failedStuffAndThusUnused.functions)

;(ns-unmap *ns* 'get)
(defn getKeyIfExists
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

(defn getExistingKey
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