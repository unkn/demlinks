(ns random.learning.clojure.-defn)

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
