(ns t-util.transducer)


(defn t1
  ([f1]
   (fn [f2]
     (fn [x]
       (f2 (f1 x))))))

(defn doubler
  "Test function"
  [x] (* 2 x))

(defn test1_1
  "T1 does not compose without the help of an identity function "
  []
  (((comp (t1 doubler) (t1 inc)) identity) 1))

(defn test1_2
  "T1 composes by calling and not using the compose function
   composition is implicit"
  []
  (((t1 inc) doubler) 1))


(defn test1_3
  []
  ((t inc) ((t inc) doubler)))


(defn test1_4
  []
  (->
    (t1 inc)
    (t1 inc)
    (t1 inc)
    (t1 doubler)
    ))