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
            [reagent.core :as r]
            [hiposfer.rata.state :as state]))

(defn- listen!
  "registers a listener for the connection transactions. Returns a
  reagent/ratom whose value will automatically updated on every
  transact"
  [conn]
  (let [ratom (r/atom @conn)]
    (data/listen! conn ::tx (fn [tx-report] (reset! ratom (:db-after tx-report))))
    (swap! conn assoc ::ratom ratom)))

(defn- unlisten!
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

(defn init!
  "takes a Datascript conn and starts listening to its transactor for changes"
  [conn]
  (when (some? state/conn) ;; just in case
    (unlisten! state/conn)
    (unlisten! conn))
  (set! state/conn conn)
  (listen! state/conn))

(defn pull!
  "same as datascript/pull but returns a ratom which will be updated
  every time that the value of conn changes"
  [selector eid]
  (r/track! pull* (::rtx/ratom @state/conn) selector eid))

(defn q!
  "Returns a reagent/atom with the result of the query.
  The value of the ratom will be automatically updated whenever
  a change is detected"
  [query & inputs]
  (r/track! q* query (::rtx/ratom @state/conn) inputs))

(defn db
  "return the Datascript Database instance that rework currently uses.
  The returned version is immutable, therefore you cannot use
  datascript/transact!.

  This is meant to keep querying separate from mutations"
  []
  @state/conn)

(defn transact!
  "same as Datascript transact except that uses the connection
  passed at init!"
  ([tx-data]
   (transact! tx-data nil))
  ([tx-data tx-meta]
   (data/transact! state/conn tx-data tx-meta)))
