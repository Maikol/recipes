(ns recipes.db.core
  (:require [recipes.db.schema :as schema]
            [clojure.pprint :refer [pprint]])
  (:use korma.core)
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
      (let [ingredient-values (assoc (dissoc ingredient :id) :recipe_id recipe-id)]
        (insert ingredients (values ingredient-values))))))

(defn create-recipe [params]
  (let [recipe-params (dissoc params :ingredients)
        ingredients-map (get params :ingredients)]
    (transaction
      (let [recipe (insert recipes (values recipe-params))]
        (insert-ingredients (get recipe :id) ingredients-map)))))

(defn get-all-recipes []
  (select recipes (with ingredients)))
