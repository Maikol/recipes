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

(defroutes home-routes
  (GET "/" [] (home-page))
  (POST "/recipes/new" {:keys [body-params]} (models/create-recipe body-params))
  (GET "/recipes" [] (models/get-all-recipes))
  (DELETE "/recipes/:id" [id] (models/destroy-recipe id)))
