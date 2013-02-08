(ns proof.of.concept.caveats.macros)

(def asym :a)
(def bsym :b)
(def m {'asym :akey 'bsym :bkey}) ;this map will be evaluated if you're not careful

(defmacro subtlebug_macromap
  []
    (let [
          m2 (quote m)
          evaledm m]
      `(do
         (showmap m)
         (showmap ~m2)
         (showmap ~evaledm)
         )
      )
  )

(defmacro showmap [mapp]
  `(println ~mapp)
  )

;=> (subtlebug_macromap)
;{bsym :bkey, asym :akey}
;{bsym :bkey, asym :akey}
;{:b :bkey, :a :akey}
;nil

;and if you comment-out the first line aka "(def asym :a)"
;=> (subtlebug_macromap)
;{bsym :bkey, asym :akey}
;{bsym :bkey, asym :akey}
;CompilerException java.lang.RuntimeException: Unable to resolve symbol: asym in this context, compiling:(NO_SOURCE_PATH:1:1) 

