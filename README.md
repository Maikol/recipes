# Migrations

```
lein repl
(use 'lobos.core 'lobos.connectivity 'lobos.config 'lobos.migration 'lobos.migrations)
(binding [*src-directory* "src/clj/"] (migrate))
```

# Korma

```
lein repl
(use 'korma.db 'korma.core 'recipes.db.schema 'recipes.db.core)
(select recipes (with ingredients))
```
