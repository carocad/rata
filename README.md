# rata

[![Build Status](https://travis-ci.com/hiposfer/rata.svg?branch=master)](https://travis-ci.com/hiposfer/rata)
[![Clojars Project](https://img.shields.io/clojars/v/hiposfer/rata.svg)](https://clojars.org/hiposfer/rata)


Reactive [Datascript](https://github.com/tonsky/datascript/) queries through [Reagent's](https://github.com/reagent-project/reagent) track mechanism

## Example

Just use reagent as you normally would. The only difference is in
how you fetch the data. Use Datascript queries and pull patterns :)

```clojure
(ns example.core
  (:require [reagent.core :as reagent]
            [datascript.core :as data]
            [hiposfer.rata.core :as rata]))

;; WARNING: dont do this at home
(defonce foo (rata/init! (data/create-conn {:user/input {:db.unique :db.unique/identity}})))

(defn hello-world
  []
  (let [click-count @(rata/q! '[:find ?count .
                                :where [?input :user/input "click"]
                                       [?input :click/count ?count]])]
    [:div "For each click, you get a greeting :)"
     [:input {:type "button" :value "Click me!"
              :on-click #(rata/transact! [{:user/input "click"
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
