(ns random.learning.clojure.usetheoverridexns
  (:refer-clojure :exclude [sorted?])
  (:use random.learning.clojure.overridex)
  (:refer-clojure :exclude [sorted?])
  ;(:require [random.learning.clojure.overridex])
  )

(and 
  (= false (sorted? '(1 2)))
  (= true (sorted? (sorted-set 1 2))))


