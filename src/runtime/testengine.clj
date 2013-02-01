(ns runtime.testengine
  (:use clojure.test)
  )

(def ^:private a RuntimeException)
(defn ^:private b [] (throw (RuntimeException. "1"))) 

(with-test 
  (defmacro isthrown?
    [cls & restt]
    (let [tocls (eval cls)]
      `(is 
         (
           ~'thrown? ;thanks Anderkent for unquote-quote here
           ~tocls
           ~@restt
           )
         )
      )
    )
  (is (thrown? java.lang.RuntimeException (b)))
  (isthrown? a (b))
  (is (=
        (macroexpand-1 
          '(isthrown? a (throw (RuntimeException. "1")))
          )
        '(clojure.test/is (thrown? java.lang.RuntimeException (throw (RuntimeException. "1"))))
        ))
  
  )


;(macroexpand-1 
;  '(isthrown? a (throw (RuntimeException. "1")))
;)
;=
;(clojure.test/is (thrown? java.lang.RuntimeException (throw (RuntimeException. "1"))))


