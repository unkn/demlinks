; Copyright (c) 2012-2013, AtKaaZ
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.

(ns demlinks.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))


;so let me see what I want here, in terms of graphs
(defprotocol someGraph
  (openPersistent [pathOrConf] "opens the specified graph with persistent storage ie. not in-memory")
  (openInMemory [] "opens an in-memory graph")
  (close [graph] "safely shutdown the graph")
  (isOpen? [graph] "true is open")
  )


(extend-protocol someGraph
  java.lang.String
  (open
    [^String strpath]
    (println strpath)
    )
  
  java.io.File
  (open
    [^java.io.File fpath]
    (println fpath)
    )
  
;  nil
;  (open
;    [nill]
;    (println nill)
;    )
  )

(openPersistent "a")
(openPersistent (clojure.java.io/as-file "a"))
(open nil)
(openInMemory nil) ;this won't work
