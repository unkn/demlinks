; Copyright (c) 2012-2013, AtKaaZ
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.


(ns runtime.q
  (:use runtime.testengine :reload-all) 
  (:use clojure.tools.trace) 
;(:use [runtime.q :as q] :reload-all)
  )

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
  [cls & restt]
  ...
  )

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
  (is (thrown? exceptionThrownBy_assumedTrue (assumedTrue (= 1 2)) )) 
  (is (thrown? exceptionThrownBy_assumedTrue (assumedTrue (= 1 1) (= 2 1)) ))
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

(show_state)

