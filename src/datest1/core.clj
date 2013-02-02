; Copyright (c) 2012-2013, AtKaaZ
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.

(ns datest1.core
  (:use [datomic.api :only (q db) :as d])
  (:use [runtime.q :as q] :reload-all)
;  (:require [clojure.pprint :as pprint])
  )

(defn -main
  "I don't do a whole lot."
  [& args]
  (println "Hello, World!"))





(println (sym-info randomundefinedeversymbol121321)) ; :undefined
(println (sym-info conn)) ; :undefined or :unbound

(def uri "datomic:mem://seattle2")

(defn xonnect []
  (println "connecting...")
  (try (def conn (d/connect uri))  (catch Exception e (:db/error (ex-data e))) )
  )

(def dbe (xonnect) )

(println (sym-info conn)) ; :unbound

(cond (= :unbound (sym-state conn))
      (cond (= :peer/db-not-found dbe) 
            (do
              (println "creating db, first time")
              (d/create-database uri)
              (def dbe (xonnect) )
              )
            )
      )

(println dbe)

(println (sym-info conn)) ; :bound


(q/show_state)

