(ns ingredients.models.recipe
  (:require [clojure.java.jdbc :as sql]))

(def spec (or (System/getenv "DATABASE_URL")
              "postgresql://localhost:5432/recipes_app_dev"))

(defn all []
  (into [] (sql/query spec ["select * from ingredients order by id desc"])))

(defn create [ingredient]
  (sql/insert! spec :ingredietns [:name ] [ingredient]))
