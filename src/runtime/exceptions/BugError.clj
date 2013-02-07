;FIXME: epic failing here
(ns runtime.exceptions.BugError
  ;(:import java.lang.Error)
  ;)

  (:gen-class
    ;:name runtime.exceptions.BugError
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
  )

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
