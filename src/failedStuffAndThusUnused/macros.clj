; Copyright (c) 2012-2013, AtKaaZ
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.

;(comment ;well midje loads this for some reason
  
(ns failedStuffAndThusUnused.macros)
;;here lay all the stuff which I wanted to do and use but I failed
;;either because I failed to implement them, or their implementation cannot be used
;;due to how the overall system works (in the case of defmacro below)
;;either way, these are not used.


;failedTODO: redefine the defs like defmacro and stuff to warn when redefining happens
;and have defmacro-again as the redef one; remember this: (meta #'runtime.q/assumedTrue)

(clojure.core/defmacro defMacroAgain [& allPassedForms] 
  `(clojure.core/defmacro ~@allPassedForms)
  )

;argh, this doesn't work because when making any changes and reloading this in repl 
;ie. Ctrl+Alt+L in eclipse+ccw  then it's detected as already defined, fac mi
;something like:
;AssertionError form `assumedTrue` already defined as `runtime.q/assumedTrue` 
;at line: 211` and you tried to redefine it at: {:line 214, :column 1}
;in namespace: `runtime.q` and who says all this is:  runtime.q/defmacro (q.clj:196)
;in the case you inserted two lines above and Ctrl+Alt+L to reload it in repl

;(ns-unmap *ns* 'assumedTrue)
(ns-unmap *ns* 'defmacro) ;to prevent clojure's built-in warning on redefine
(clojure.core/defmacro defmacro 
  "redefine defmacro to fail when redefining already existing symbols
which would mainly happen when you defmacro thesamesymbol more than once
"
  [& allPassedForms]
  (let [macroToDefine# (first (rest &form))
        asVar# (resolve macroToDefine#)
;        asSym# (symbol asVar#)
        meta_ (meta asVar#)
        namespace_ (str (:ns meta_))
        name_ (:name meta_)
        nsAndName# (str namespace_ "/" name_) ;TODO: get "/" as a constance from clojure somehow
        alreadyDefinedAtLine (:line meta_)
        ;alreadyDefinedInNs (:)
        selfFormMeta (meta &form) ;this is only ie. {:line 211, :column 1}
        ;reDefinedAtLine (:line selfFormMeta)
        ;reDefinedInNS (:ns selfFormMeta)
        ]
    (cond (meta asVar#) ;if it has meta, it's already defined
      ;we quote the following, so the line number of the defmacro shows in the exception
      (throw (new AssertionError (str "form `" macroToDefine# 
                                      "` already defined as `"
                                      nsAndName# "` at line: "
                                      alreadyDefinedAtLine 
                                      "` and you tried to redefine it at: "
                                      selfFormMeta
                                      " in namespace: `" *ns* "`"
                                      " and who says all this is:"
                                      )
                  )
             )
      :else
      ;(prn (meta (first &var)))
      `(defMacroAgain ~@allPassedForms)
      )
    )
  )



(defmacro assumedTrue-bad
  "will throw when the first of the passed expressions evaluate to false or nil"
  [& _] ;allows 0 or more params, but 0 params will throw and allow you to see the original line number
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
         (let [
               exactform# (first allparams#)
               evaluatedForm# (eval exactform#)
               ]
           (prn "exactform#:" exactform#)
           (prn "evalled:" evaluatedForm# "rest count:" (count allparams#))
           (when-not evaluatedForm#
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
           (cond (> (count allparams#) 1);aka no more
;             true
;             :else
             (do 
               (prn "COUNT:" (count allparams#) "rest:" (rest allparams#))
               ( recur (rest allparams#))
               )
             )
           )
         )
       )
     )
  ) ;macro end

(defn somef_ [a] (assumedTrue (= 3 a)))
;(somef_ 3)
;(somef_ 4)

;(def xmxm java.io.File);well this blows
;and use it like ^xmxm or ^{:tag xmxm} => fail!


(defmacro defalias 
  [dst src]
  `(do 
     (ns-unmap *ns* '~dst) ;this should help with REPL while reloading, to avoid some error when already defined due to :use when :exclude didn't contain the newly defined one
     ;ns-unmap is bad when reloading the namespace which uses this namespace, causes:
     ;IllegalStateException deftest already refers to: #'runtime.q/deftest in namespace: runtime.q_test  clojure.lang.Namespace.warnOrFailOnReplace (Namespace.java:88)
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


(defmacro defalias
"
remember to add this to your (ns ...):
(:refer clojure.test :exclude [deftest is])
if you're defalias-ing `deftest` and `is`, for example.
"
  [src dest]
  `(defmacro ~dest [& ~'all2]
     `(~src ~@all2) ;this is some retarded shiet - failed
     )
  )


;);comment, have this be last line!