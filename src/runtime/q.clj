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
  (:refer-clojure :exclude [sorted?])
  (:refer clojure.test :exclude [deftest is testing use-fixtures])
  (:refer-clojure :exclude [sorted?])
  (:use robert.hooke)
  (:refer-clojure :exclude [sorted?])
  (:use [taoensso.timbre :as timbre 
         :only (trace debug info warn error fatal spy)])
  ;(:require flatland.useful.ns)
  ;(:use clojure.tools.trace) 
  ;(:use runtime.clazzez :reload-all) 
  ;(:use [runtime.q :as q] :reload-all)
  ;(:use [runtime.q.exceptions :as qex] :reload-all)
  )

(set! 
  *warn-on-reflection*
  true)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;these should be kinda first:



;(def deftest clojure.test/deftest)
;(ns-unmap *ns* 'is)
;(defalias is clojure.test/is)
(defmacro is [& all]
  `(clojure.test/is ~@all)
  )

(defmacro use-fixtures [& all]
  `(clojure.test/use-fixtures ~@all)
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

(def exceptionThrownWhenNotAClass AssertionError)

(defn getAsClass [sym]
  (cond 
    (class? sym)
    sym
    :else
    (throw 
      (eval 
        (list 'new 
          ;AssertionError ;
          exceptionThrownWhenNotAClass
          (str 
            "you didn't pass a class, you passed `"
            sym
            "`"
            )
          )
        )
      )
    )
  )
#_(defmacro getAsClass 
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
       (throw ;XXX: don't use thro here, they'll recur
         (new AssertionError 
              (str "you must pass a class(ie. not an instance) to `"
                '~(first &form)
                "` at "
                '~(meta &form)
                " form was: `"
                '~&form
                "`" 
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
;unblinded by Aaron Cohen here: https://groups.google.com/d/msg/clojure/1AJ7cShhhWg/QE7JVcmIyb4J
;which basically means I should use eval at runtime not at compile time as I was doing so far
  `(eval (list 'new (getAsClass ~cls) ~@restt))
  )

(defmacro call-method [inst m & args]
"
=> (def someInst \"somestringinstance\")
#'runtime.q/someInst
=> (call-method someInst 'toUpperCase)
\"SOMESTRINGINSTANCE\"
=> (call-method someInst \"toUpperCase\")
\"SOMESTRINGINSTANCE\"
=> (call-method someInst toUpperCase)
CompilerException java.lang.RuntimeException: Unable to resolve symbol: toUpperCase in this context, compiling:(NO_SOURCE_PATH:1:1)
=> (def four (* 2 2))
#'runtime.q/four
=> (call-method someInst 'substring (+ 1 1) four)
\"me\"
=> (call-method someInst \"substring\" (+ 1 1) four)
\"me\"
=> (call-method \"abcde\" (quote substring) 2 four)
\"cd\"
" 
  `(. ~inst ~(symbol (eval m)) ~@args)
  )


(defn call-fn ;by Meikel Brandmeyer (kotarak) https://groups.google.com/d/msg/clojure/YJNRnGXLr2I/h4t9-oDbMUcJ
"
=> (def f (call-fn \"java.lang.String\" \"substring\" \"startpos\" \"endpos\"))
#'runtime.q/f
=> (f \"abcdef\" 2 4)
\"cd\"
=> (f (str \"123\" \"45\") (+ 1 1) 4)
\"34\"
" 
  [& args]
  {:arglists ([cls method & args])}
  (let [o (gensym)
        [cls method & args] (map symbol args)]
    (eval
      `(fn [~o ~@args]
         (. ~(with-meta o {:tag cls})
            (~method ~@args))))))

;You can also do away with the argument names. You just need the number of arguments.
(defn call-fn ;by Meikel Brandmeyer (kotarak)
"
(def f (call-fn \"java.io.File\" \"renameTo\" 1))

=> (f \"abcdef\" 2 4)
\"cd\"
=> (f (str \"123\" \"45\") (+ 1 1) 4)
\"34\"
" 
  [class method n-args]
  (let [o    (gensym)
        args (repeatedly n-args gensym)
        [class method] (map symbol [class method])]
    (eval
      `(fn [~o ~@args]
         (. ~(with-meta o {:tag class})
            (~method ~@args))))))

(defmacro rethro
  [ex]
  `(thro ~ex)
  )


;FIXME: => (let [x rte] (thro x)) ;CompilerException java.lang.UnsupportedOperationException: Can't eval locals, compiling:(NO_SOURCE_PATH:1:14) 
(defmacro thro
"
(thro RuntimeException \"concatenated \" \"message\")
"
  [ex & restt]
  ;(class? java.lang.String)
  (let [
        eex (eval ex)
        ;_ (prn eex)
        ;_ (prn (class eex))
        ]
     (cond
       
       ;if passed a class or symbol resolving to a class
       (and
         ;(instance? java.lang.Class eex)
         (class? eex)
         (contains? 
           (supers eex) 
           java.lang.Throwable
           )
         )
       ;then
       `(throw (newClass ~ex (str ~@restt)))
       
       ;if passed an instance of an exception
       (instance? java.lang.Throwable eex)
       ;then throw it as it is
       `(throw ~ex)
       
       ;none of the above
       :else
       `(throw 
         (new RuntimeException 
              (str 
                "you must pass a class/instance to `"
                '~(first &form)
                "` at "
                '~(meta &form)
                " form was: `"
                '~&form
                "`" 
                )
              )
         )
       )
     ;(let [cls# (getAsClass ~ex)]
       
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
  (is (= (eval 'java.lang.RuntimeException) (getAsClass a)))
  )








(defmacro isthrown?
    [cls & restt]
    (let [tocls 
          (getAsClass (eval cls))
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

(deftest test_getAsClass2
  (isthrown? exceptionThrownWhenNotAClass (getAsClass 'a))
  (isthrown? exceptionThrownWhenNotAClass (getAsClass 123))
  )


;XXX: with-test is bad when modifying the defined func/macro and 
;it fails on reload (ctrl+alt+L) the old version will be used in 
;tests and never reloaded unless repl restart
(deftest test_isthrown?
  (is (thrown? java.lang.RuntimeException (b)))
  (isthrown? a (b))
  ;seems fixed: if ever, the following will fail due to symbol vs class  apparently 
  ;as detected by Anderkent, exact test case here: https://gist.github.com/4691902
  (isnot (=
        (macroexpand-1 
          '(isthrown? a (throw (java.lang.RuntimeException. "1")))
          )
        '(clojure.test/is (thrown? java.lang.RuntimeException (throw (java.lang.RuntimeException. "1"))))
        ))
  
  )


(def ^:private rte java.lang.RuntimeException)
(def ^:private rte2 (newClass rte "12"))

(deftest test_thro1
  (isthrown? java.lang.RuntimeException (thro rte))
  (isthrown? rte (thro rte))
  (isthrown? rte (thro rte2))
  ;FIXME: won't work: (isthrown? rte2 (thro rte2));due to compiletime/runtime macro crap; let's just say I wanna transcend this level of programming and get up there into a graph-like based system in 3D, asap ffs!
  (isthrown? java.lang.RuntimeException (thro java.lang.RuntimeException))
  (isthrown? rte (thro java.lang.RuntimeException))
  )
  
;(macroexpand-1 
;  '(isthrown? a (throw (RuntimeException. "1")))
;)
;=
;(clojure.test/is (thrown? java.lang.RuntimeException (throw (RuntimeException. "1"))))






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


#_(defn someeval [& all]
  (map identity all)
  (map constantly all)
  )

(def ^{:dynamic true} *exceptionThrownBy_assumedPred* AssertionError)
;(defn *exceptionThrownBy_assumedPred*_fn [] *exceptionThrownBy_assumedPred*)
;inspired from (source assert)
(defmacro assumedPred1
"
will throw if the passed expression does not satisfy predicate
ie. if pred is true? and (true? x) is false or nil it will throw
"
  [pred x & restOfFailMsg]
  (assumptionBlock
    (let [failMsgIfAny 
          (when-not (empty? restOfFailMsg)
            ;thanks to alex_baranosky for the following form (originally here https://www.refheap.com/paste/11118 )
            ;(eval
            ;(let [evalled 
                  ;(map eval restOfFailMsg) CompilerException java.lang.UnsupportedOperationException: Can't eval locals, compiling:(datest1/ret.clj:263:11) 
                ;  (apply someeval restOfFailMsg)
               ;   ]
;            (do
;              (prn 
;                (concat ['list "\n"
;                         "The fail msg is:\n`\n"] 
;                  ;evalled
;                  restOfFailMsg
;                  ["\n`"])
;                )
              
              (list `apply `str
                (concat ['list "\n"
                         "The fail msg is:\n`\n"] 
                  ;evalled
                  restOfFailMsg
                  ["\n`"])
                )
;              )
              ;)
            )
          ]
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
                      ~failMsgIfAny
                      "\n"
                      )
               )
             )
           )
         );do
      );let
    )
  )

(defmacro assumedPred
"will throw when the first of the passed expressions evaluates to false or nil
each expression can be just a form ie. (= 1 2) or a vector like this:
[(= 1 2) \"concatenated\" \"msg\" \"when fails\"]
[(= 1 2)] ;msg ommited, it's equivalent to just (= 1 2)
";TODO: above
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
                        (do 
                          ;(println "oneFormOrig" oneForm)
                          (apply list `assumedPred1 pred 
                            (cond (not (vector? oneForm))
                              (do
                                ;(println "oneFormNowVec" (vector oneForm))
                                (vector oneForm)
                                )
                              :else 
                              (do
                                ;(println "oneFormAlready" (vector oneForm))
                                oneForm)
                              )
                            )
                          )
                        )
                      )
                    'whatAssumptionsReturnWhenTrue
                    )
              )
        )
      )
    )

(deftest test_vecParams
  (is (= true (assumedPred true? true [true] [true "msghere" " a" "b"])))
  ;TODO: make these tests better:
  (isthrown? *exceptionThrownBy_assumedPred* 
    (assumedPred true? false [true] [true "msghere" " a" "b"]))
  (isthrown? *exceptionThrownBy_assumedPred* 
    (assumedPred true? true [false] [true "msghere" " a" "b"]))
  (isthrown? *exceptionThrownBy_assumedPred* 
    (assumedPred true? true [true] [false "msghere" " a" "b"]))
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
                (str "you didn't pass any parameters to `"
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
  (defmacro assumedTrue;TODO: make it test only first form and rest be err msg without need to (str ..)
    [ & allPassedForms ]
    (throwIfNil &form allPassedForms)
    `(assumedPred true? ~@allPassedForms)
    )

;(defmacro att
;    [& allPassedForms ]
;  ;(throwIfNil &form allPassedForms)
;  (throwIfNil &form (first allPassedForms))
;  `(assumedPred true? x ~@allPassedForms)
;  )
;
;(att)
  
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

(defn pst-soe ;to test, do this at REPL: ```````'1  ;it should stack overflow
  "show last 100 stacktraceelements when stackoverflow occurred"
  ([]
    (pst-soe 100)
  )
  ([^long num]
    (dorun 
      (map 
        (fn [^java.lang.StackTraceElement s] (println (.toString s)))
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

Pass a symbol, not a form

ie. does ,(eval (quote a)) which is same as just  ,a"
  [zsym]
  
;  (let [lexically-exists? (get &env zsym) resolvable? (resolve zsym) ]
;    (if (or lexically-exists? resolvable?)
;      zsym
;      (throw (Exception. "C"))
;      )
;    `(let [qsym# (quote ~zsym)] 
;       (try ;I'll just let the exception fall through
;`(do 
`(let [q# (quote ~zsym)
       a# (eval q#)]
   ;(println "zzzz " q# a#)
   a#;
   )
     ;(eval (quote ~zsym))
;   )
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
           (let [undefinedsymbol (gensym 'connrandomeseehtihtdahd210euowkjas)]
             (println undefinedsymbol)
             (is (thrown? clojure.lang.Compiler$CompilerException
                   (encast undefinedsymbol)))
             )
           #_(is (thrown? clojure.lang.Compiler$CompilerException
                 (gensym 'connrandomeseehtihtdahd210euowkjas)))
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

;(ns-unmap *ns* 'sorted?)
(defn sorted?
  [coll]
  {:pre [(assumedTrue (coll? coll))]}
  (clojure.core/sorted? coll)
  )

(defn sortedMap? [param]
  (and (map? param) (sorted? param))
  )

(defn delete-file
"
an implementation that returns the true/false status
which clojure.java.io/delete-file doesn't do(tested in 1.5.0-RC14)
thanks to Sean Corfield, I'm made aware that
(clojure.java.io/delete-file \"file\" :not-deleted)
can be used to return :not-deleted when failed, however I still don't agree with this
optimization-based-implementation, so I still want to get true/false from this, even though
I could make a new function on top of the original delete-file function, because the original
does have it's use, ie. it allows me to return whatever value i want(except false/nil) when deletion fails,
tho doesn't allow me to return whatever I want when it succeeds, let's just say that
I wouldn't wanna implement it like that ever (not consciously anyway).
"
  [f & [silently]]
  (let [ret (.delete (clojure.java.io/file f))]
    (cond (or ret silently)
      ret
      :else
      (throw (java.io.IOException. (str "Couldn't delete " f)))
      )
    )
  )



(defn getUniqueFile 
"
pass nil to in-path  if the default temporary-file directory is to be used
 The default temporary-file directory is specified by the system property java.io.tmpdir
 aka (System/getProperty \"java.io.tmpdir\")
returns: java.io.File
"
[& [in-path prefix suffix]]
  (java.io.File/createTempFile
    (or prefix "unq")
    suffix ; may be nil, in which case the suffix ".tmp" will be used
    (clojure.java.io/as-file in-path)
    )
  )

(defn getUniqueFolder
  [& [in-path prefix suffix]]
  ;(delay 
    (try
      (let [^java.io.File uniqueFile (getUniqueFile in-path prefix suffix)
            ]
        (assumedTrue (.exists uniqueFile) (.isFile uniqueFile))
        (assumedFalse (.isDirectory uniqueFile))
        (delete-file uniqueFile false)
        (assumedFalse (.exists uniqueFile))
        (.mkdir uniqueFile)
        (assumedTrue (.exists uniqueFile) (.isDirectory uniqueFile))
        uniqueFile
        )
      (catch Throwable t 
        (do 
          (throw t);
          ;(rethro t) ;CompilerException java.lang.UnsupportedOperationException: Can't eval locals, compiling:(runtime\q.clj:1070:11) 
          )
        )
      )
   ; )
  )

(deftest test_asfile
  (let [x (newClass java.io.File "s")
        y (clojure.java.io/as-file x)
        ]
    (is (= x y))
    )
  )
;(q/show_state)
;(q/here)
(show_state)
(gotests)
