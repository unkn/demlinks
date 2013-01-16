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
 
(defmacro assumedTrue
  "will throw if any of the passed expressions evaluate to false or nil"
  [& _] ;allows 0 or more params, but 0 params will throw and allow you to see the original line number
  ;(pri "` lexical env: `" ~a)
  `(do 
     (let [myname# '~(first &form) ;aka the name of this macro 
           allPassedForms# '~(rest &form) ;all parameters passed to this macro
           ]
       (cond (<= (count allPassedForms#) 0)
         (throw 
           (AssertionError. 
             (str "you passed no parameters to `" myname# "`")
             )
           )
         )
       (loop [allparams# allPassedForms#]
         ( do
           
         (let [
               exactform# (first allparams#)
               evaluatedForm# (eval exactform#)
               ]
           
           ;(prn "all params:" '~(rest &form))
           (prn "exactform#:" exactform#)
           (prn "evalled:" evaluatedForm# "rest count:" (count allparams#))
           ;(prn "third:" evaluatedForm#)
           ;true)
           ;(prn "aT:" (quote ~x) f# eva#)
           (when-not evaluatedForm#
             (do
               (throw 
                 (AssertionError. 
                   (str 
                   myname# " failed "
                   exactform#
                   " was "
                   evaluatedForm#
                   )
                   )
                 )
               )
             )
           (cond (<= (count allparams#) 1);aka no more
             true
             :else
             (do 
               (prn "COUNT:" (count allparams#) "rest:" (rest allparams#))
               ( recur (rest allparams#))
               )
             )
           )
         ))
       )
     )
  ) ;macro end

;TODO: make tests for this macro
;(assumedTrue 1 2 3 (> 2 1) (= :a :a) (= 1 2))
;(assumedTrue)

;(assert nil "msg")
;(defn somef_ [a] (assumedTrue (= 3 a)))

;thanks to gfredericks for this macro:
(defmacro asserts [& forms] (cons 'do (for [f forms] (list `assert f))))


;(asserts 1 2 3 nil )
;(defn somef_ [a] (println ( macroexpand-1 '(asserts (= 1 1) (>= a 2) (= 3 a)))))
(defn somef_ [a] {:pre [(= 3 a) (= 4 a)]} 1)

;(somef_ 3)
;(somef_ 2)

(show_state)

