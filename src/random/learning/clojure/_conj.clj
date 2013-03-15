(ns random.learning.clojure.-conj)

(= [2 3 1] (conj [2 3] 1))
;[2 3 1]
(= '(1 2 3) (conj '(2 3) 1))
;(1 2 3)