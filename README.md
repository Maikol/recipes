# Migrations

```
lein repl
(use 'lobos.core 'lobos.connectivity 'lobos.config 'lobos.migration 'lobos.migrations)
(binding [*src-directory* "src/clj/"] (migrate))
```
