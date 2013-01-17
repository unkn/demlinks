(ns goodButUnused.macros)
;;here lay all the stuff that are good but I'm not using them
;;either because I modified them into a better-suited-for-me version
;;or for some other reasons that I can't think of

;thanks to gfredericks for this macro:
(defmacro asserts [& forms] (cons 'do (for [f forms] (list `assert f))))

;(defmacro b [& restall] `(asserts ~@restall))
;(b)
;(b 1 2 (= 1 nil))

;(asserts 1 2 3 nil )
;(defn somef_ [a] (println ( macroexpand-1 '(asserts (= 1 1) (>= a 2) (= 3 a)))))
;(defn somef_ [a] {:pre [(= 3 a) (= 4 a)]} 1)

;(somef_ 3)
;(somef_ 2)

(defmacro assert1 ;from: (source assert) but modified now
  "Evaluates expr and throws an exception if it does not evaluate to
  logical true."
  {:added "1.0"}
  [x]
  (when *assert*
    `(do
       (let [evaled# ~x form# '~x]
         (prn evaled# form#)
         (when-not evaled#
           (throw (new AssertionError (str "Assert failed: " (pr-str form#) " was " evaled#)))
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
        ;the commented variant is used:
;        (throw  (new AssertionError
;                     (let [selfName# (first &form) lineNo# (meta &form)]
;                       (str "you didn't pass any parameters to macro `"
;                            selfName#
;                            "` form begins at line: "
;                            lineNo# 
;                            )
;                       )
;                     )
;                )
        ;and this is the unused but good to know variant:
      `(throw (new AssertionError
                  (let [selfName# '~(first &form) lineNo# '~(meta &form)]
                    (str "you didn't pass any parameters to macro `" 
                         selfName#
                         "` at line: "
                         lineNo#
                         )
                  )
                  ))
        :else
        (cons 'do (for [oneForm allPassedForms] (list `assumedTrue1 oneForm)))
        )
      )
    )