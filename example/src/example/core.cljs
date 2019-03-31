(ns example.core
  (:require [reagent.core :as reagent :refer [atom]]
            [datascript.core :as data]
            [hiposfer.rata.core :as rata]))

(enable-console-print!)

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
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)

