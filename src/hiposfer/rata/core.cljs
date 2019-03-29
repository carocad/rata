(ns hiposfer.rata.core
  "A reagent reactive wrapper around Datascript's transactor

  It provides both reactive queries and pull patterns.

  Custom transactions are supported through middlewares. A
  middleware is a function with the signature
  (middleware) => (db transaction) => transaction

  Therefore a middleware is a high order function which returns
  a function that will receive the current state and the current
  transaction and returns a transaction.
  The transaction resulting from the chain will be used instead
  of the original one"
  (:require [datascript.core :as data]
            [reagent.core :as r]))

;; (tr)ansaction (ident)ity. The most basic middleware.
;; Returns the given transaction
(defn- trident [_ tx] tx)

(defn listen!
  "registers a listener for the connection transactions. Returns
  the conn object itself with the added listener and state holder

  Subsequent usages of conn in q! and pull! will return reactive
  atoms that will update their value whenever the Datascript value
  changes

  middlewares is a sequence of middlewares like [f g h ...] which
  will be applied in order to each transaction"
  ([conn]
   (listen! conn nil))
  ([conn middlewares]
   (let [ratom      (r/atom @conn) ;; initial state
         middleware (if (empty? middlewares) trident
                      (reduce (fn [result f] (f result))
                              trident
                              middlewares))]
     (data/listen! conn ::tx (fn [tx-report] (reset! ratom (:db-after tx-report))))
     (alter-meta! conn assoc ::ratom ratom)
     (alter-meta! conn assoc ::middleware middleware)
     ;; return the conn again to allow standard datascript usage
     conn)))


(defn unlisten!
  "unregisters the transaction listener previously attached with
  listen!"
  [conn]
  (data/unlisten! conn ::tx)
  (alter-meta! conn dissoc ::ratom)
  (alter-meta! conn dissoc ::middleware))

(defn- q*
  "same as datascript/q but takes a reagent/atom as connection.
  Useful to use with reagent/track"
  [query ratom inputs]
  (apply data/q query @ratom inputs))

(defn- pull*
  "same as datascript/pull but takes a reagent/atom as connection.
   Useful to use with reagent/track"
  [ratom selector eid]
  (data/pull @ratom selector eid))

(defn pull!
  "same as datascript/pull but returns a ratom which will be updated
  every time that the value of conn changes"
  [conn selector eid]
  (r/track! pull* (::ratom (meta conn) selector eid)))

(defn q!
  "Returns a reagent/atom with the result of the query.
  The value of the ratom will be automatically updated whenever
  a change is detected"
  [query conn & inputs]
  (r/track! q* query (::ratom (meta conn) inputs)))

(defn transact!
  "same as datascript/transact! but uses the transaction from the middleware
   chain instead of tx-data"
  ([conn tx-data]
   (transact! conn tx-data nil))
  ([conn tx-data tx-meta]
   (let [middleware (::middleware (meta conn))]
     (data/transact! conn
                     (middleware @conn tx-data)
                     tx-meta))))
