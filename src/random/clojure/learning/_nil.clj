(ns random.clojure.learning.-nil
  (:use clojure.test)
  )

(deftest niltruefalse
  
(is (nil? nil))
(is (false? false))
(is (true? true))

(is (not (nil? nil?)))
(is (not (nil? false)))
(is (not (nil? true)))
(is (not (nil? 0)))
(is (not (nil? "")))
(is (not (nil? '())))
(is (not (nil? (list))))

(is (not (false? false?)))
(is (not (false? true)))
(is (not (false? 1)))
(is (not (false? 0)))
(is (not (false? nil)))
(is (not (false? "false")))
(is (not (false? 36rfalse)))

(is (not (true? true?)))
(is (not (true? false)))
(is (not (true? nil)))
(is (not (true? 1)))
(is (not (true? 0)))
(is (not (true? "true")))
(is (not (true? 36rtrue)))

)
;I'm ctrl+alt+L -ing this from eclipse+ccw, so:
(run-tests)