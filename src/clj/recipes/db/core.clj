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

;;
;; Ingredients
;;

(def ingredient-types {:quantity #(Double/parseDouble %)})

(defn insert-ingredients [recipe-id ingredients-map]
  (let [ingredients-seq (vals ingredients-map)]
    (doseq [ingredient ingredients-seq]
      ;; Should be better implemented
      (let [ingredient-values (reduce-kv #(update-in %1 [%2] %3)
                                          (assoc (dissoc ingredient :id) :recipe_id recipe-id)
                                          ingredient-types)]
        (insert ingredients (values ingredient-values)))))
    {:status "ok"})

;;
;; Recipes
;;

(defn create-recipe [params]
  (let [recipe-params (get params :recipe)
        ingredients-map (get params :ingredients)]
    (transaction
      (let [recipe (insert recipes (values recipe-params))]
        (insert-ingredients (get recipe :id) ingredients-map)))))

(defn get-all-recipes []
  (select recipes))

(defn destroy-recipe [recipe-id]
  (let [recipe-id-int (Integer/parseInt recipe-id)]
    (delete ingredients (where {:recipe_id recipe-id-int}))
    (delete recipes (where {:id recipe-id-int}))
    {:status "ok"}))
