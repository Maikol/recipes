(ns recipes.db.core
  (:require [recipes.db.schema :as schema]
            [clojure.pprint :refer [pprint]])
  (:use korma.db))

(declare recipes ingredients)

(defentity recipes
  (pk :id)
  (has-many ingredients {:fk :recipe_id}))

(defentity ingredients
  (pk :id)
  (belongs-to recipes {:fk :recipe_id}))

(defn insert-ingredients [recipe-id ingredients-map]
  (let [ingredients-seq (vals ingredients-map)]
    (doseq [ingredient ingredients-seq]
      (let [ingredient-name (get ingredient :val)]
        (insert ingredients (values {:name ingredient-name :recipe_id recipe-id}))))))

(defn create-recipe [params]
  (let [recipe-name (get params :recipe-name)
        ingredients-map (get params :ingredients)]
    (transaction
      (let [recipe (insert recipes (values {:name recipe-name}))]
        (insert-ingredients (get recipe :id) ingredients-map)))))
