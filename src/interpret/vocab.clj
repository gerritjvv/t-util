(ns
  interpret.vocab)

(defrecord Term [op v1 v2])

(defrecord At [op pos term])

(defn var [x] (->Term :var x nil))
(defn con [i] (->Term :con i nil))
(defn add [a b] (->Term :add a b))
(defn lam [x v] (->Term :lam x v))
(defn app [t u] (->Term :app t u))
(defn eval-count [] (->Term :eval-count nil nil))

(defn at
  "At returns a At record but acts as a Term with the :op key"
  [pos term] (->At :at pos term))
