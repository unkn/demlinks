(ns random.learning.backtick.backtick1
  (:require [backtick :as backtick])
  (:require [clojure.test :as t])
  )

(t/deftest test_backtick
  (t/is 
    (=
      '(clojure.core/inc 1)
      (backtick/syntax-quote (inc 1))
      (eval (backtick/syntax-quote-fn '(inc 1))) 
      )
    )
  
  (t/is
    (=
      '(clojure.core/inc (clojure.core/+ 1 2))
      (backtick/syntax-quote (inc (+ 1 2)))
      (eval (backtick/syntax-quote-fn '(inc (+ 1 2))))
      )
    )
  
  (t/is
    (=
      '(clojure.core/inc 3)
      (backtick/syntax-quote (inc ~(+ 1 2)))
      (eval (backtick/syntax-quote-fn '(inc ~(+ 1 2))))
      )
    )
  (t/is
    (=
      '{:a (clojure.core/+ 1 2), :b 4}
      (backtick/syntax-quote {:a (+ 1 2) :b ~(+ 1 3)})
      (eval (backtick/syntax-quote-fn '{:a (+ 1 2) :b ~(+ 1 3)}))
      )
    )
  )


(t/run-tests)
