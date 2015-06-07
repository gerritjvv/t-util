(ns interpret.interpreter-test
  (:require [clojure.test :refer :all]
            [interpret.interpreter :as int]
            [interpret.vocab :as vocab]
            [t-util.monads.api :as monad]))


(defonce term0 (vocab/app (vocab/lam "x" (vocab/add (vocab/var "x") (vocab/var "x"))) (vocab/add (vocab/con 10) (vocab/con 11))))

(defonce term-error (vocab/app (vocab/lam "x" (vocab/add (vocab/var "x") (vocab/var "b"))) (vocab/add (vocab/con 10) (vocab/con 11))))

(defonce term-error-pos (vocab/app (vocab/lam "x" (vocab/add (vocab/var "x") (vocab/at 10 (vocab/var "b")))) (vocab/add (vocab/con 10) (vocab/con 11))))

(defonce term-error-function (int/interpret (vocab/app (vocab/con 1) (vocab/con 2)) {}))

(deftest interpret-term0
  "Test correct interpretation of lambda, apply, var and constant"
  []
  (is (monad/showM (int/interpret term0 {})) 42))

(deftest interpret-term-error
  "Test lookup failure"
  []
  (is (monad/error? (int/interpret term-error {})) true))

(deftest interpret-term-error-function
  "Test function expected error message"
  []
  (is (monad/error? (int/interpret term-error-function {})) true))

(deftest interpret-term-error-pos
  "Test that the at term is correctly interpreted and the line number added to the error message"
  []
  (is (monad/showM (int/interpret term-error-pos {})) "Undefined variable b line 10"))