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

(def existing 2)
(a1 existing)
(a2 existing)

;get the same behaviour for macro and fn, just pass thru the params
;xmacro and xfn act the same
(defmacro xmacro [& all] `(println ~@all))

(= '(clojure.core/println) (macroexpand-1 '(xmacro))) ;true

(defn xfn [& p]
  (apply println p))

(xfn) ; == (xmacro) == calling (println)
(xfn 1) ; == (xmacro 1) == calling (println 1)

