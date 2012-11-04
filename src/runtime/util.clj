; Copyright (c) AtKaaZ and contributors.
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.


(ns runtime.util
  (:require [runtime.q :as q] :reload-all)
  )

(defmacro sym-state [zsym] ;;FIXME:
  ;there are like 6 cases:
  ;1. zsym is undefined symbol, returns :undefined
  ;2. zsym is defined but unbound var, returns :unbound
  ;3. zsym is defined and bound, returns :bound
  ;4. zsym is an expression ie. #() or (list 1 2 3), throws exception
  ;5. zsym is a special symbol ie. def   test this via (special-symbol? 'def) , returns :special
  ;6. zsym is a macro ie. defn, returns :macro
  `(try 
     (let [qsym# (quote ~zsym)]
           (if (not (symbol? qsym#)) ;ie. #() or (list 1 2 3)
             (throw (new RuntimeException "do not pass a form - symbol expected")) 
             ;else it's a symbol ie. def (special), defn (macro), somesymbol (symbol), someundefinedsymbol (symbol)
             (let [thevar# (resolve qsym#)] 
               (if (nil? thevar#) 
                 (if (special-symbol? qsym#)
                   :special
                   :undefined
                   )
                 (if (bound? thevar#)
;                   [:bound qsym#]
                   (if (macro-var? thevar#)
                     :macro
                     :bound
                     )
                   :unbound
                   ) 
                 )
               )
             )
           )
     (catch ClassCastException cce# (throw (new Exception (str "a" cce#))))
     )
  )
           
(defmacro encast 
  "used to wrap around possibly undefined symbols,
so that referencing them like this at compile time
will not cause exception but instead the exception
will happen only when the code containing 
the undefined symbol is reached/executed

ie. does ,(eval (quote a)) which is same as just  ,a"
  [zsym]
  
;  (let [lexically-exists? (get &env zsym) resolvable? (resolve zsym) ]
;    (if (or lexically-exists? resolvable?)
;      zsym
;      (throw (Exception. "C"))
;      )
;    `(let [qsym# (quote ~zsym)] 
;       (try ;I'll just let the exception fall through
         `(eval (quote ~zsym))
;         (catch Throwable t#
;           (throw (RuntimeException. (str "Symbol undefined `" qsym# "`" t#))) ;TODO: make ex-info
;           )
;         )
;       )
)


(defmacro sym-info [zsym]
  `(let [ss# (sym-state ~zsym)] 
     (if (not= :bound ss#)
       {:state ss# :value nil} 
       {:state ss# :value (encast ~zsym)}
       )
     )
  )


(defmacro get-as-var [sym-or-var]
  `(let [qsym# (quote sym-or-var)]
   (if (var? ~sym-or-var)
    ~sym-or-var
    (if (symbol? qsym#)
      (var ~sym-or-var)
      (throw (new RuntimeException (str "you should pass a symbol or a var not `" qsym# "`")))
      )
    )
  ))

;TODO: handle all other cases
(defmacro macro? [zsym]
  `(macro-var? (var ~zsym))
  )

(defmacro macro-var? [zvar]
  `(boolean (:macro (meta ~zvar)))
  )


(comment
(defn sym-info2 [zsym]
  (try
  (let [ss (sym-state zsym)]
    (if (= :bound ss)
      :bound
      :nil
      )
    )
  (catch Throwable rte (pr rte)) ;;cannot ever catch CompilerException in this case for "Unable to resolve symbol"
  )
  )
);comment


(q/show_state)
;(q/here)
