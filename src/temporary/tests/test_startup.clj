(ns temporary.tests.test-startup
  (:require [clojure.test :as t])
  )


(when *compile-files* (println "compiling" *ns*))
(println "(re)loaded namespace" *ns*)
1


(t/deftest test1
  (t/is (= 1 1))
  )

(t/run-tests)
