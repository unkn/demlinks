(ns random.learning.clojure._defmacro)

(defmacro a1 [param]
  `(prn '~param)
  )

(defmacro a2 [param]
  `(prn ~param)
  )

(a1 non-existing)

#_(try
  (a2 non-existing)
  ;XXX: you cannot catch it!
  (catch clojure.lang.Compiler$CompilerException c c)
    )

(def existing 1)
(a1 existing)
(a2 existing)


