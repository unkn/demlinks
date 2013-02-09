(ns random.learning.clojure._defalias
  (:refer clojure.test :exclude [deftest])
  (:use flatland.useful.ns)
  )

(defalias deftest clojure.test/deftest)