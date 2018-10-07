(ns hiposfer.rata.core
  "A reagent reactive wrapper around Datascript's transactor

  Although this is a rewrite of mpdairy/posh library it is much
  more useful and simple. The problem with posh is that it tries
  to do too much. It target both datomic and datascript. Therefore
  it recreates the reagent/atom implementation internally.

  This bring other problems with it:
  - all queries must be cached (but reagent is capable of doing that
     with reagent/track)
  - whenever the state changes, posh must figure out which query result
    should be updated (but reagent is capable of doing that by diffing
     the result of the queries)
  - only the currently viewable components's queries should be executed
     (but reagent already does that since all inactive reagent/track are
     removed automatically)
  - all the problems above also make posh a big library with hundreds of lines
    (reagent.tx has barely 50 even with all datascript functionality !!)

    See https://reagent-project.github.io/news/news060-alpha.html
    for more information"
  (:require [datascript.core :as data]
            [reagent.core :as r]))

(defn listen!
  "registers a listener for the connection transactions. Returns
  the conn object itself with the added listener and state holder

  Subsequent usages of conn in q! and pull! will return reactive
  atoms that will update their value whenever the Datascript value
  changes"
  [conn]
  (when (nil? (::ratom @conn))
    (let [ratom (r/atom @conn)] ;; initial state
      (data/listen! conn ::tx (fn [tx-report] (reset! ratom (:db-after tx-report))))
      ;; keep a reference to the ratom to avoid GC
      (swap! conn assoc ::ratom ratom)))
  ;; return the conn again to allow standard datascript usage
  conn)

(defn unlisten!
  "unregisters the transaction listener previously attached with
  listen!"
  [conn]
  (data/unlisten! conn ::tx)
  (swap! conn dissoc ::ratom))

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
  (r/track! pull* (::ratom @conn) selector eid))

(defn q!
  "Returns a reagent/atom with the result of the query.
  The value of the ratom will be automatically updated whenever
  a change is detected"
  [query conn & inputs]
  (r/track! q* query (::ratom @conn) inputs))
