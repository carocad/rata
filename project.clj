(defproject hiposfer/rata "0.3.0"
  :description "State management through Datascript and reagent ratoms"
  :url "https://github.com/hiposfer/rata"
  :license {:name "LGPLv3"
            :url "https://github.com/hiposfer/rata/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.10.0" :scope "provided"]
                 [org.clojure/clojurescript "1.9.946" :scope "provided"]
                 [reagent "0.8.1" :scope "provided"]
                 [datascript "0.18.2" :scope "provided"]]
  ;; deploy to clojars as - lein deploy releases
  :deploy-repositories [["releases" {:sign-releases false :url "https://clojars.org/repo"}]
                        ["snapshots" {:sign-releases false :url "https://clojars.org/repo"}]])


;Although this is a rewrite of mpdairy/posh library it is much
;more useful and simple. The problem with posh is that it tries
;to do too much. It target both datomic and datascript. Therefore
;it recreates the reagent/atom implementation internally.
;
;This bring other problems with it:
;- all queries must be cached (but reagent is capable of doing that
;                               with reagent/track)
;- whenever the state changes, posh must figure out which query result
;should be updated (but reagent is capable of doing that by diffing
;                    the result of the queries)
;- only the currently viewable components's queries should be executed
;(but reagent already does that since all inactive reagent/track are
;  removed automatically)
;- all the problems above also make posh a big library with hundreds of lines
;(reagent.tx has barely 50 even with all datascript functionality !!)
