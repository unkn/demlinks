(ns runtime.testengine
  (:use clojure.test)
  )

(defmacro isthrown?
  [cls & restt]
  (list 
    `is 
    (let [tocls (eval cls)] 
      `(
         ~'thrown? ;thanks Anderkent for unquote-quote here
         ~tocls
         ~@restt
         )
      )
    )
  )

(def a RuntimeException)

(macroexpand-1 
  '(isthrown? a (throw (RuntimeException. "1")))
)

;(isthrown? a (throw (RuntimeException. "1")))