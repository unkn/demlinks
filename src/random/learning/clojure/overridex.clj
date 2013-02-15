(ns random.learning.clojure.overridex
  (:refer-clojure :exclude [sorted?])
  )

(defn sorted?
  [coll]
  {:pre [ (coll? coll)]}
  (clojure.core/sorted? coll)
  )


