# AGENTS.md

This repository is a Spring Boot (Java 21) service for M-Pesa payment processing.
Use this file as the operating guide for agentic work in this repo.

## Build, Run, and Test
- Build (compile): `mvn clean compile`
- Package: `mvn clean package`
- Run app (dev profile default): `mvn spring-boot:run`
- Run all tests: `mvn test`
- Run a single test class: `mvn -Dtest=PaymentsApplicationTests test`
- Run a single test method: `mvn -Dtest=PaymentsApplicationTests#contextLoads test`

Notes:
- No dedicated lint/format task is configured in Maven.
- Tests use JUnit 5; `@ActiveProfiles("test")` uses H2 and disables Flyway.

## Where Things Live
- Controllers: `src/main/java/io/github/wmjuguna/daraja/controllers`
- Services: `src/main/java/io/github/wmjuguna/daraja/services`
- Repositories: `src/main/java/io/github/wmjuguna/daraja/repositories`
- Entities: `src/main/java/io/github/wmjuguna/daraja/entities`
- DTOs (records only): `src/main/java/io/github/wmjuguna/daraja/dtos`
- Utils + converters: `src/main/java/io/github/wmjuguna/daraja/utils`
- DB migrations: `src/main/resources/db/migrations`

## Code Style and Conventions
### Java basics
- Java version is 21; use modern language features when safe.
- Keep classes in the `io.github.wmjuguna.daraja` package tree.
- Use Lombok where the project already uses it (`@Getter`, `@Setter`, `@Builder`, etc.).
- Prefer constructor injection (see service/controller patterns).
- Avoid adding comments unless necessary for non-obvious logic.

### Imports and formatting
- Use explicit imports (no wildcard imports).
- Keep import groups consistent: Java/Jakarta, third-party, then project imports.
- Keep annotations on their own lines when they are long (see controllers).
- Use 4-space indentation and standard Spring formatting.

### Naming
- Classes: PascalCase.
- Methods/fields: camelCase.
- DTO records end with `Request`, `Response`, or `DTO` as appropriate.
- Migrations use snake_case table/column names.

### Error handling and logging
- Prefer domain-specific exceptions in `io.github.wmjuguna.daraja.exceptions`.
- Let controllers return `ResponseEntity` with `ResponseTemplate` for structured responses.
- Log meaningful business events with `log.info`/`log.error` (avoid noisy logs).

## API and Swagger Documentation
Every REST endpoint must be fully documented:
- `@Operation` with summary, description, and tags.
- `@ApiResponses` including success and error examples.
- `@Parameter` for all path/query/body params.
- Examples must use realistic M-Pesa data.

## DTO Standards (Records Only)
- All DTOs are Java records, no DTO classes.
- Every record parameter must have `@JsonProperty("field_name")`.
- Use `@Schema` at record level and on every field.
- Include constraints and examples in `@Schema`.
- Validation annotations go on record parameters.

## Entity Standards
- Every entity must have:
  - `id` as BIGSERIAL primary key.
  - `uuid` as VARCHAR(36) unique identifier.
  - `created_at` and `updated_at` timestamps via the Base entity.
- All tables must include timestamps and entities should extend the Base entity.
- Always specify `@Table(name = "table_name")`.
- Use `@Column(name = "column_name")` only (no validation in entities).
- Relationships use UUID fields and join by `uuid` (not `id`):
  - Keep both the UUID column and the object relationship.

Example relationship pattern:
```
@Column(name = "related_entity_uuid")
private String relatedEntityUuid;

@OneToOne/@ManyToOne/@OneToMany
@JoinColumn(name = "related_entity_uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
private RelatedEntity relatedEntity;
```

## Enum and Converter Standards
- Enum fields use `@Convert(converter = EnumConverter.class)`.
- Place converters in `io.github.wmjuguna.daraja.utils.converters`.

## Migration Standards (Flyway)
- Location: `src/main/resources/db/migrations/`
- Versioning: `Vyyyymmddhhmm__Description.sql` (use `date +%Y%m%d%H%M`).
- Define constraints, indexes, and validation in migrations.
- Use UUIDs for foreign keys (reference `uuid`).

## Configuration and Security
- Credentials must be stored in DB (merchant_config), not env vars.
- Database config comes from env vars with defaults in `application.yaml`.
- Do not log or commit sensitive secrets.

## Tests and Profiles
- Use `@ActiveProfiles("test")` for test classes.
- H2 is the test DB; Flyway should be disabled in tests.

## Commits
- Follow git conventions with a single summarized line per commit message.

## External Rules
- No Cursor rules found (.cursor/rules or .cursorrules not present).
- No GitHub Copilot instructions found (.github/copilot-instructions.md not present).
