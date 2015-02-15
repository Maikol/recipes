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

# Models

## Recipe

```
id (auto inc)
name
instructions
avatar_url
ingredients (has many)
created (Date now)
```

## Ingredient

```
id (auto inc)
name
units
quantity
recipe_id (belongs to recipe)
created (Date now)
```
