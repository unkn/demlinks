; Copyright (c) 2012-2013, AtKaaZ
; All rights reserved.
; The use and distribution terms for this software are covered by the
; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
; which can be found in the file epl-v10.txt at the root of this distribution.
; By using this software in any fashion, you are agreeing to be bound by
; the terms of this license.
; You must not remove this notice, or any other, from this software.


;FIXME: epic failing here
(ns runtime.exceptions
  ;(:import java.lang.Error)
  )

  (gen-class
    :name runtime.exceptions.BugError
    :extends [java.lang.RuntimeException]
    :implements [clojure.lang.IDeref]
    ;:constructors {[] []}
    :constructors {[java.util.Map String] [String]
                   [java.util.Map String Throwable] [String Throwable]}
    :state info
    :init init
    :prefix "-"
;    :main false
    
    :methods [[getInfo [] java.util.Map]
              [addInfo [Object Object] void]]
    )
  ;:methods [[setLocation [String] void]
  ;          [getLocation [] String]])

(import 'runtime.exceptions.BugError)

;; when we are created we can set defaults if we want.
#_(defn -init []
  "store our fields as a hash"
  [[] (atom {:location "default"})])

;; little functions to safely set the fields.
(defn setfield
  [this key value]
      (swap! (.state this) into {key value}))

(defn getfield
  [this key]
  (@(.state this) key))

;; "this" is just a parameter, not a keyword
#_(defn -setLocation [this loc]
  (setfield this :location loc))

#_(defn  -getLocation
  [this]
  (getfield this :location))

(defn- -init
  ([info message]
    [[message] (atom (into {} info))])
  ([info message ex]
    [[message ex] (atom (into {} info))]))

(defn- -deref
  [^BugError this]
  @(.info this))

(defn- -getInfo
  [this]
  @this)

(defn- -addInfo
  [^BugError this key value]
  (swap! (.info this) assoc key value))
