; Copyright (c) 2012-2013, AtKaaZ
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.

(ns util.funxions
  (:require [runtime.q :as q])
  (:require [backtick])
  (:require clojure.pprint)
  )


(def exceptionThrownWhenRequiredParamsNotSpecified
  java.lang.RuntimeException)

;you need not bind this though, unless you really want to name it something
;but it won't clash with others, supposedly - there may be some case when it could clash let within a let
(def ^:dynamic *fxn_defBlock_symbol* (gensym 'fxn_defBlock))
(def ^:dynamic *fxn_defBlockRaw_symbol* (gensym 'fxn_defBlockRaw))


(defmacro get_fxn
"
returns a macro that when called, evaluates the value of <the symbol returned by the symbol that you passed>
=> (get_fxn *fxn_defBlock_symbol* defBlock)

=> *fxn_defBlock_symbol*
fxn_defBlock

=> (macroexpand-1 '(get_fxn_defBlock))
fxn_defBlock

=> (get_fxn_defBlock)
CompilerException java.lang.RuntimeException: Unable to resolve symbol: fxn_defBlock in this context, compiling:(NO_SOURCE_PATH:1:1) 
;so evaluates fxn_defBlock in current context, ie. not like (eval fxn_defBlock) which wouldn't see those inside the let
"
  [sym namee]
  `(defmacro ~(symbol (str "get_fxn_" namee)) []
     ~sym ;actually this is good here, i don't need the `~*fxn_defBlock_symbol* variant which seems to be the same thing O_o
     )
  )

(get_fxn *fxn_defBlock_symbol* defBlock) ;(defmacro get_fxn_defBlock ...)
(get_fxn *fxn_defBlockRaw_symbol* defBlockRaw) ;(defmacro get_fxn_defBlockRaw ...)
;(defmacro get_fxn_defBlock2
;  []
;  ;like get the value of <the symbol returned by *fxn_defBlock_symbol*>
;  `~*fxn_defBlock_symbol*
;  )
;=> (macroexpand-1 '(get_fxn_defBlock))
;fxn_defBlock3
;=> (macroexpand-1 '(get_fxn_defBlock2))
;fxn_defBlock3
;=> (clojure.tools.macro/mexpand-all '(defmacro get_fxn_defBlock2
;     []
;     ;like get the value of the symbol returned by *fxn_defBlock_symbol*
;     `~*fxn_defBlock_symbol*
;     ))
;(do (def get_fxn_defBlock2 (fn* ([&form &env] *fxn_defBlock_symbol*))) (. (var get_fxn_defBlock2) (setMacro)) (var get_fxn_defBlock2))
;=> (clojure.tools.macro/mexpand-all '(get_fxn *fxn_defBlock_symbol* defBlock))
;(do (def get_fxn_defBlock (fn* ([&form &env] *fxn_defBlock_symbol*))) (. (var get_fxn_defBlock) (setMacro)) (var get_fxn_defBlock))

(q/deftest test_second
  (q/is (= nil (second nil)))
  )

(defmacro defxn ;def funxion
  [fname ;funxion name
   passedDefBlock; a map
   & codeblocks ;multiple forms as code
   ]
  (let [lst (list 'backtick/template passedDefBlock)
        evaDefBlock (eval lst) ;the defblock after ~ are evaluated
        ;e (eval evaDefBlock)
        aliases (second (find evaDefBlock :aliases)) ;can be nil
        ]
    (q/execWhenLogLevel :debug (clojure.pprint/pprint (list ":aliases=" aliases)))
    (q/execWhenLogLevel :debug (clojure.pprint/pprint (list "evaDefBlock=" evaDefBlock)))
    ; evaDefBlock == `'~evaDefBlock = `~*fxn_defBlock_symbol*
    `(defn ~fname [& all#]
       (let [~*fxn_defBlock_symbol* '~evaDefBlock
             ~*fxn_defBlockRaw_symbol* '~passedDefBlock
             ;~'fxn_evalled ~e
             ]
         ;(clojure.pprint/pprint (list ~'*fxn_defBlockRaw_symbol* ~*fxn_defBlockRaw_symbol*))
         ;(clojure.pprint/pprint (list ~'*fxn_defBlock_symbol* ~*fxn_defBlock_symbol*));symbol and its value
         ;(println ~e)
         ~@codeblocks
         #_(= (~(:c x) ~(:d x))
           (~(:e x) ~(:d x)))
         ;(prn ~(:c x) ~(:e x))
         )
       )
    )
  )

#_(try ;can't catch it
(defxn noes {:a ~(inc (+ 1 2)) :b firsta 
             :c {:b c 
                 ;:b 2
                 }
             }
     (println "!!!" fxn_defBlock)
     (:b fxn_defBlock)
     )
(catch Compile$CompilerException c c))

;TODO: throw on dup keys; - this is already happening on "eval"

#_(
=> (defxn noes {:a ~(inc (+ 1 2)) :b firsta}
     (println "!!!" fxn_defblock)
     (:b fxn_defblock)
     )
{:a 4, :b firsta}
#'util.funxions/noes
=> (noes)
raw {:a (clojure.core/unquote (inc (+ 1 2))), :b firsta}
defblock {:a 4, :b firsta}
!!! {:a 4, :b firsta}
firsta
)


;(clojure.pprint/pprint 

(defxn foo
  ;`[clojure.set/join ~(+ 1 2)]
  ;if you want some form to be evaluate then place ~ before it
  ;this is the defblock
  {;:something {:a ~(+ 1 2)}
   :a (hash-map :a 1 :b 2)
   :b ~(hash-map :a 3 :b 4 :a 5) ;that's one way to use dup keys and you wouldn't even catch the bug
   :c {:a 6 :b 7 
       ;:a 8 ;can't dup this, will throw
       }
   ;aliases are supported to allow later renaming the params used within the defblock without worrying that you forgot to rename all instances
   :aliases {;p1 p2 where p1 is parameter name used in here and p2 is the actual name the param has in the function body
             ;all names are keywords to allow evaluating the entire defblock and they are actually symbols inside the function body
             ;p1 oldvalue that you don't want to change
             ;p2 newvalue that you want to change and this one will be visible as symbol within the function body
             :a firsta ;p1=:a p2=:firsta
             :b b
             :c ~(symbol "c")
             ;:c :b ;will throw because both :b and :c map to same :b
             }
   :optional {:a 0 
              :b 0}
   :required #{:c :d :e}
   
   ;supposedly i don't want to run invariants on the optional unspecified(at call) params
   ;because i plan not using those at all and I should have a function to check if that param was or not specified, can't just use a value ie. nil
   ;but if it was specified, then do apply invariants on it.
   
   :invariants [notnil? :all-unspecified;-optionals; only :optional can be unspecified(at call) :all-uac
                notnil? :all ;both spec and unspec
                notnil? :all-specified; :all-sac
                notnil? :except :unspecified
                ~(list partial > 0) :except :specified
                (partial > 0) [:all [:not :specified] [:except [:a :c]] ]
                ]
   ;invariants ran over all specified params but not over the unspecified(and thus optional ones which have the default value assigned)
   :spec_invariants [notnil? :only [:a :b :c :d :e]
                     notnil? :all
                     (partial > 0) [:a :c :d]
                     ]
   ;invariants that are ran over the optional non-specified(on call) params
   ;invariants for the optional unspecified(at call) params; since you can't not specify any of the :required params
   :ou_invariants [notnil? :only [:a :b]
                   (partial > 0) :all
                   (partial > 1) :except [:a]
                   ]
   
   ;maybe not implement this:
   ;:allow_extras false ;by default false, if to allow parameters that are none of the defined ones in defblock also collect them in extras as a vector in order of occurrence
   }
#_  {a nil 
   b nil
   };:optional + explicit default value 
#_  #{ 
    c d e;:required 
    ;:pre [:a nil?]
    }
  ;TODO: throw on  extra aka unspecified  params
  ;TODO: allow invariants functions for each param and throw when any of them fail(obviously)
  ;TODO: ignore optional params that weren't passed on call
  ;TODO: throw when required params aren't passed on call
  (clojure.pprint/pprint (list "foocode" (get_fxn_defBlock)));firsta)
  )
;)


#_(q/deftest test_calls1
  (q/isthrown? exceptionThrownWhenRequiredParamsNotSpecified 
    (foo))
  (foo :c 1 :d 1 :e 1)
  )

#_(defn foo [& all]
  (cond (odd? (count all))
    (println "odd")
    :else
    (println "even")
    #_(condp all
      )
    )
  )


#_(foo 1)
#_(foo 1 2)
#_(defxn noes {:a ~(inc (+ 1 2)) :b firsta}
     (println "!!!" fxn_defblock)
     (:b fxn_defblock)
     )
#_(noes)

#_(when *compile-files* (println 1))

;=> (def b 'ax)
;#'util.funxions/b
;=> b
;ax
;=> (let [ax 1] [(= 'ax b) (= ax (eval b))])
;CompilerException java.lang.RuntimeException: Unable to resolve symbol: ax in this context, compiling:(NO_SOURCE_PATH:1:1) 


(def a 0)
(defxn noes {:a 
             ~(inc (+ 1 2)) ;this will resolve at compile time?
             :b firsta 
             :c (partial > 1) ;the function will resolve at the time defxn is called 
             :d a ;"a" has to be resolvable in current ns where defxn is called and it will point to the same a, thus not be relative to *ns* once defxn executed
             :e ~(list partial > 1)
             }
  (println "!!!" (get_fxn_defBlock))
;  (:b fxn_defBlock)
;  (:c fxn_defBlock)
  )
(println (noes))
(def a 1)
(println (noes))
(binding [*fxn_defBlock_symbol* 'abc]
  (println *fxn_defBlock_symbol*)
  (noes))


(q/show_state2)
(q/gotests)