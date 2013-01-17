; Copyright (c) AtKaaZ and contributors.
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.


(ns datest1.core-test
  (:use clojure.test)
  (:use [midje.sweet])
  (:use runtime.util :reload-all)
  )

;(fact (+ 2 2) => 5)

(deftest a-test
  (testing "FIXME, I fail."
;               (is (= 0 1))
;           (sym-status +)
;           (sym-status #())
;           (sym-status connrandomeseehtihtdahd210euowkjas)
    )
)

(deftest a-test2  
  (testing "encast"
;           (is (= 0 1))
           (encast true)
           (encast false)
           (encast nil)
           (encast (println 1)) ;FIXME: check so that it doesn't get executed(evaluated) more than 1 time
           
    )
  )

(fact "something" 
      (encast connrandomeseehtihtdahd210euowkjas) 
      => (throws clojure.lang.Compiler$CompilerException)
      )  

(a-test)
(a-test2)
;(println 3)

