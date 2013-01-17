; Copyright (c) AtKaaZ and contributors.
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.


(ns runtime.q)
;(:use [runtime.q :as q] :reload-all)

(defn ax [] (println 1))

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
 

(defmacro assumedTrue1
"will throw if the passed expressions evaluates to false or nil"
  [x]
  (when *assumptions*
    `(do
       (let [evaled# ~x
             form# '~x
             self# '~(first &form)]
         (prn evaled# form#)
         (cond evaled#
           true
           :else
           (throw (new AssertionError (str self# " failed: " (pr-str form#) " was " (pr-str evaled#))))
           )
         )
       )
    )
  )

(defmacro assumedTrue
"will throw when the first of the passed expressions evaluates to false or nil"
  [& allPassedForms]
    (when *assumptions*
      (cond (empty? allPassedForms)
        (throw  (new AssertionError
                     (let [selfName# (first &form) lineNo# (meta &form)]
                       (str "you didn't pass any parameters to macro `"
                            selfName#
                            "` form begins at line: "
                            lineNo# 
                            )
                       )
                     )
                )
        :else ;thanks to gfredericks for this line:
        (cons 'do (for [oneForm allPassedForms] (list `assumedTrue1 oneForm)))
        )
      )
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

(show_state)

