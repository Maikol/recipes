(ns recipes.dev
  (:require [recipes.core :as core]
            [figwheel.client :as figwheel :include-macros true]
            [reagent.core :as r]))

(enable-console-print!)

(figwheel/watch-and-reload
  :websocket-url "ws://localhost:3449/figwheel-ws"
  :jsload-callback (fn [] (r/force-update-all)))

(core/init!)
