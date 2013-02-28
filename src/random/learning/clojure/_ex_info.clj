(ns random.learning.clojure.-ex-info)

(ex-data (q/is 
     (thrown? clojure.lang.ExceptionInfo 
       (throw (ex-info "1" {:a 1})))))
;{:a 1}

