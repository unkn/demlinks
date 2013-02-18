; Copyright (c) 2012-2013, AtKaaZ
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.

(ns util.funxions
  (:require [runtime.q :as q])
  )


#_(defn foo [& {:keys [a b c]}]
  [a b c]
  )

(def exceptionThrownWhenRequiredParamsNotSpecified
  java.lang.RuntimeException)

(deffunc foo
  {:optional [:a :b]
   :required [:c :d :e]
   :pre [:a nil?]
   }
  
  )

(q/deftest test_calls1
  (q/isthrown? exceptionThrownWhenRequiredParamsNotSpecified 
    (foo))
  (foo :c 1 :d 1 :e 1)
  )

(defn foo [& all]
  (cond (odd? (count all))
    (println "odd")
    :else
    (println "even")
    #_(condp all
      )
    )
  )


(foo 1)
(foo 1 2)