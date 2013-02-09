; Copyright (c) 2012-2013, AtKaaZ
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.

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

