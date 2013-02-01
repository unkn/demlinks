(ns random.clojure.learning._max_key
  (:use clojure.test)
  )

;this entire namespace(parent) flat-out inspired by this post: 
;https://groups.google.com/d/msg/clojure/OhvltFCz9tw/O6vWEilGo4gJ

(deftest t1

(def a {:one 1 :two 2})
(def b {:three 3 :two 2.5})
(def c {:four 4 :two 2 :one 2})

(is (= 
      b
      (max-key :two a b)
      (max-key :two b a)
      ))


(is (= c (max-key :two a c)))

(is (= a (max-key :two c a)))


)

;I'm ctrl+alt+L -ing this from eclipse+ccw, so:
(run-tests)