(ns recipes.routes.home
            (:require [recipes.layout :as layout]
                      [recipes.util :as util]
                      [compojure.core :refer :all]
                      [noir.response :refer [edn]]
                      [clojure.pprint :refer [pprint]]
                      [recipes.models.recipe :as model]
                      [recipes.models.migration :as schema]))

(require '[clojure.java.jdbc :as j])

(def postgres-db {:classname "org.postgresql.Driver"
                  :subprotocol "postgresql"
                  :subname "//localhost:5432/recipes_app_dev"})

(defn home-page []
      (layout/render
        "index.html" {:docs (util/md->html "/md/docs.md")}))

(defn save-document [doc]
      (schema/migrate)
      (pprint doc)
      (model/create (get doc :recipe-name))
      {:status "ok"})

(defroutes home-routes
  (GET "/" [] (home-page))
  (POST "/save" {:keys [body-params]}
    (edn (save-document body-params))))
