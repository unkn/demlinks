; Copyright (c) 2012-2013, AtKaaZ
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.

;(comment ;well midje loads this for some reason
  
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


;good but not generic enough: (replaced by assumedPred)

;inspired from (source assert)
(defmacro assumedTrue1
"will throw if the passed expressions evaluates to false or nil"
  [x]
  (cond *assumptions*
    `(do
       (let [evaled# ~x
             form# '~x
             self# '~(first &form)]
         ;(prn evaled# form#)
         (cond evaled#
           true
           :else
           (throw (new AssertionError (str self# " failed: " (pr-str form#) " was " (pr-str evaled#))))
           )
         )
       )
    :else
    `true
    )
  )

(defmacro assumedTrue
"will throw when the first of the passed expressions evaluates to false or nil"
  [& allPassedForms]
    (cond *assumptions*
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
        :else ;thanks to gfredericks for inspiration of this now modified line:
        (cons 'do (conj 
                    (vec 
                      (for [oneForm allPassedForms] 
                        (list `assumedTrue1 oneForm)
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

;);comment, have this be last line!