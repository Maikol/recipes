(ns recipes.models.migration
  (:require [clojure.java.jdbc :as sql]
            [recipes.models.recipe :as recipe]))

(defn migrated? []
  (-> (sql/query recipe/spec
                 [(str "select count(*) from information_schema.tables "
                       "where table_name='recipes'")])
      first :count pos?))

(defn migrate []
  (when (not (migrated?))
    (print "Creating database structure...") (flush)
    (sql/db-do-commands recipe/spec
                        (sql/create-table-ddl
                         :recipes
                         [:id :serial "PRIMARY KEY"]
                         [:name :varchar "NOT NULL"]
                         [:created_at :timestamp
                          "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]))
    (println " done")))
