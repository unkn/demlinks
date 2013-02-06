; Copyright (c) 2012-2013, AtKaaZ
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.

(ns runtime.q_test
;  (:use [midje.sweet])
  ;(:use clojure.test)
  (:use runtime.q :reload-all)
  ;(:use runtime.util :reload-all)
  )

;(def exceptionThrownBy_assumedTrue AssertionError)
;(def truthyInputValueFor_assumedTrue true)

(binding [*assert* true *assumptions* true] ;TODO: try all combinations of these set, to true/false/nil

;  (clojure.test/is 
;    (thrown? ;exceptionThrownBy_assumedTrue
;      clojure.lang.Compiler$CompilerException 
;    (assumedTrue)))
  
;  (fact ""
;        (assumedTrue) => throws exceptionThrownBy_assumedTrue)
;        )

;(def =>)
;(def throws 'thrown?)
;(def provided)
;
;(defmacro fact
;  [descr & checks]
;  (is (= java.lang.String (class descr)))
;  `(println ~@checks)
;  ;(let [f0rm ])
;  )

;(deftest t1_assumedTrue
;  (is (true? (assumedTrue (= 1 1))))
;  (is (true? (assumedTrue (= 1 1) (= 2 2))))


;(fact "assumedTrue returns true" 
;      (assumedTrue (= 1 1)) => true
;      (assumedTrue (= 1 1) (= 2 2)) => true
;      )

;(is (thrown? exceptionThrownBy_assumedTrue (assumedTrue (= 1 2)) )) 
;(is (thrown? exceptionThrownBy_assumedTrue (assumedTrue (= 1 1) (= 2 1)) ))

;(fact "assumedTrue throws when first encountered expr. is false" 
;      (assumedTrue (= 1 2)) => (throws exceptionThrownBy_assumedTrue) 
;      (assumedTrue (= 1 1) (= 2 1)) => (throws exceptionThrownBy_assumedTrue)
;      )

;)

;(fact "assumedTrue doesn't evaluate more than once" 
;      (assumedTrue (= 1 2)) => (throws exceptionThrownBy_assumedTrue) 
;      (assumedTrue (= 1 1) (= 2 1)) => (throws exceptionThrownBy_assumedTrue)
;      )

;(def oneAtom (atom false))
;(defn scInit [] (reset! oneAtom false))
;(defn sc1 [] truthyInputValueFor_assumedTrue)

(fact "assumedTrue uses short circuiting"
      (assumedTrue (= 1 2) (sc1)) => (throws exceptionThrownBy_assumedTrue)
      (provided 
        (sc1) => nil :times 0)
      )

(fact "assumedTrue uses short circuiting2"
      (assumedTrue (= 1 1) (sc1)) => true
      (provided 
        (sc1) => truthyInputValueFor_assumedTrue :times 1)
      )

(fact "assumedTrue uses short circuiting3"
      (assumedTrue (= 1 1) (sc1) (= 1 2)) => (throws exceptionThrownBy_assumedTrue)
      (provided 
        (sc1) => truthyInputValueFor_assumedTrue :times 1)
      )

(fact "assumedTrue uses short circuiting4"
      (assumedTrue (= 1 1) (sc1) (= 1 1)) => true
      (provided 
        (sc1) => truthyInputValueFor_assumedTrue :times 1)
      )

(fact "assumedTrue uses short circuiting5"
      (assumedTrue (sc1)) => true
      (provided 
        (sc1) => truthyInputValueFor_assumedTrue :times 1)
      )

(fact "assumedTrue uses short circuiting6"
      (assumedTrue (= 1 2) (sc1) (= 1 1)) => (throws exceptionThrownBy_assumedTrue)
      (provided 
        (sc1) => nil :times 0)
      )



);binding