# rata

[![Build Status](https://travis-ci.com/hiposfer/rata.svg?branch=master)](https://travis-ci.com/hiposfer/rata)
[![Clojars Project](https://img.shields.io/clojars/v/hiposfer/rata.svg)](https://clojars.org/hiposfer/rata)


Reactive [Datascript](https://github.com/tonsky/datascript/) queries through [Reagent's](https://github.com/reagent-project/reagent) track mechanism

## usage

Rata hooks itself into the transactor of Datascript. So you just need to register
it against Datascript's connection. From that point onwards, you should use
`rata/q` and `rata/pull` with the **connection**

```clojure
(ns example.core
  (:require [reagent.core :as reagent]
            [datascript.core :as data]
            [hiposfer.rata.core :as rata]))

(defonce state (data/create-conn {:user/input {:db.unique :db.unique/identity}}))

(rata/listen! state)

(defn hello-world
  []
  (let [click-count @(rata/q! '[:find ?count .
                                :where [?input :user/input "click"]
                                       [?input :click/count ?count]]
                              state)] ;; this is conn not the db as in datascript !!
    [:div "For each click, you get a greeting :)"
     [:input {:type "button" :value "Click me!"
              :on-click #(data/transact! state [{:user/input "click"
                                                 :click/count (inc click-count)}])}]
     (for [i (range click-count)]
       ^{:key i}
       [:div "hello " i])]))

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))
```


## License

Copyright © 2018

Distributed under LGPLv3
