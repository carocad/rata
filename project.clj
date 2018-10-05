(defproject hiposfer/rata "0.1.0"
  :description "Reactive Datascript queries"
  :url "https://github.com/hiposfer/hiposfer.rata"
  :license {:name "LGPLv3"
            :url "https://github.com/hiposfer/hiposfer.rata/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.9.0" :scope "provided"]
                 [org.clojure/clojurescript "1.9.946" :scope "provided"]
                 [reagent "0.7.0" :scope "provided"]
                 [datascript "0.16.6"]]
  ;; deploy to clojars as - lein deploy releases
  :deploy-repositories [["releases" {:sign-releases false :url "https://clojars.org/repo"}]])
