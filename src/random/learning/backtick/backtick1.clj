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
  
  (t/is
    (=
      '{:a 3, :b some-undefined-symbol-for-the-win-123}
      (backtick/template {:a ~(+ 1 2) :b some-undefined-symbol-for-the-win-123})
      (eval (backtick/template-fn '{:a ~(+ 1 2) :b some-undefined-symbol-for-the-win-123}))
      )
    )
  
  (t/is
    (=
      '{:a 3, :b random.learning.backtick.backtick1/some-undefined-symbol-for-the-win-123}
      `{:a 3, :b some-undefined-symbol-for-the-win-123}
      (backtick/syntax-quote {:a ~(+ 1 2) :b some-undefined-symbol-for-the-win-123})
      (eval (backtick/syntax-quote-fn '{:a ~(+ 1 2) :b some-undefined-symbol-for-the-win-123}))
      )
    )
  )


(t/run-tests)
