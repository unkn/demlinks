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
  )

;check this:
(defn somefn
  [req1 req2 ;required params 
   & {
      :keys [a b c d e] ;optional params
      :or {a 1 ;optional params with preset default values other than the nil default
               ; b takes nil if not specified on call
           c 3 ; c is 3 when not specified on call
           d 0 ; d is 0 --//--
               ; e takes nil if not specified on call
           }
      :as mapOfParamsSpecifiedOnCall
      }]
  (println req1 req2 mapOfParamsSpecifiedOnCall a b c d e)
  )
;=> (somefn 9 10 :b 2 :d 4)
;9 10 {:b 2, :d 4} 1 2 3 4 nil
;nil


#_(defn foo [& {:keys [a b c]}]
  [a b c]
  )

(def exceptionThrownWhenRequiredParamsNotSpecified
  java.lang.RuntimeException)

;=> (defxn noes {:a ~(inc 1) :b firsta})
;(clojure.core/apply clojure.core/hash-map (clojure.core/concat [(quote :a) (inc 1) (quote :b) (quote firsta)]))
;#'util.funxions/noes
;=> (noes)
;{:a 2, :b firsta}
;nil
;=> (macroexpand-1 '(defxn noes {:a ~(inc 1) :b firsta}))
;(clojure.core/defn noes [& all__99708__auto__] 
;   (clojure.core/println 
       ;(clojure.core/apply clojure.core/hash-map 
         ;(clojure.core/concat [(quote :a) 
                               ;(inc 1) 
                               ;(quote :b) (quote firsta)]))))


(defmacro defxn ;def funxion
  [fname ;funxion name
   defblock; a map
   & codeblocks ;multiple forms as code
   ]
  (let [x (
            backtick/template
            ;backtick/syntax-quote-fn 
            ~defblock)
        ;e (eval x)
        ]
    (println x)
    ;(println e)
    `(defn ~fname [& all#]
       (println ~x)
       )
    )
  )

(clojure.pprint/pprint 

(defxn foo
  ;`[clojure.set/join ~(+ 1 2)]
  ;if you want some form to be evaluate then place ~ before it
  ;this is the defblock
  {:something {:a ~(+ 1 2)}
   ;aliases are supported to allow later renaming the params used within the defblock without worrying that you forgot to rename all instances
   :aliases {;p1 p2 where p1 is parameter name used in here and p2 is the actual name the param has in the function body
             ;all names are keywords to allow evaluating the entire defblock and they are actually symbols inside the function body
             ;p1 oldvalue that you don't want to change
             ;p2 newvalue that you want to change and this one will be visible as symbol within the function body
             :a firsta ;p1=:a p2=:firsta
             :b b
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
                notnil? :except :specified
                notnil? [:all [:not :specified] [:except [:a :c]] ]
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
  (println firsta)
  )
)

#_(defn foo [] 1)

#_(q/deftest test_calls1
  (q/isthrown? exceptionThrownWhenRequiredParamsNotSpecified 
    (foo))
  (foo :c 1 :d 1 :e 1)
  )

(defn foo [& all]
  (cond (odd? (count all))
    (println "odd")
    :else
    (println "even")
    #_(condp all
      )
    )
  )


(foo 1)
(foo 1 2)