(ns interpret.monad-test
  (:require [clojure.test :refer :all]
            [monads.api :as monad]))

(deftest monad-on-success
  []
  (is (monad/on-error (monad/errorM 1) x (dec x)) 0)
  (is (monad/showM (monad/on-error (monad/successM 1) x (dec x))) 1)
  (is (monad/on-success (monad/successM 1) x (dec x)) 0)
  (is (monad/showM (monad/on-success (monad/->MError 1) x (dec x))) 1)

  (is (monad/on-success-else (monad/successM 1) x (inc x) (dec x)) 2)
  (is (monad/on-success-else (monad/errorM 1) x (inc x) (dec x)) 0))


