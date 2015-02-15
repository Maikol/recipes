(ns recipes.routes.home
  (:require [recipes.layout :as layout]
            [recipes.util :as util]
            [compojure.core :refer :all]
            [noir.response :refer [edn]]
            [clojure.pprint :refer [pprint]]
            [recipes.db.core :as models]))

(require '[clojure.java.jdbc :as j])

(def postgres-db {:classname "org.postgresql.Driver"
                  :subprotocol "postgresql"
                  :subname "//localhost:5432/recipes_app_dev"})

(defn home-page []
      (layout/render
        "index.html" {:docs (util/md->html "/md/docs.md")}))

(defn save-document [doc]
      (pprint doc)
      (models/create-recipe doc)
      {:status "ok"})

(defroutes home-routes
  (GET "/" [] (home-page))
  (POST "/save" {:keys [body-params]}
    (edn (save-document body-params))))
