; Copyright (c) 2012-2013, AtKaaZ
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.

(ns demlinks.core
  (:require [taoensso.timbre :as timbre 
         :only (trace debug info warn error fatal spy)])
  )

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))


;so let me see what I want here, in terms of graphs
(defn 
  openPersistent
  "opens the specified graph with persistent storage ie. not in-memory"
  [pathOrConf]
  (timbre/info "persistent graph open")
  )

(defn 
  openInMemory
  "opens an in-memory graph"
  []
  (timbre/info "in memory graph open")
  )

(defn 
  close 
  "safely shutdown the graph"
  [graph] 
  (timbre/info "graph down")
  )
  
(defn 
  isOpen? 
  "true if open"
  [graph]
  
  )


(openPersistent "a")
(openPersistent (clojure.java.io/as-file "a"))
(openPersistent nil)
(openInMemory)
