(ns lobos.config
	(:use lobos.connectivity))

(def db
	{:classname "org.postgresql.Driver"
	:subprotocol "postgresql"
	:user "migueldeelias"
	:password ""
	:subname "//localhost:5432/recipes_dev"})

(open-global db)
