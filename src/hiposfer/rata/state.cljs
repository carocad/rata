(ns hiposfer.rata.state)

;; NOTE: it is possible, at any time, to inspect the *complete* state of the
;; app by just evaluating conn in the repl

;; Holds the current state of the complete app
(defonce conn nil)
