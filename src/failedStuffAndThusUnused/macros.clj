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
