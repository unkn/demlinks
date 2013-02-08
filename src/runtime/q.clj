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
  (:refer clojure.test :exclude [deftest is testing])
  (:use robert.hooke)
  ;(:require flatland.useful.ns)
  ;(:use clojure.tools.trace) 
  ;(:use runtime.clazzez :reload-all) 
  ;(:use [runtime.q :as q] :reload-all)
  ;(:use [runtime.q.exceptions :as qex] :reload-all)
  )


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;these should be kinda first:



;(def deftest clojure.test/deftest)
;(ns-unmap *ns* 'is)
;(defalias is clojure.test/is)
(defmacro is [& all]
  `(clojure.test/is ~@all)
  )
;(defalias deftest clojure.test/deftest)
#_(defmacro deftest [& all]
  `(clojure.test/deftest ~@all)
  )
;(ns-unmap *ns* 'deftest)
(defmacro deftest [& all]
  `(binding [*assert* true *runTimeAssumptions* true] ;TODO: try all combinations of these set, to true/false/nil
     (clojure.test/deftest ~@all)
     )
  )

(defmacro testing [& all]
  `(clojure.test/testing ~@all)
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;(defn ax [] (println 1))

;FIXME: problem when setting this to false by default here, because all the tests here would then need to have binding it to true and they currently don't
(def ^:dynamic *compileTimeAssumptions*;XXX: this only affects compiletime, has no effect at runtime
  ;false)
  (or *assert* true)); use `whenAssumptionsCompiled` instead

(def ^:dynamic *runTimeAssumptions*
  ;false)
  (or *assert* *compileTimeAssumptions* true))

(def whatAssumptionsReturnWhenTrue true)

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

(defmacro thro
"
(thro RuntimeException \"concatenated \" \"message\")
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

(defmacro priv_whenAssumptions_Execute [dynVar & executeForms]
  (when dynVar
    (cond (empty? executeForms)
      (thro AssertionError "you didn't pass any forms to " &form)
      :else
      `(do ~@executeForms)
      )
    )
  )

(defmacro whenAssumptionsCompiled [& executeForms]
  `(priv_whenAssumptions_Execute *compileTimeAssumptions* ~@executeForms)
  )

(defmacro whenAssumptionsEnabledAtRuntime [& executeForms]
  `(priv_whenAssumptions_Execute *runTimeAssumptions* ~@executeForms)
  )

(defn assumptionsEnabled? []
  (and *compileTimeAssumptions* *runTimeAssumptions*)
  )

#_(defmacro whenAssumptions_Execute [& executeForms]
  (when (assumptionsEnabled?)
    `(do ~@executeForms)
    )
  )


#_(deftest atest
  (is (= nil (println 2 *compileTimeAssumptions* *runTimeAssumptions*)))
  )

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



(deftest test_getAsClass
  (is (= java.lang.RuntimeException (getAsClass a)))
  (is (= java.lang.RuntimeException (getAsClass RuntimeException)))
  (is (= java.lang.RuntimeException (getAsClass java.lang.RuntimeException)))
  (isnot (= 'java.lang.RuntimeException (getAsClass a)))
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




(def ^:private rte java.lang.RuntimeException)
(deftest test_thro
  (isthrown? java.lang.RuntimeException (thro rte))
  )

;(thro 2)

(defmacro assumptionBlock [form]
  (cond *compileTimeAssumptions*
    `(cond *runTimeAssumptions*
       ~form
       :else
       whatAssumptionsReturnWhenTrue
       )
    :else
    whatAssumptionsReturnWhenTrue
    )
  )

(def ^{:dynamic true} *exceptionThrownBy_assumedPred* AssertionError)
;(defn *exceptionThrownBy_assumedPred*_fn [] *exceptionThrownBy_assumedPred*)
;inspired from (source assert)
(defmacro assumedPred1
"
will throw if the passed expression does not satisfy predicate
ie. if pred is true? and (true? x) is false or nil it will throw
"
  [pred x]
  (assumptionBlock
    `(do
       (let [pred# ~pred
             predQuote# (quote ~pred)
             evaled# ~x
             form# '~x
             self# '~(first &form)
             yield# (pred# evaled#)]
         (cond yield#
           whatAssumptionsReturnWhenTrue
           :else
           (thro *exceptionThrownBy_assumedPred* 
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
    )
  )

(defmacro assumedPred
"will throw when the first of the passed expressions evaluates to false or nil"
  [pred & allPassedForms]
    (assumptionBlock
      (cond (empty? allPassedForms)
        (throw  (new AssertionError
                     (let [selfName# (first &form) lineNo# (meta &form)]
                       (str "you didn't pass enough parameters to macro `"
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
                    'whatAssumptionsReturnWhenTrue
                    )
              )
        )
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

(def ;^{:dynamic true} 
  exceptionThrownBy_assumedTrue *exceptionThrownBy_assumedPred*)

;(with-test
  (defmacro assumedTrue
    [ & allPassedForms ]
    (throwIfNil &form allPassedForms)
    `(assumedPred true? ~@allPassedForms)
    )

;(defn sc1 [] whatAssumptionsReturnWhenTrue)

(defn assumptionCorrect?
"
return true if assumption is correct
"
  [form]
  `(= whatAssumptionsReturnWhenTrue ~form)
  )

(deftest test_assumedTrue
  ;XXX: (trace-forms or trace not working with this when it throws 
    (is (assumptionCorrect? (assumedTrue (= 1 1))))
   ; )
  (is (assumptionCorrect? (assumedTrue (= 1 1) (= 2 2))))
  (when (assumptionsEnabled?)
    (isthrown? exceptionThrownBy_assumedTrue (assumedTrue (= 1 2)) )
    (isthrown? exceptionThrownBy_assumedTrue (assumedTrue (= 1 1) (= 2 1)) )
    )
)

(defmacro assumedFalse
"
throws if any of the passed params evals to non-\"false?\"
btw:
=> (false? nil)
false
=> (false? false)
true
"
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
    (dorun 
      (map 
        (fn [^String s] (println (.toString s)))
        (take-last num (.getStackTrace ^Throwable *e))
        )
      )
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
  ;7. FIXME: handle this: (#(sym-state %) prn2) ;CompilerException java.lang.RuntimeException: Unable to resolve symbol: prn2 in this context, compiling:(NO_SOURCE_PATH:1:1) 

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


(def initialTimes 0)
(def unInitializedTimes -1)

(def times (atom unInitializedTimes))

(defn incTimes []
  (swap! times inc)
  )

(defn incTimes_hook [f & arg]
  (incTimes)
  (apply f arg)
  )

(defn resetTimes []
  (reset! times initialTimes)
  )

(defmacro times? [expectedTimes & forms]
  `(do 
    (resetTimes)
    (do ~@forms)
    (is (= ~expectedTimes @times))
    )
  )


(defn- dummy1 []
  nil
  )

(defmacro get-as-var
"
=> (get-as-var #'prn)
#'clojure.core/prn
=> (get-as-var prn)
#'clojure.core/prn

=> (var prn)
#'clojure.core/prn
=> (var #'prn)
CompilerException java.lang.ClassCastException: clojure.lang.Cons cannot be cast to clojure.lang.Symbol, compiling:(NO_SOURCE_PATH:1:1) 

"
  [sym-or-var]
  `(let [qsym# (quote sym-or-var)
         ;moo2# ~sym-or-var
         ;xxx# (println (var-get moo2#))
         ]
    (if (var? ~sym-or-var)
      ~sym-or-var
      (if (symbol? qsym#)
        (encast 
          (var ~sym-or-var)
          )
        (throw 
          (new RuntimeException 
            (str "you should pass a symbol or a var not `" qsym# "`")
            )
          )
        )
      )
    )
  )

;(defmacro xx [func]
;  (println func (var? func) (symbol? func) (var? (eval func)))
;  )
;=> (xx #'dummy1)
;(var dummy1) false false true
;nil
;=> (xx dummy1)
;dummy1 false true false
;nil


(defmacro attachTimes [func]
  {:pre [(assumedTrue (or (symbol? func) (var? func) (var? (eval func)) ) )]}
  (list `add-hook (list `get-as-var func) (var incTimes_hook))
  )
;both work
(attachTimes #'dummy1)
(attachTimes dummy1)

(deftest test_addhook_on_same_var
  (testing "add-hook called more than once on the same var, still has effect only once"
    (times? 1
      (dummy1)
      )
    )
  )


(deftest a-test2  
  (testing "encast"
;           (is (= 0 1))
           (encast true)
           (encast false)
           (encast nil)
           (times? 1 
             (encast (dummy1))
             )
           (is (thrown? ArithmeticException (/ 1 0)))
           (is (thrown? clojure.lang.Compiler$CompilerException
                        (encast connrandomeseehtihtdahd210euowkjas)))
    )
  )


(defmacro sym-info [zsym]
  `(let [ss# (sym-state ~zsym)] 
     (if (not= :bound ss#)
       {:state ss# :value ~zsym};nil} ; works: ~zsym};not-works: (encast ~zsym) -> when (#(sym-info %) prn) , CompilerException java.lang.RuntimeException: Unable to resolve symbol: p1__5531# in this context, compiling:(NO_SOURCE_PATH:1:1) 
       {:state ss# :value (encast ~zsym)}
       )
     )
  )




#_(defmacro get-as-var [sym-or-var]
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

(def exception_NotImplemented RuntimeException);FIXME: make a class here

(defn ni [& all]
  (thro exception_NotImplemented "NotImplementedException: " all);we can leave it as list, it's much more visible
  )

(defn gotests
  []
  (binding [*assert* true *runTimeAssumptions* true]
    (run-tests)
    )
  )

;(q/show_state)
;(q/here)
(show_state)
(gotests)
