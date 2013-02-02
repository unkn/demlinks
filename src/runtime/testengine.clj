(ns runtime.testengine
  ;(:use clojure.test)
  (:refer clojure.test :exclude [deftest is])
  ;(:use flatland.useful.ns); :only defalias)
  (:require flatland.useful.ns); :only defalias)
  (:use [runtime.clazzez :as c] :reload)
  ;(:require runtime.q)
  ;(:refer runtime.q :only [getAsClass])
  ;(:use [runtime.q :as q]); :reload)
  ;(:refer clojure.test :only [deftest])
  )

(defmacro defalias 
  [dst src]
  `(do 
     (ns-unalias *ns* '~dst)
     (flatland.useful.ns/defalias ~dst ~src)
     )
  )

;(def deftest clojure.test/deftest)
;(ns-unalias *ns* is)
(defalias deftest clojure.test/deftest)
(defalias is clojure.test/is)

(def ^:private a java.lang.RuntimeException)
(defn ^:private b [] (throw (java.lang.RuntimeException. "1"))) 

(defmacro isnot
  [formm & msg]
  `(is (not ~formm) ~@msg)
  )

(deftest test_isnot
  (isnot false)
  (isnot false "msg")
  (isnot (= 1 2))
  (isnot (= 1 2) "msg2")
  (is (isnot false))
  (is (isnot false "msg3"))
  (is (isnot false "msg3") "msg4")
  )




(defmacro isthrown?
    [cls & restt]
    (let [tocls 
          (c/getAsClass cls)
          ;(symbol (str (eval cls)))
          ;tocls2 (read-string (str "(quote " (eval cls) ")")) 
          ;_ (prn tocls2)
          ;(class tocls)
          ]
      `(is 
         (
           ~'thrown? ;thanks Anderkent for unquote-quote here
           ~tocls
           ~@restt
           )
         )
      )
    )
;XXX: with-test is bad when modifying the defined func/macro and 
;it fails on reload (ctrl+alt+L) the old version will be used in 
;tests and never reloaded unless repl restart
(deftest test_isthrown?
  (is (thrown? java.lang.RuntimeException (b)))
  (isthrown? a (b))
  ;FIXME: if ever, the following will fail due to symbol vs class  apparently 
  ;as detected by Anderkent, exact test case here: https://gist.github.com/4691902
  (isnot (=
        (macroexpand-1 
          '(isthrown? a (throw (java.lang.RuntimeException. "1")))
          )
        '(clojure.test/is (thrown? java.lang.RuntimeException (throw (java.lang.RuntimeException. "1"))))
        ))
  
  )


;(macroexpand-1 
;  '(isthrown? a (throw (RuntimeException. "1")))
;)
;=
;(clojure.test/is (thrown? java.lang.RuntimeException (throw (RuntimeException. "1"))))


(run-tests)
