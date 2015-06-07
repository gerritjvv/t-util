(ns interpret.interpreter
  (:require [t-util.monads.api :as monad]
            [interpret.vocab :as vocab]))


;expects an object with :op
(defmulti interpret (fn [term env] (:op term)))


(defmethod interpret :default [term env] (monad/errorM (str "The term " term " is not supported")))

(defmethod interpret :at [{:keys [pos term]} env]
  (monad/on-error (interpret term env)
                  err
                  (monad/errorM (str err " line " pos))))

(defmethod interpret :eval-count [_ env]
  )

(defmethod interpret :var [{:keys [v1]} env]
  (if (contains? env v1)
    (monad/successM (get env v1))
    (monad/errorM (str "Undefined variable " v1))))

(defmethod interpret :con [term _]
  (monad/successM (:v1 term)))

(defmethod interpret :add [{:keys [v1 v2]} env]
  (monad/bindM (interpret v1 env) (fn [a]
                                    (monad/bindM (interpret v2 env) (fn [b]
                                                                      (monad/successM (+ a b)))))))

(defmethod interpret :lam [{:keys [v1 v2]} env]
  (let [name v1
        term v2]
    (monad/maybeM
      (monad/identityM
        (fn [a]
          ;we need something better here, Error will not be handled correctly
          (interpret term (assoc env name a)))))))

(defmethod interpret :app [{:keys [v1 v2]} env]
  (monad/bindM (interpret v1 env)
               (fn [f]
                 (if (fn? f)
                   (monad/bindM (interpret v2 env)
                                (fn [v]
                                  (apply f [v])))
                   (monad/errorM (str "A function was expected but got " f))))))

