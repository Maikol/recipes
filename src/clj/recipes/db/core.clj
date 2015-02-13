(ns recipes.db.core
  (:require [recipes.db.schema :as schema])
  (:use korma.core))

(declare recipes ingredients)

(defentity recipes
  (pk :id)
  (has-many ingredients {:fk :recipe_id}))

(defentity ingredients
  (pk :id)
  (belongs-to recipes {:fk :recipe_id}))

(defn create-recipe [name]
  (insert recipes
    (values {:name [name]})))
