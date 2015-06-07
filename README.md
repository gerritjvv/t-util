# t-util

Practical "monad" patterns for clojure development to ease error handling especially when dealing
with I/O or other resources that usually throw exceptions to do the same.


Note that this library is also a test bed for ideas and that's why there are namespaces like
interpret.interpreter etc.

  * Public namespaces are kept stable.


[![Clojars Project](http://clojars.org/t-util/latest-version.svg)](http://clojars.org/t-util)

## Research

  * The essence of functional programming, http://www.eliza.ch/doc/wadler92essence_of_FP.pdf, The most useful paper on where monads begun and how to really use them |


## Public namespaces

`t-util.monads.api`


# Error or Success

A function call many times need to communicate error or success, this pattern is normally described using an interpreter as an example  
but can also be used for:  

  * File read write
  * Network I/O
  * JDBC calls
  * Lock acquire
  * Condition checking like validation

The standard way is by throwing an exception, but we can also have the function return a type that shows Success or Error.  
For the latter this allows the calling function to react differently on error and success in a more direct way (my opinion)  

```clojure

(require '[t-util.monads.api :as mapi])

(defn read-io [x] (if x (mapi/successM :true) (mapi/errorM "This is an error")))

(read-io nil)
;;#t_util.monads.api.MError{:v "This is an error"}

(read-io 1)
;;#t_util.monads.api.Success{:v :true}

(mapi/error? (read-io nil))
;; true
(mapi/success? (read-io 1))
;;true
 
;; helper macros these make is easier to work with success error situations

;; on-success-else fn variable-binding commands-if-success commands-if-error
(mapi/on-success-else (read-io 1) x (prn "read value" x) (prn "failed to read due to " x)) 
;;:true
(mapi/on-success-else (read-io nil) x (prn "read value" x) (prn (str "failed " x))) 
;;"failed This is an error"

;;value of a function
(mapi/showM (read-io 1))
;; :true 

```


## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0
