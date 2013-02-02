; Copyright (c) 2012-2013, AtKaaZ
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.


(ns runtime.q
  ;(:use runtime.testengine :reload-all)
  (:refer clojure.test );can actually avoid this due to new definition of defalias; :exclude [deftest is])
  (:require flatland.useful.ns)
  ;(:use clojure.tools.trace) 
  ;(:use runtime.clazzez :reload-all) 
;(:use [runtime.q :as q] :reload-all)
  )
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;these should be kinda first:
(defmacro defalias 
  [dst src]
  `(do 
     (ns-unmap *ns* '~dst) ;this should help with REPL while reloading, to avoid some error when already defined due to :use when :exclude didn't contain the newly defined one
     (flatland.useful.ns/defalias ~dst ~src)
     )
  )

(deftest test_defalias ;this test doesn't seem to actually work as I'd wanted
  (try
    (let [
          a (defalias pr clojure.core/pr) ;before
          expect (eval '(var runtime.q/pr))
          ]
      (is (= a expect ))
      )
    (catch Throwable e (throw e)) 
    (finally ;Throwable e 
      (do
        (ns-unmap *ns* 'pr) ;after
        ;(throw e)
        )
      )
    )
  )

;(def deftest clojure.test/deftest)
;(ns-unalias *ns* is)
(defalias deftest clojure.test/deftest)
(defalias is clojure.test/is)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;(defn ax [] (println 1))

(def ^:dynamic *assumptions* (or *assert* true))

(defn moo [] (get {:a 1} :a :not-found)
  )

(defmacro pri [& all]
  `(print (str ~@all))
  )

(defmacro priln [& all]
  `(do 
     (pri ~@all)
     (newline)
     nil
     )
  )

(defmacro show_state []
  "show when namespace where the call to this macro resides
got (re)loaded and/or compiled
"
  `(do
    ;  (prn &form)
    (when *compile-files* (println "compiling" *ns*))
    ;compile like this:
    ;(compile (symbol (str *ns*)))
    ;or Ctrl+Alt+K  in eclipse+ccw
    ;it will only work once, unless you modify it
    
    (pri "(re)loaded namespace: `" (str *ns*))
    (pri "` lexical env: `" '~&env)
    (pri "` caller form: `" '~&form)
    (pri "` caller line: `" '~(meta &form))
    (pri "` caller file: `" *file*)
    (priln "`")
    nil
    )
  )


(defmacro here [] ;thanks to S11001001
  `'~(-> &form meta :line)
  )
 



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

(defmacro getAsClass 
"
=> (getAsClass a)
java.lang.RuntimeException
=> (getAsClass RuntimeException)
java.lang.RuntimeException
=> (getAsClass 'RuntimeException)
java.lang.RuntimeException
"
  [sym]
  `(let [cls# (eval ~sym)] 
     (cond (class? cls#)
       cls#
       :else
       (throw 
         (new AssertionError 
              (str "you must pass a class to `" '~(first &form)
                   "` at " '~(meta &form)
                   )
              )
         )
       )
     )
  )

(deftest test_getAsClass
  (is (= java.lang.RuntimeException (getAsClass a)))
  (is (= java.lang.RuntimeException (getAsClass RuntimeException)))
  (is (= java.lang.RuntimeException (getAsClass java.lang.RuntimeException)))
  (isnot (= 'java.lang.RuntimeException (getAsClass a)))
  )

(defmacro newClass
"
you can pass a symbol
ie.
(def a java.lang.RuntimeException)
(newClass a \"whatever\")

which would fail if you do it with just new:
(new a \"whatever)
"
  [cls & restt]
  (let [asCls (getAsClass cls)]
    `(new ~asCls ~@restt)
    )
  )






(defmacro isthrown?
    [cls & restt]
    (let [tocls 
          (getAsClass cls)
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


(defmacro thro
"
(thro RuntimeException \"concatenaed \" \"message\")
"
  [ex & restt]
  ;(class? java.lang.String)
  `(do 
     (when-not 
       (and
         (instance? java.lang.Class ~ex)
         (contains? 
           (supers ~ex) 
           java.lang.Throwable
           )
         )
       (throw 
         (new RuntimeException 
              (str "you must pass a class to `" '~(first &form)
                   "` at " '~(meta &form)
                   )
              )
         )
       )
     ;(let [cls# (getAsClass ~ex)]
       (throw (newClass ~ex (str ~@restt)))
      ; )
     )
  )

(def ^:private rte java.lang.RuntimeException)
(deftest test_thro
  (isthrown? java.lang.RuntimeException (thro rte))
  )

;(thro 2)

(def exceptionThrownBy_assumedPred AssertionError)
;(defn exceptionThrownBy_assumedPred_fn [] exceptionThrownBy_assumedPred)
;inspired from (source assert)
(defmacro assumedPred1
"
will throw if the passed expression does not satisfy predicate
ie. if pred is true? and (true? x) is false or nil it will throw
"
  [pred x]
  (cond *assumptions*
    `(do
       (let [pred# ~pred
             predQuote# (quote ~pred)
             evaled# ~x
             form# '~x
             self# '~(first &form)
             yield# (pred# evaled#)]
         (cond yield#
           true
           :else
           (thro exceptionThrownBy_assumedPred 
                 (str self# 
                      " failed, the following wasn't truthy: `(" 
                      predQuote# 
                      " "
                      (pr-str form#) 
                      ")` was `("
                      predQuote#
                      " "
                       (pr-str evaled#)
                       ")` which yielded `"
                       yield# "`"
                       )
                 )
           )
         )
       )
    :else
    `true
    )
  )

(defmacro assumedPred
"will throw when the first of the passed expressions evaluates to false or nil"
  [pred & allPassedForms]
    (cond *assumptions*
      (cond (empty? allPassedForms)
        (throw  (new AssertionError
                     (let [selfName# (first &form) lineNo# (meta &form)]
                       (str "you didn't enough parameters to macro `"
                            selfName#
                            "` The form begins at line: `"
                            lineNo# "`. You passed: " &form 
                            )
                       )
                     )
                )
        :else ;thanks to gfredericks for inspiration of this now modified line:
        (cons 'do (conj 
                    (vec 
                      (for [oneForm allPassedForms] 
                        (list `assumedPred1 pred oneForm)
                        )
                      )
                    'true
                    )
              )
        )
      :else
      `true
      )
    )

(defmacro throwIfNil 
"
to be used inside macros, 
pass &form as first param,
pass rest as second param , rest is [ & rest ] in macro's definition
ie. (defmacro something [param1 p2 & restparams] ... throwIfNil &form restparams)
"
  [caller & param]
  `(let [caller# ~caller params# ~@param ] 
     ;(println caller# "e" params#)
     (cond (or (nil? params#) (empty? params#))
       (throw  
         (new AssertionError
              (let [
                    selfName# 
                    ;'~(first &form)
                    (first caller#)
                    
                    lineNo# 
                    ;'~(meta &form)
                    (meta caller#)
                    ]
                (str "you didn't pass any parameters to macro `"
                     selfName#
                  "` form begins at line: "
                  lineNo# 
                  )
                )
              )
         )
       )
     )
  )

(defn truthy? [x] (and (not (nil? x)) (not (false? x))) )

(defmacro assumedTruthy
  [ & allPassedForms ]
  (throwIfNil &form allPassedForms)
  `(assumedPred truthy? ~@allPassedForms)
  )

(def exceptionThrownBy_assumedTrue exceptionThrownBy_assumedPred)

;(with-test
  (defmacro assumedTrue
    [ & allPassedForms ]
    (throwIfNil &form allPassedForms)
    `(assumedPred true? ~@allPassedForms)
    )
(deftest test_assumedTrue
  ;XXX: (trace-forms or trace not working with this when it throws 
    (is (true? (assumedTrue (= 1 1))))
   ; )
  (is (true? (assumedTrue (= 1 1) (= 2 2))))
  (isthrown? exceptionThrownBy_assumedTrue (assumedTrue (= 1 2)) ) 
  (isthrown? exceptionThrownBy_assumedTrue (assumedTrue (= 1 1) (= 2 1)) )
)

(defmacro assumedFalse
  [ & allPassedForms ]
  ;(prn allPassedForms)
  (throwIfNil &form allPassedForms)
  `(assumedPred false? ~@allPassedForms)
  )

; (assumedFalse)
;(assumedFalse undefinesymbolehad98230jd0q3iw)

(defmacro assumedNil
  [ & allPassedForms ]
  (throwIfNil &form allPassedForms)
  `(assumedPred nil? ~@allPassedForms)
  )

(defn notnil? [x] (not (nil? x)) )

(defmacro assumedNotNil
  [ & allPassedForms ]
  (throwIfNil &form allPassedForms)
  `(assumedPred notnil? ~@allPassedForms)
  )


;TODO: make tests for this macro
;(assumedTrue 1 2 3 (> 2 1) (= :a :a) (= 1 2))
;(assumedTrue)

;(assert nil "msg")
;(defn somef_ [a] (assumedTrue (= 3 a)))


;(use 'clojure.tools.trace)
;(assert1 (= 1 2))
;(defn somef_ [a] {:pre [
;                        (assumedTrue1 (= 3 a))
;                        (assumedTrue1 (> 4 a))
;                        ]}
;  1)

;(defn somother [a] (assert (> a 5)))

;(defn somef_ [a] {:pre [
;                        (somother a) 
;                        (asserts (= 3 a) (> 4 a))
;                        ]}
;  1)

;(assumedTrue nil)
;(assumedTrue #(println "boo")) ;obv. returns non-nil function
;(assumedTrue (#(println "boo"))) ;returns nil
;(somef_ 3)
;(somef_ 4)

;(runtime.q/assumedTrue1)
;(asserts (= 1 1) (= 1 2))

(defn pst-soe
  "show last 100 stacktraceelements when stackoverflow occurred"
  ([]
    (pst-soe 100)
  )
  ([^long num]
    (dorun (map #(println (.toString %)) (take-last num (.getStackTrace *e ))))
    )
)


(defmacro sym-state [zsym] ;;FIXME:
  ;there are like 6 cases:
  ;1. zsym is undefined symbol, returns :undefined
  ;2. zsym is defined but unbound var, returns :unbound
  ;3. zsym is defined and bound, returns :bound
  ;4. zsym is an expression ie. #() or (list 1 2 3), throws exception
  ;5. zsym is a special symbol ie. def   test this via (special-symbol? 'def) , returns :special
  ;6. zsym is a macro ie. defn, returns :macro
  `(try 
     (let [qsym# (quote ~zsym)]
           (if (not (symbol? qsym#)) ;ie. #() or (list 1 2 3)
             (throw (new RuntimeException "do not pass a form - symbol expected")) 
             ;else it's a symbol ie. def (special), defn (macro), somesymbol (symbol), someundefinedsymbol (symbol)
             (let [thevar# (resolve qsym#)] 
               (if (nil? thevar#) 
                 (if (special-symbol? qsym#)
                   :special
                   :undefined
                   )
                 (if (bound? thevar#)
;                   [:bound qsym#]
                   (if (macro-var? thevar#)
                     :macro
                     :bound
                     )
                   :unbound
                   ) 
                 )
               )
             )
           )
     (catch ClassCastException cce# (throw (new Exception (str "a" cce#))))
     )
  )


(deftest a-test
  (testing "FIXME:, I fail."
;               (is (= 0 1))
;           (sym-status +)
;           (sym-status #())
;           (sym-status connrandomeseehtihtdahd210euowkjas)
    )
)

(defmacro encast 
  "used to wrap around possibly undefined symbols,
so that referencing them like this at compile time
will not cause exception but instead the exception
will happen only when the code containing 
the undefined symbol is reached/executed

ie. does ,(eval (quote a)) which is same as just  ,a"
  [zsym]
  
;  (let [lexically-exists? (get &env zsym) resolvable? (resolve zsym) ]
;    (if (or lexically-exists? resolvable?)
;      zsym
;      (throw (Exception. "C"))
;      )
;    `(let [qsym# (quote ~zsym)] 
;       (try ;I'll just let the exception fall through
         `(eval (quote ~zsym))
;         (catch Throwable t#
;           (throw (RuntimeException. (str "Symbol undefined `" qsym# "`" t#))) ;TODO: make ex-info
;           )
;         )
;       )
)



(deftest a-test2  
  (testing "encast"
;           (is (= 0 1))
           (encast true)
           (encast false)
           (encast nil)
           (encast (println 1)) ;FIXME: check so that it doesn't get executed(evaluated) more than 1 time
           (is (thrown? ArithmeticException (/ 1 0)))
           (is (thrown? clojure.lang.Compiler$CompilerException
                        (encast connrandomeseehtihtdahd210euowkjas)))
    )
  )


(defmacro sym-info [zsym]
  `(let [ss# (sym-state ~zsym)] 
     (if (not= :bound ss#)
       {:state ss# :value nil} 
       {:state ss# :value (encast ~zsym)}
       )
     )
  )


(defmacro get-as-var [sym-or-var]
  `(let [qsym# (quote sym-or-var)]
   (if (var? ~sym-or-var)
    ~sym-or-var
    (if (symbol? qsym#)
      (var ~sym-or-var)
      (throw (new RuntimeException (str "you should pass a symbol or a var not `" qsym# "`")))
      )
    )
  ))

;TODO: handle all other cases
(defmacro macro? [zsym]
  `(macro-var? (var ~zsym))
  )

(defmacro macro-var? [zvar]
  `(boolean (:macro (meta ~zvar)))
  )


(comment
(defn sym-info2 [zsym]
  (try
  (let [ss (sym-state zsym)]
    (if (= :bound ss)
      :bound
      :nil
      )
    )
  (catch Throwable rte (pr rte)) ;;cannot ever catch CompilerException in this case for "Unable to resolve symbol"
  )
  )
);comment


(defn gotests
  []
  (binding [*assert* true *assumptions* true]
    (run-tests)
    )
  )

;(q/show_state)
;(q/here)
(show_state)

