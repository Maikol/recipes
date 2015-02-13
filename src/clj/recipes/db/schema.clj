(ns recipes.db.schema
	(:use korma.db
				korma.core))

(def pg (postgres {
	:db "recipes_dev"
	:user "migueldeelias"
	:password ""
	}))

(defdb korma-db pg)
