(ns runtime.clazzez
  (:use runtime.testengine :reload-all)
  )

(def ^:private a java.lang.RuntimeException)

(defmacro getAsClass 
"
=> (getAsClass a)
java.lang.RuntimeException
=> (getAsClass RuntimeException)
java.lang.RuntimeException
=> (getAsClass 'RuntimeException)
java.lang.RuntimeException
"
  [sym]
  `(let [cls# (eval ~sym)] 
     (cond (class? cls#)
       cls#
       :else
       (throw 
         (new AssertionError 
              (str "you must pass a class to `" '~(first &form)
                   "` at " '~(meta &form)
                   )
              )
         )
       )
     )
  )

(deftest test_getAsClass
  (is (= java.lang.RuntimeException (getAsClass a)))
  (is (= java.lang.RuntimeException (getAsClass RuntimeException)))
  (is (= java.lang.RuntimeException (getAsClass java.lang.RuntimeException)))
  (isnot (= 'java.lang.RuntimeException (getAsClass a)))
  )

(defmacro newClass
"
you can pass a symbol
ie.
(def a java.lang.RuntimeException)
(newClass a \"whatever\")

which would fail if you do it with just new:
(new a \"whatever)
"
  [cls & restt]
  (let [asCls (getAsClass cls)]
    `(new ~asCls ~@restt)
    )
  )

;(run-tests)