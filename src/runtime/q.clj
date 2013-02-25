; Copyright (c) 2012-2013, AtKaaZ
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.

(ns runtime.q
  (:refer-clojure :exclude [sorted?])
  (:require [runtime.q :as q])
  (:refer clojure.test :exclude [deftest is testing use-fixtures])
  ;(:refer-clojure :exclude [sorted?])
  (:require [robert.hooke :as rh])
  ;(:require [runtime.ret :as r])
  ;(:refer-clojure :exclude [sorted?])
  (:require [taoensso.timbre :as timbre ;this should be used only in this namespace here, ever
         :only (trace debug info warn error fatal spy)])
  ;(:require flatland.useful.ns)
  ;(:use clojure.tools.trace) 
  ;(:use runtime.clazzez :reload-all) 
  ;(:use [runtime.q :as q] :reload-all)
  ;(:use [runtime.q.exceptions :as qex] :reload-all)
  (:require [backtick])
  (:require clojure.pprint)
  (:refer-clojure :exclude [sorted?])
  )
;FIXME: ccw, still getting this warning: WARNING: sorted? already refers to: #'clojure.core/sorted? in namespace: runtime.q, being replaced by: #'runtime.q/sorted?

(set! 
  *warn-on-reflection*
  true)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;these should be kinda first:

;(backtick/defquote almostLikeBackTick clojure.core/resolve) just use backtick/syntax-quote now
;beware (almostLikeBackTick f) => nil



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



(defmacro newInstanceOfClass
"
you can pass a symbol
ie.
(def a java.lang.RuntimeException)
(newInstanceOfClass a \"whatever\")

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

(def exceptionThrownWhenBadParams
  java.lang.RuntimeException
  )

(defn thro [ex & restt] 
  (cond
    (and
      (class? ex)
      (contains? 
        (supers ex)
        java.lang.Throwable
        )
      )
    (throw (newInstanceOfClass ex (apply str restt)))
    
    (instance? java.lang.Throwable ex)
    (throw ex)
    
    :else 
    (throw (newInstanceOfClass exceptionThrownWhenBadParams
         ;(new RuntimeException ;exception thrown when invalid params passed to thro
              (str
                "you must pass a class/instance, you passed `"
                ex " " (clojure.string/join " " restt)
                "`"
                )
           )
         )
    )
  )



(def exceptionThrownWhenSomethingUnexpectedHappened
  java.lang.RuntimeException
  )

(defn throBadParams [ & all ]
  (thro exceptionThrownWhenBadParams all)
  )

(defn throUnexpected [ & all ]
  (thro exceptionThrownWhenSomethingUnexpectedHappened "something unexpected happened:" all)
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

(defn logLevelCurrent []
  (:current-level @timbre/config)
  )

(defn logLevelSet [level]
  (timbre/set-level! level)
  )

(defn logLevelSufficient? [level]
  (timbre/sufficient-level? level)
  )

(defmacro pri [& all]
  `(cond (logLevelSufficient? :info)
     (print (str ~@all));too ugly to use timbre logging here
     )
  )

(defmacro priln [& all]
  `(do 
     (pri ~@all)
     (newline)
     nil
     )
  )

#_(defmacro epply
"
does `apply` if it's a function
or eval + list* if it's a macro
"
  [funcOrMacro & args]
  `(cond true;(macro? ~funcOrMacro)
     (eval (list* '~funcOrMacro ~args)) ;thanks bbloom for eval + list*
     :else
     ;assumed is a fn?
     (apply '~funcOrMacro ~args)
    )
  )

(defmacro when-logLevel [ & [logLevel :as all] ]
"
checked at runtime
when timbre logLevel is at least the specified one
then executes the passed forms
"
  `(when (logLevelSufficient? ~logLevel)
    ~@(rest all)
    )
  )

(defmacro when-debug [& forms]
  (backtick/syntax-quote (when-logLevel :debug ~@forms))
  )

(defmacro binding-LogLevel [logLevel & forms]
"
force this loglevel while executing forms
"
  `(let [save# (logLevelCurrent)
          _# (logLevelSet ~logLevel)
          ]
    (try
      (do
        ~@forms
        )
      (finally
        (logLevelSet save#)
        )
      )
    )
  )

(deftest test_exec_whenloglevel_debug_binding
  (let [save# (logLevelCurrent)
        _# (logLevelSet :info)
        ]
    (try
      (do
        (is (= true (logLevelSufficient? :info)))
        (is (= 2 (q/when-logLevel :info (println 1) 2)))
        (is (= 3 (q/when-logLevel :info 1 2 3)))
        (is (= nil (when-debug 4))) ;log level is :info which is not sufficient for :debug
        (is (= nil (q/when-debug (println 4))))
        (is (= nil (q/when-debug (println 4) 5)))
        )
    (finally
      (logLevelSet save#)
      )
    )
    )
  
  (binding-LogLevel :info
    (is (= 'moo (when-logLevel :info 'moo)))
    (is (= nil (when-debug 4)))
    )
  
  (binding-LogLevel :debug
    (is (= 'moo (when-logLevel :info 'moo))) ;still works cause :debug includes :info
    (is (= 4 (when-debug 4)))
    (is (= nil (q/when-debug (println 4))))
    (is (= 5 (q/when-debug (println 4) 5)))
    )
  
  (binding-LogLevel :warn
    (is (= nil (when-debug 4)))
    (is (= nil (when-logLevel :info 'moo)))
    )
  )

(defn ^:private logAny [ & [logLevel :as all] ]
  ;newline before any log msg 'cause hard to tell lines esp. when they span multiple lines due to wrap
  (when (logLevelSufficient? logLevel)
    (println);"\n"
    )
  #_(assumedTrue [
                (macro? timbre/log)
                "nolonger a macro, well our impl. must change"
                ])
  (timbre/log logLevel 
    (clojure.string/join " " (rest all)); thanks gfredericks for opening my eyes to use join here
    )
  )

;the following is inspired from http://blog.jayfields.com/2011/02/clojure-and.html
(defmacro get-lexical-env []
"
=> (let [a 1 b (+ 1 2)]
     (let [c (do \"a\" \"b\")]
       (q/get-lexical-env)
       )
     )
{a 1, b 3, c \"b\"}
"
  (let [envkeys (keys &env)]
    `(zipmap (quote ~envkeys) (list ~@envkeys))
    )
  )

(defmacro show-lexical-env []
  `(clojure.pprint/pprint (get-lexical-env))
  )

(defmacro show_state []
  "show when namespace where the call to this macro resides
got (re)loaded and/or compiled
"
  `(do
    ;  (prn &form)
    (when *compile-files* (log :debug "compiling" *ns*))
    ;(when true (log :debug "compiling" *ns*))
    ;compile like this:
    ;(compile (symbol (str *ns*)))
    ;or Ctrl+Alt+K  in eclipse+ccw
    ;it will only work once, unless you modify it
    
    (pri "(re)loaded namespace: `" (str *ns*))
    (pri "` lexical env: `" (show-lexical-env))
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


(def ^:private rte java.lang.RuntimeException)
(def ^:private rte2 (newInstanceOfClass rte "12"))


(deftest test_getAsClass
  (is (= java.lang.RuntimeException (getAsClass a)))
  (is (= java.lang.RuntimeException (getAsClass RuntimeException)))
  (is (= java.lang.RuntimeException (getAsClass java.lang.RuntimeException)))
  (isnot (= 'java.lang.RuntimeException (getAsClass a)))
  (is (= (eval 'java.lang.RuntimeException) (getAsClass a)))
  (is (apply = 
        (let [a# rte
              zzz rte
              b# java.lang.RuntimeException
              yyy java.lang.RuntimeException]
          (list
            rte
            java.lang.RuntimeException
            (getAsClass a#)
            (getAsClass b#)
            (getAsClass zzz)
            (getAsClass yyy)
            )
          )
        )
    )
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



(deftest test_thro1
  (isthrown? java.lang.RuntimeException (thro rte))
  (isthrown? rte (thro rte))
  (isthrown? rte (thro rte "msg"))
  (isthrown? rte (thro rte2))
  ;FIXed: won't work: 
  (isthrown? exceptionThrownWhenNotAClass (eval '(isthrown? rte2 (thro rte2))))
  (isthrown? java.lang.RuntimeException (thro java.lang.RuntimeException))
  (isthrown? rte (thro java.lang.RuntimeException))
  
  (isthrown? rte 
    (let [a# rte] 
      (thro a#)
      )
    )
  
  (isthrown? java.lang.RuntimeException 
    (let [a# rte] 
      (thro a#)
      )
    )
  
  (isthrown? java.lang.RuntimeException 
    (let [a# java.lang.RuntimeException] 
      (thro a#)
      )
    )
  
  (isthrown? rte
    (let [a# java.lang.RuntimeException] 
      (thro a#)
      )
    )
  
  (isthrown? rte
    (let [a# rte2] 
      (thro a#)
      )
    )
  
  (isthrown? rte
    (let [a# (newInstanceOfClass rte (str "1" "2"))] 
      (thro a#)
      )
    )
 
  )
  

(deftest test_newClass1
  (is 
    (instance? java.lang.RuntimeException
      (newInstanceOfClass java.lang.RuntimeException))
    )
  (is 
    (instance? java.lang.RuntimeException
      (newInstanceOfClass rte))
    )
  (is 
    (instance? rte
      (newInstanceOfClass java.lang.RuntimeException))
    )
  
  (is 
    (instance? rte
      (newInstanceOfClass rte))
    )
  
  (= rte 
    (class (newInstanceOfClass rte)))
  
  (= java.lang.RuntimeException
    (class (newInstanceOfClass rte)))
  
  (= java.lang.RuntimeException
    (class (newInstanceOfClass java.lang.RuntimeException)))
  
  (is (apply = 
        (let [a# rte
              zzz rte
              b# java.lang.RuntimeException
              yyy java.lang.RuntimeException]
          (list
            rte
            java.lang.RuntimeException
            (class (newInstanceOfClass a#))
            (class (newInstanceOfClass b#))
            (class (newInstanceOfClass zzz))
            (class (newInstanceOfClass yyy))
            )
          )
        )
    )
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
              
              ;(do
               ; (prn restOfFailMsg)
                (list `apply `str
                  (concat ['list "\n"
                           "The fail msg is:\n`\n"] 
                    ;evalled
                    restOfFailMsg
                    ["\n`"])
                  )
                ;)
;              )
              ;)
            )
          envkeys (keys &env)
          ]
      `(do
         ;(let [lexwithin# (quote ~(keys &env))]
         (let [pred# ~pred
               predQuote# (quote ~pred)
               evaled# ~x
               form# '~x
               self# '~(first &form)
               yield# (pred# evaled#)
               ;lexenv# (get-lexical-env)
               ]
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
                      yield# "`"; and lexical env within the expression was: "
;                      (list lexwithin#)
                      ~failMsgIfAny
                      " lexical env.: \n" (zipmap (quote ~envkeys) (list ~@envkeys))
                      "\n"
                      )
               )
             )
           );let1
;         );let2
         );do
      );let
    )
  )

(defmacro assumedPred
"will throw when the first of the passed expressions evaluates to false or nil
each expression can be just a form ie. (= 1 2) or a vector like this:
[(= 1 2) \"concatenated\" \"msg\" \"when fails\"]
[(= 1 2)] ;msg ommited, it's equivalent to just (= 1 2)
"
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
                    `whatAssumptionsReturnWhenTrue
                    )
              )
        )
      )
    )

(defmacro isAssumptionFailed
"
to be used within a deftest
"
  [& forms]
  `(q/isthrown? 
    *exceptionThrownBy_assumedPred*
    ~@forms
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
  
  (isAssumptionFailed
    (assumedPred true? false [true] [true "msghere" " a" "b"]))
  (isAssumptionFailed
    (assumedPred true? true [false] [true "msghere" " a" "b"]))
  (isAssumptionFailed 
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

;(def ;^{:dynamic true} 
;  exceptionThrownBy_assumedTrue *exceptionThrownBy_assumedPred*)

;(with-test
  (defmacro assumedTrue
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
    (isAssumptionFailed (assumedTrue (= 1 2)) )
    (isAssumptionFailed (assumedTrue (= 1 1) (= 2 1)) )
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



(defn pst-soe ;to test, do this at REPL: ```````'1  ;it should stack overflow
"show last 100 stacktraceelements when stackoverflow occurred
you should've already passed the following jvm arg:
-XX:MaxJavaStackTraceDepth=-1
else it won't remember the full trace only last 1024 elements? or was it 1000 forgot
"
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
  ;//7. cannot be handled: handle this: (#(sym-state %) prn2) ;CompilerException java.lang.RuntimeException: Unable to resolve symbol: prn2 in this context, compiling:(NO_SOURCE_PATH:1:1)
  ;8. alsoFIXME: (#(sym-state %) prn) returns :undefined while (sym-state prn) return :bound
;9. => (let [a 1] (sym-state a)) ;returns :undefined
  ;10. fix: (let [defn 1] (sym-state defn)) ; returns :macro
  ;so 9&10 see if it's in lexical env first
  ;11. handle when passing a class
  ;12. check out this case with symbol and class actually ends up being a class not a symbol passed to us
;=> (eval (backtick/template (list 'sym-state ~(eval a))))
;(sym-state java.lang.RuntimeException)
;=> (eval (sym-state java.lang.RuntimeException))
;:class
;(def a java.lang.RuntimeException)
;=> (eval (eval (backtick/template (list 'sym-state ~(eval a)))))
;:non-symbol

  (let [envkeys (keys &env)]
    `(try 
       (let [qsym# (quote ~zsym)
             lexEnv# (zipmap (quote ~envkeys) (list ~@envkeys))
             ]
         (cond (not (symbol? qsym#)) ;ie. #() or (list 1 2 3)
           ;(throw (new RuntimeException "do not pass a form - symbol expected"))
           :non-symbol ;ie. 1 or 'a or "a" or '(1 2 3)
           :else ;it's a symbol ie. def (special), defn (macro), somesymbol (symbol), someundefinedsymbol (symbol)
           (do
             (let [a# (find lexEnv# qsym#)]
               (cond a#
                 ;could also be bound outside, but also lexical, tho the latter will be in effect
                 :lexical
                 :else
               ;not a lexical or whatever you call it
                 (let [thevar# (resolve qsym#)] ;can resolve to a class ie. (resolve (quote java.lang.RuntimeException))
                   (cond (nil? thevar#)
                     (if (special-symbol? qsym#)
                       :special
                       :undefined
                     )
                     
                     (class? thevar#) :class
                     
                     :else
                     (do
                       (assumedTrue [(var? thevar#) "unexpected use case wasn't nil nor class nor var : `" thevar# "`" ]) 
                       (cond (bound? thevar#) 
                         (if (macro-var? thevar#)
                           :macro
                           :bound
                           )
                         :else
                         :unbound
                         )
                       )
                     )
                   )
               )
               )
             )
         )
         )
       (catch ClassCastException cce# ;FIXME: 11. handle when passing a class
         (rethro cce#))
     )
    );let
  );macro


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

ie. does ,(eval (quote a)) which is same as just  ,a
note: it won't work for lexical symbols (inside a let) because it uses eval
"
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

(defmacro encast2 [zsym]
  `~zsym
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
  (list `rh/add-hook (list `get-as-var func) (var incTimes_hook))
  )
;both work
(attachTimes #'dummy1)
(attachTimes dummy1)




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
     (condp = ss#
       #_(not-any? #(= ss# %) '(
                                   :bound
                                   :lexical
                                   ))
       :bound
       {:state ss# :value (encast ~zsym)}
       :lexical
       {:state ss# :value ~zsym} ;FIXME: this will always throw when symbol is undefined
       ;:else
       {:state ss# :value nil};~zsym};nil} ; works: ~zsym};not-works: (encast ~zsym) -> when (#(sym-info %) prn) , CompilerException java.lang.RuntimeException: Unable to resolve symbol: p1__5531# in this context, compiling:(NO_SOURCE_PATH:1:1)
;       :else
;       {:state ss# :value (encast ~zsym)}
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

;TODO: handle all other cases, still won't handle 
;ie. (macro? timbre/log) when in a different namespace currently but executing the macro in the original namespace where timbre alias is defined
(defmacro macro? [zsym]
  `(= :macro (sym-state ~zsym))
  )

(defmacro macro-var? [zvar]
  `(boolean (:macro (meta ~zvar)))
  )


#_(comment
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


(defn addAndIgnoreNil
"
allowed no args, in case you pass two things which are both nil
"
  [& restt]
  ;(apply (fnil + 0 0) one restt) ;thanks to gfredericks for ,((fnil + 0) nil 4)
  (->> restt (filter identity) (apply + 0));thanks to gfredericks for ,(->> [nil 1 nil] (filter identity) (apply + 1))
  )

(deftest test_addAndIgnoreNil
  (is (= 2 (addAndIgnoreNil nil 1 1)))
  (is (= 2 (addAndIgnoreNil 1 nil 1)))
  (is (= 2 (addAndIgnoreNil 1 1 nil)))
  (is (= 2 (addAndIgnoreNil 2 nil)))
  (is (= 2 (addAndIgnoreNil nil 2)))
  (is (= 2 (addAndIgnoreNil 2)))
  (is (= 0 (addAndIgnoreNil)))
  (is (= 0 (addAndIgnoreNil nil)))
  (is (= 0 (apply addAndIgnoreNil (take 100 (repeat nil)))))
  (is (= 1 (apply addAndIgnoreNil 1 (take 100 (repeat nil)))))
  (is (= 1 (apply addAndIgnoreNil (concat (take 100 (repeat nil)) '(1))) ))
  (is (= 2 (apply addAndIgnoreNil 1 (concat (take 100 (repeat nil)) '(1))) ))
  )

(defn getLocation [& [shift]]
  (let [
        sta (.getStackTrace (new Exception "showLocation"))
        seqsta (seq sta)
        ste (nth 
            seqsta
            (addAndIgnoreNil shift 2)
            )
        ;fn (.getFileName ste)
        ;linenum (.getLineNumber ste)
        ;namespacee (.getClassName ste)
        ]
    #_{:file fn 
     :line linenum 
     :ns namespacee 
     ;:sta seqsta
     :ste ste
     }
    (clj-stacktrace.core/parse-trace-elem ste)
    )
  )


;XXX: would be nice to allow skipping `showFunction` instead of having to set it to `nil` when just wanting to set `shift`
;XXX: maybe allow macros to be passed not just functions
(defn showLocation
"
optional input:
- showFunction = to function to call with the map of location as parameter
- shift = number to shift into the stacktrace, u shouldn't need to use this
don't pass a macro as the function
"
  [& [showFunction shift]]
  {:pre [
         (assumedTrue [
                      (or (nil? showFunction) (fn? showFunction))
                      "you must pass a function, or not specify it or nil to use println"
                      ])
         ]}
  (;apply showFunction
    (or showFunction println ) 
    (getLocation (addAndIgnoreNil shift 2)))
  )

(deftest test_empty
  (is (empty? nil))
  (is (empty? '()))
  (is (empty? (list)))
  )




#_(defn ^:private priv_functionget [& msg2]
  '(fn [x#] 
     (logAny
       (or loglevel :debug) 
       x# msg2)
     )
  )

;TODO:something like (or x 1) but we check x against some invariants if it's non-truthy
#_(defn fallback
  [original & fallbacks]
  
  )

(defn assumedValidIfPresent [validityfn presentfn thing & failmsg]
  {:pre [(assumedTrue 
           [
            (ifn? validityfn)
            "you didn't pass a valid ifn? as validityfn"
            "you passed `"
            validityfn
            "`"
            ]
           [
            (ifn? presentfn)
            "you didn't pass a valid ifn? as presentfn"
            "you passed `"
            presentfn
            "`"
            ]
           )]}
  (cond (presentfn thing)
    (assumedTrue [(validityfn thing)
                  failmsg])
    :else
    ;not-present, not checked and return true
    true
    )
  )

(defn assumedValidLogLevel [loglevel]
  (assumedTrue [
                (keyword? loglevel)
                "loglevel must be a keyword ie. :info or :debug"
                "you passed `" loglevel "`" 
                ])
  )

(defn assumedValidShift [shift]
  (assumedTrue [(number? shift)
                "the shift must be a number, you passed `"
                shift "`"
                ])
  )

(defmacro ^:private priv_functionget [& msg2]
  `(fn [x#]
     (logAny
       (or ~'loglevel :debug) ;TODO: check logLevel is valid
       x# ~@msg2)
     )
  )

(defn showHere
"
optional inputs:
shift = shift in stacktrace to finetune the shown location
logLevel = ie. :info :debug etc.
rest = strings to be joined with space between them; or nil to skip
"
  [& [shift loglevel  :as all]]
  {:pre [
         (assumedValidIfPresent assumedValidShift notnil? shift)
         (assumedValidIfPresent assumedValidLogLevel notnil? loglevel)
         ]}
  (showLocation (let [msg (nthrest all 2)]
                  (cond (empty? msg)
                    (priv_functionget)
                    #_#(logAny
                       (or loglevel :debug) 
                       %)
                    :else
                    (priv_functionget
                      "Msg: `\n"
                      (clojure.string/join " " msg)
                      "\n`"
                      )
                    #_#(logAny
                       (or loglevel :debug) 
                       %
                       "Msg: `\n"
                       (clojure.string/join " " msg)
                       "\n`"
                       )
                    )
                  )
                  
    (addAndIgnoreNil shift 2))
  )



(defn logShift [shift loglevel & anyMsg]
  {:pre [(assumedValidLogLevel loglevel)
         (assumedValidShift shift)
         ]}
  (apply showHere (addAndIgnoreNil shift 3) loglevel anyMsg)
  )


(defn logCaller [loglevel & anyMsg ]
  {:pre [(assumedValidLogLevel loglevel)]}
  (apply logShift 4 loglevel anyMsg)
  )

(defn log [loglevel & anyMsg]
  {:pre [(assumedValidLogLevel loglevel)]}
  (apply logShift 3 loglevel anyMsg)
  )

(defn show_state2 []
  (when *compile-files* (log :debug "compiling" *ns*))
  (logCaller 
    :info 
    "(re)loaded namespace `" (str *ns*) "`"
    )
  )

(deftest test_addhook_on_same_var
  (testing "rh/add-hook called more than once on the same var, still has effect only once"
    (times? 1
      (dummy1)
      )
    )
  )

;(q/show_state)
;(q/here)
(show_state)
(show_state2)
(gotests)