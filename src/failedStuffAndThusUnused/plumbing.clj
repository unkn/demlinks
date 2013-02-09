; Copyright (c) 2012-2013, AtKaaZ
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.

(ns failedStuffAndThusUnused.plumbing
  (:require [plumbing.core :as p])
  )

(def graph_TransformFlatfileToSymlink
  "bla bla"
  {
   ;^{:tag java.io.File}
   :ffull (r/fnk [flat-file] (.getAbsoluteFile (clojure.java.io/as-file flat-file)))
   ;^String 
   :full-path (r/fnk [ffull] (.getAbsolutePath ffull))
   :zmap (r/fnk [full-path] (parse-flatfile-wannabe-symlink full-path))
   :howManyLines (r/fnk [zmap] (getLinesCount zmap))
   ;^String 
   :sym-to (r/fnk [zmap] (getPointsTo zmap))
   }
  )

