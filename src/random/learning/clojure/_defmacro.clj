(ns random.learning.clojure._defmacro)

(defmacro a1 [param]
  `(prn '~param)
  )

(defmacro a2 [param]
  `(prn ~param)
  )

(a1 non-existing)

#_(try
  (a2 non-existing)
  ;XXX: you cannot catch it!
  (catch clojure.lang.Compiler$CompilerException c c)
    )

(def existing 2)
(a1 existing)
(a2 existing)
s
;get the same behaviour for macro and fn, just pass thru the params
;xmacro and xfn act the same
(defmacro xmacro [& all] `(println ~@all))

(= '(clojure.core/println) (macroexpand-1 '(xmacro))) ;true

(defn xfn [& p]
  (apply println p))

(xfn) ; == (xmacro) == calling (println)
(xfn 1) ; == (xmacro 1) == calling (println 1)

;;;;;;;;;;;;;;

;check this out:
=> (def b 'ax) 
   (let [ax 1] [(= 'ax b) (= ax b)])
;#'util.funxions/b
;[true false]
=> b
;ax
=> (let [ax 1] [(= 'ax b) (= ax (eval b))])
;CompilerException java.lang.RuntimeException: Unable to resolve symbol: ax in this context, compiling:(NO_SOURCE_PATH:1:1) 

;the only(?) way:
=> (defmacro b [] `~'ax)
;#'util.funxions/b
=> (b)
;CompilerException java.lang.RuntimeException: Unable to resolve symbol: ax in this context, compiling:(NO_SOURCE_PATH:1:1) 
=> (let [ax 1] (= ax (b) ))
true

(let [ax 1] ;this let is invisible so you don't know ax is the name
  (let [c (b)]
    (= ax c ) ;ofc. ax is not directly known, just here to show c == ax 
    )
  )
;true

;;;;;;;;;;;;;;


(def fxn_defBlock_symbol 'fxn_defBlock3)


(defmacro get_fxn [sym namee]
  `(defmacro ~(symbol (str "get_fxn_" namee)) []
     ~sym ;actually this is good here, i don't need the `~fxn_defBlock_symbol variant which seems to be the same thing O_o
     )
  )

(get_fxn fxn_defBlock_symbol defBlock)
(defmacro get_fxn_defBlock2
  []
  ;like get the value of the symbol returned by fxn_defBlock_symbol
  `~fxn_defBlock_symbol
  )
=> (macroexpand-1 '(get_fxn_defBlock))
fxn_defBlock3
=> (macroexpand-1 '(get_fxn_defBlock2))
fxn_defBlock3
=> (clojure.tools.macro/mexpand-all '(defmacro get_fxn_defBlock2
     []
     ;like get the value of the <symbol returned by fxn_defBlock_symbol>
     `~fxn_defBlock_symbol
     ))
(do (def get_fxn_defBlock2 (fn* ([&form &env] fxn_defBlock_symbol))) (. (var get_fxn_defBlock2) (setMacro)) (var get_fxn_defBlock2))
=> (clojure.tools.macro/mexpand-all '(get_fxn fxn_defBlock_symbol defBlock))
(do (def get_fxn_defBlock  (fn* ([&form &env] fxn_defBlock_symbol))) (. (var get_fxn_defBlock ) (setMacro)) (var get_fxn_defBlock ))


;;;;;;;;;;;;;;
(def component? number?)

(defmacro defcomponent [name co]
  `(let [c# ~co]
     (assert (component? c#) 
       (str "Not a valid IComponent passed:\nevaluated form: `" 
         (pr-str c#) "`\noriginal form: `" 
         '~co "`\nfull form: `" '~&form "`"
         "\nlocation: " ~(meta &form) " file: " ~*file* 
         ))
     (def ~name c#)
     )
  )

(defcomponent a (do "a" (str "b")))

;;;;;;;;;;;;;;;;;
(defmacro a [] (get &env 'b))
;#'util.funxions/a
(let [b 1] (a))
;CompilerException java.lang.RuntimeException: Can't embed object in code, maybe print-dup not defined: clojure.lang.Compiler$LocalBinding@6d99ef1b, compiling:(NO_SOURCE_PATH:1:31) 

;the following is from http://blog.jayfields.com/2011/02/clojure-and.html
(defmacro show-env [] 
  (println (keys &env)) 
  `(println ~@(keys &env))
  )
;#'util.funxions/show-env
=> (let [band "zeppelin" city "london"] (show-env))
;(city band)
;london zeppelin
;nil

(defmacro show-env [] 
  (let [envkeys (keys &env)]
    `(do
       (prn (first (quote ~envkeys)))
       (prn (first (list ~@envkeys)))
       (prn (zipmap (quote ~envkeys) (list ~@envkeys)))
       )
    )
  )

=> (let [band "zeppelin" city "london"] (show-env))
;city
;"london"
;{band "zeppelin", city "london"}
;nil

;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;
