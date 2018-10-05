# rata

Reactive [Datascript](https://github.com/tonsky/datascript/) queries through [Reagent's](https://github.com/reagent-project/reagent) track mechanism

## Usage

```clojure
(ns example.core
  (:require [datascript.core :as data]
            [hiposfer.rata.core :as rata]))

;; WARNING: dont do this at home
(rata/init! (data/create-conn {:user/input {:db.unique :db.unique/identity}}))

(defn my-component
  []
  (let [click-count @(rata/q! [])]
    [:div "For each click, you get a greeting :)"
      [:input {:type "button" :value "Click me!"
               :on-click #(rata/transact! [{:user/input "click"
                                            :click/count (inc click-count)}])}]
      (for [i click-count]
        [:div "hello "])]))

```


## License

Copyright © 2018

Distributed under LGPLv3
