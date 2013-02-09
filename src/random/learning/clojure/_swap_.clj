(ns random.learning.clojure.-swap-
  (:use clojure.test)
  )

(def players (atom ()))
;(reset! players ())


(deftest a1
  (is (= '(:player1) (swap! players conj :player1)))
  (is (= '(:player1) @players))
  (is (= '(:player2 :player1) (swap! players conj :player2)))
  (is (= '(:player2 :player1) @players))
  )


(deref players)

(def counter (atom 0))
;(reset! counter 0)
(deftest b1
  (is (= 0 @counter))
  (is (= 1 (swap! counter inc)))
  (is (= 1 @counter))
  (is (= 2 (swap! counter inc)))
  (is (= 2 @counter))
        )


(run-tests)
