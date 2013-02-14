(ns random.learning.clojure.usetheoverridexns
  (:use random.learning.clojure.overridex)
  )

(and 
  (= false (sorted? '(1 2)))
  (= true (sorted? (sorted-set 1 2))))


