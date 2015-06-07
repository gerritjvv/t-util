(ns monads.api)

(declare successM)

(defprotocol ICallable
  (-call [this f])
  (-call2 [this f xs]))

(defprotocol IMonad
  (-unitM [this x])
  (-bindM [this a])
  (-showM [this]))

(defprotocol IMaybe
  (-success? [this]))

(defprotocol IState
  (-state [this])
  (-value [this]))

(extend-protocol ICallable
  Object
  (-call [this f]
    (f this))
  (-call2 [this f x2]
    (f this x2)))

(deftype StateRecord [v state]
  IState
  (-state [_] state)
  (-value [_] v)
  IMonad
  (-unitM [_ state] (StateRecord. nil state))
  (-bindM [_ f] (-call2 v f state))
  (-showM [_] [v state]))

;identity monad
(defrecord I [v]
  ICallable
  (-call [_ f] (f v))
  IMonad
  (-unitM [_ x] (->I x))
  (-bindM [_ f] (f v))
  (-showM [_] v))

(defrecord MError [v]
  ICallable
  (-call [this _] this)
  (-call2 [this _ _] this)
  IMaybe
  (-success? [this] nil)
  IMonad
  (-unitM [_ x] (->MError x))
  (-bindM [this _] this)
  (-showM [_] v))

(defrecord Success [v]
  ICallable
  (-call [_ f] (-call v f))
  (-call2 [_ f x2] (-call2 v f x2))
  IMaybe
  (-success? [this] true)
  IMonad
  (-unitM [_ x] (->Success x))
  (-bindM [_ f] (-call v f))
  (-showM [_] v))

(defrecord Maybe [v]
  ICallable
  (-call [_ f] (-call v f))
  (-call2 [_ f x2] (-call2 v f x2))
  IMaybe
  (-success? [_] (-success? v))
  IMonad
  (-unitM [_ x] (->Maybe x))
  (-bindM [_ f]
    (-bindM v f))
  (-showM [_] (-showM v)))

(defonce TYPE-SUCCESS (type (->Success 0)))
(defonce TYPE-ERROR (type (->MError 0)))


(defn unitM [monad x]
  (-unitM monad x))

(defn bindM [monad f]
  (-bindM monad f))

(defn showM [monad]
  (-showM monad))

(defn identityM [v] (->I v))
(defn successM [v] (->Success v))
(defn errorM [v] (->MError v))

(defn success? [v]
  (-success? v))

(defn error? [v]
  (not (-success? v)))

(defn maybeM [v]
  (->Maybe v))

(defn svalue [s]
  "Returns the state value"
  (-value s))

(defn sstate
  "Returns the state's state"
  [s] (-state s))

(defmacro
  on-error
  "Helper macro that: if (error? v) binds the result to the binding succ and runs body else return the result
  Note that mbind is used to execute the body so that ~b is binded to the error value
  usage: (on-error v x (inc x))"
  [v b & body]
  `(if (error? ~v) (let [~b (showM ~v)] ~@body) ~v))

(defmacro
  on-success
  "Helper macro that: if (success? v) binds the result to the binding succ and runs body else return the result
  Note that mbind is used to execute the body so that ~b is binded to the success value
  usage: (on-success v x (inc x))"
  [v b & body]
  `(if (success? ~v) (bindM ~v (fn [x#] (let [~b x#] ~@body))) ~v))


(defmacro
  on-success-else
  "Helper macro that: if (success? v) binds the result to the b succ and runs body else return the result
  usage: (on-success-else v x (inc x) (dec x))"
  [v b succes-block error-block]
  `(let [y# ~v]
     (if (success? y#) (bindM y# (fn [x#] (let [~b x#] ~@succes-block))) (let [~b (showM y#)] ~@error-block))))
