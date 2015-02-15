(ns lobos.migrations
	;; exclude some clojure built-in symbols so we can use the lobos' symbols
	(:refer-clojure :exclude [alter drop
														bigint boolean char double float time])
	;; use only defmigration macro from lobos
	(:use (lobos [migration :only [defmigration]]
					core
					schema)))

(defmigration add-recipes-table
	(up [] (create
		(table :recipes
			(integer :id :primary-key :auto-inc )
			(varchar :name 100 )
			(timestamp :created (default (now))))))
	(down [] (drop (table :recipes ))))

(defmigration add-ingredients-table
	(up [] (create
					(table :ingredients
						(integer :id :primary-key :auto-inc )
						(varchar :name 250)
						(varchar :units 100)
						(timestamp :created (default (now)))
						(integer :recipe_id [:refer :recipes :id] :not-null))))
	(down [] (drop (table :ingredients ))))
