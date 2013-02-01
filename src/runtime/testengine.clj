(ns runtime.testengine
  (:use clojure.test)
  )

(defmacro isthrown?
  [cls & restt]
  (list 
    `is 
    (let [tocls (eval cls)
          a 'thrown?] 
;      (apply 
;        #
`(
           ;list 
           ~a 
           ~tocls

        ~@restt
        )
;        )
      )
    )
  )

(def a RuntimeException)

(macroexpand-1 
  '(isthrown? a (throw (RuntimeException. "1")))
)

;(isthrown? a (throw (RuntimeException. "1")))