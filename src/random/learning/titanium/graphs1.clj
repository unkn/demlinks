;this from: http://titanium.clojurewerkz.org/articles/getting_started.html
(ns random.learning.titanium.graphs1
  (:require 
    [clojurewerkz.titanium.graph :as tg]
    [clojurewerkz.titanium.elements :as te]
    [clojurewerkz.titanium.edges :as ted]
    [clojurewerkz.titanium.query :as tq]
    )
  )

;(def g (tg/open-in-memory-graph))
;(println (System/getProperty "java.io.tmpdir"))
(def g (tg/open 
         "c:\\1\\some"
         ;(System/getenv "java.io.tmpdir")
         ))

  
(def v (tg/add-vertex g {:name "Titanium" :language "Clojure"}))

(te/properties-of v)
;{"name" "Titanium", "language" "Clojure"}

;v
;#<PersistStandardTitanVertex v[4]>

(te/property-of v :name)
;"Titanium"

(te/id-of v)
;4

(te/property-names v)
;#{"name" "language"}

(te/assoc! v "status"     "crawled"
             "crawled-at" "crap"
                            )
;#<PersistStandardTitanVertex v[4]>

(te/properties-of v)
;{"status" "crawled", "crawled-at" "crap", "name" "Titanium", "language" "Clojure"}

(te/merge! v {"status" "crawled" "crawled-at" "date" "new" "stuff"})
;#<PersistStandardTitanVertex v[4]>

(te/properties-of v)
;{"new" "stuff", "status" "crawled", "crawled-at" "date", "name" "Titanium", "language" "Clojure"}

(te/dissoc! v "status" "crawled-at")
;#<PersistStandardTitanVertex v[4]>

(te/properties-of v)
;{"new" "stuff", "name" "Titanium", "language" "Clojure"}

(te/clear! v)
;#<PersistStandardTitanVertex v[4]>

(te/properties-of v)
;{}

g
;#<TitanInMemoryBlueprintsGraph titaninmemoryblueprintsgraph[null]>

(tg/remove-vertex g v)
;nil

g
;#<TitanInMemoryBlueprintsGraph titaninmemoryblueprintsgraph[null]>

v
;#<PersistStandardTitanVertex v[4]>


(let [p1 (tg/add-vertex g {:title "ClojureWerkz" :url "http://clojurewerkz.org"})
      p2 (tg/add-vertex g {:title "Titanium"     :url "http://titanium.clojurewerkz.org"})]
  (tg/add-edge g p1 p2 "links"))
;#<PersistLabeledTitanEdge e[95:116:358][116-links->160]>

(def p1 (tg/add-vertex g {:title "ClojureWerkz" :url "http://clojurewerkz.org"}))
(def p2 (tg/add-vertex g {:title "Titanium"     :url "http://titanium.clojurewerkz.org"}))
(def e (tg/add-edge g p1 p2 "links" {:verified-on "February 11th, 2013"}))
;#<PersistLabeledTitanEdge e[109:192:358][192-links->204]>

(te/properties-of e)
;{"verified-on" "February 11th, 2013"}

(te/property-of e :verified-on)
;"February 11th, 2013"

(te/id-of e)
;#<RelationIdentifier 109:192:358>

(te/property-names e)
;#{"verified-on"}

(te/assoc! e "verified-on" "date")
;#<PersistLabeledTitanEdge e[109:192:358][192-links->204]>

(te/properties-of e)
;{"verified-on" "date"}

(te/dissoc! e "status" "verified-on")
;#<PersistLabeledTitanEdge e[109:192:358][192-links->204]>

(te/properties-of e)
;{}

(te/assoc! e "verified-on" "date")

(te/properties-of e)


(te/clear! e)
;#<PersistLabeledTitanEdge e[109:192:358][192-links->204]>
(te/properties-of e)
;{}

(tg/remove-edge g e)
;nil

;Fetching Vertices
(def v1 (tg/add-vertex g {:age 28 :name "Michael"}))
(def v2 (tg/add-vertex g {:age 26 :name "Alex"}))
(def v1_ (tg/get-vertex g (te/id-of v1)))
;#<PersistStandardTitanVertex v[236]>
(te/properties-of v1_)
;{"name" "Michael", "age" 28}

;To find vertices by a key/value pair of properties
(def xs (set (tg/get-vertices g "name" "Alex")))
;13/02/11 19:40:54 WARN transaction.AbstractTitanTx: getVertices is invoked with a non-indexed key [name] which requires a full database scan. Create key-indexes for better performance.
xs
;#{#<PersistStandardTitanVertex v[264]>}

;To find all the graph vertices
(set (tg/get-vertices g))
;#{#<PersistStandardTitanVertex v[264]> #<PersistStandardTitanVertex v[204]> #<PersistStandardTitanVertex v[236]> #<PersistStandardTitanVertex v[116]> #<PersistStandardTitanVertex v[160]> #<PersistStandardTitanVertex v[192]>}

(map te/properties-of (tg/get-vertices g))
;({"title" "ClojureWerkz", "url" "http://clojurewerkz.org"} {"title" "ClojureWerkz", "url" "http://clojurewerkz.org"} {"title" "Titanium", "url" "http://titanium.clojurewerkz.org"} {"name" "Alex", "age" 26} {"name" "Michael", "age" 28} {"title" "Titanium", "url" "http://titanium.clojurewerkz.org"})

;To find an edge by id (if it is known)
(def e (tg/add-edge g v1 v2 "friend"))
;#'random.learning.titanium.graphs1/e
e
;#<PersistLabeledTitanEdge e[147:236:566][236-friend->264]>

(= e (tg/get-edge g (te/id-of e)))
;true

;To find edges by a key/value pair of properties
(def e  (tg/add-edge g v1 v2 "friend" {:since 2008}))
e
;#<PersistLabeledTitanEdge e[149:236:566][236-friend->264]>

(tg/get-edges g "since" 2008)
;#<PropertyFilteredIterable com.tinkerpop.blueprints.util.PropertyFilteredIterable@65cc892e>
(map te/properties-of (tg/get-edges g "since" 2008))
;({"since" 2008})

;To find all the graph edges
(tg/get-edges g)
;#<VertexCentricEdgeIterable com.thinkaurelius.titan.graphdb.util.VertexCentricEdgeIterable@4f47e0ba>

(map te/properties-of (tg/get-edges g))
;({} {} {"since" 2008})

(map 
     #(te/properties-of (ted/get-vertex % :in))
     (tg/get-edges g))
;({"name" "Alex", "age" 26} {"name" "Alex", "age" 26} {"title" "Titanium", "url" "http://titanium.clojurewerkz.org"})
(map 
     #(te/properties-of (ted/get-vertex % :out))
     (tg/get-edges g))
;({"name" "Michael", "age" 28} {"name" "Michael", "age" 28} {"title" "ClojureWerkz", "url" "http://clojurewerkz.org"})


(let [
      m1 {"station" "Boston Manor" "lines" #{"Piccadilly"}}
      m2 {"station" "Northfields" "lines" #{"Piccadilly"}}
      v1 (tg/add-vertex g m1)
      v2 (tg/add-vertex g m2)
      e (tg/add-edge g v1 v2 "links")
      ]
  (assert (= "links" (ted/label-of e)))
  (assert (= v2 (ted/head-vertex e)))
  (assert (= v1 (ted/tail-vertex e)))
  )

;(assert false)


(import '[com.tinkerpop.blueprints TransactionalGraph$Conclusion])
(.stopTransaction 
  g 
  com.tinkerpop.blueprints.TransactionalGraph$Conclusion/SUCCESS
  )

#_(.stopTransaction 
  g 
  com.tinkerpop.blueprints.TransactionalGraph$Conclusion/FAILURE
  )

(def tx (.startTransaction g))
(assert 
  (instance? 
    com.tinkerpop.blueprints.TransactionalGraph
    tx)
  )

(.stopTransaction 
  tx 
  TransactionalGraph$Conclusion/FAILURE)

(.stopTransaction
  g
  TransactionalGraph$Conclusion/FAILURE)

