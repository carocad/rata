# rata

[![Build Status](https://travis-ci.com/hiposfer/rata.svg?branch=master)](https://travis-ci.com/hiposfer/rata)
[![Clojars Project](https://img.shields.io/clojars/v/hiposfer/rata.svg)](https://clojars.org/hiposfer/rata)


State management through [Datascript](https://github.com/tonsky/datascript/)
and [Reagent's](https://github.com/reagent-project/reagent) track mechanism

## usage

Rata hooks itself into the transactor of Datascript. So you just need to register
it against Datascript's connection. From that point onwards, you should use
`rata/q` and `rata/pull` with the **connection**. Rata also provides a `transact!`
functions which accepts a sequence of middlewares; middlewares are useful to add
extra capabilities like transaction logging, data based effects, etc.

Check the [example](example) directory for a working project

```clojure
(ns example.core
  (:require [reagent.core :as reagent :refer [atom]]
            [datascript.core :as data]
            [hiposfer.rata.core :as rata]))

(def schema {:user/input {:db.unique :db.unique/identity}})

(defn- logger
  [middleware]
  (fn log* [db transaction]
    (let [transaction (middleware db transaction)]
      (apply js/console.log (clj->js transaction))
      transaction)))

;; state is a standard Clojure Atom containing a Datascript DB
(defonce state (rata/listen! (data/create-conn schema)
                             [logger])) ;; middleware chain

(defn example
  []
  (let [click-count @(rata/q! '[:find ?count .
                                :where [?input :user/input "click"]
                                       [?input :click/count ?count]]
                              state)]
    [:div "For each click, you get a greeting :)"
     [:input {:type "button" :value "Click me!"
              ;; rata/transact! usage here instead of data/transact! ------
              :on-click #(rata/transact! state [{:user/input "click"
                                                 :click/count (inc click-count)}])}]
     (for [i (range click-count)]
       ^{:key i}
       [:div "hello " i])]))

(reagent/render-component [example] (. js/document (getElementById "app")))

(defn on-js-reload [])
```


## License

Copyright © 2018

Distributed under LGPLv3
