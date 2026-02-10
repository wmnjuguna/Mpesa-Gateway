## [1.4.1](https://github.com/wmnjuguna/Mpesa-Gateway/compare/v1.4.0...v1.4.1) (2026-02-10)


### Bug Fixes

* **openapi:** enforce single tag per operation ([caee943](https://github.com/wmnjuguna/Mpesa-Gateway/commit/caee943c0ab3cc3ea54d52b978a36c6a0db673ed))

# [1.4.0](https://github.com/wmnjuguna/Mpesa-Gateway/compare/v1.3.1...v1.4.0) (2026-02-10)


### Features

* **security:** enforce audience and enable OIDC-only swagger auth ([d66a1d8](https://github.com/wmnjuguna/Mpesa-Gateway/commit/d66a1d83b436c7503fad99fd1c7b0c04da8bbbce))

## [1.3.1](https://github.com/wmnjuguna/Mpesa-Gateway/compare/v1.3.0...v1.3.1) (2026-02-10)


### Bug Fixes

* add Boot 4 restclient starter and client timeouts ([0493dd1](https://github.com/wmnjuguna/Mpesa-Gateway/commit/0493dd1a3894fdcd34abdd9e5e969bf853b5fcf7))

# [1.3.0](https://github.com/wmnjuguna/Mpesa-Gateway/compare/v1.2.3...v1.3.0) (2026-02-10)


### Bug Fixes

* update app bootstrap http services import ([f6c1adf](https://github.com/wmnjuguna/Mpesa-Gateway/commit/f6c1adf5e9310085b2ffc7869a1496faa8a8d107))


### Features

* configure keycloak oauth2 resource server ([2271a43](https://github.com/wmnjuguna/Mpesa-Gateway/commit/2271a43febc9f3ec418990b77ef1c326774d5787))

## [1.2.3](https://github.com/wmnjuguna/Mpesa-Gateway/compare/v1.2.2...v1.2.3) (2026-02-10)


### Bug Fixes

* enable flyway migration startup and tag docker from semantic tags ([702f3bd](https://github.com/wmnjuguna/Mpesa-Gateway/commit/702f3bd8174a2b615b8287db0dee9073d4090391))

## [1.2.2](https://github.com/wmnjuguna/Mpesa-Gateway/compare/v1.2.1...v1.2.2) (2026-02-10)


### Bug Fixes

* use spring-boot-starter-flyway for boot 4 ([229d974](https://github.com/wmnjuguna/Mpesa-Gateway/commit/229d97444fab454d1ac54710f280105d5fb4401e))

## [1.2.1](https://github.com/wmnjuguna/Mpesa-Gateway/compare/v1.2.0...v1.2.1) (2026-01-19)


### Bug Fixes

* stabilize json mappings for tests ([46f7987](https://github.com/wmnjuguna/Mpesa-Gateway/commit/46f7987165f2988c854beb56579a1fd3e36edd11))

# [1.2.0](https://github.com/wmnjuguna/Mpesa-Gateway/compare/v1.1.1...v1.2.0) (2025-09-21)


### Features

* add manual triggers to build and deploy workflows ([79a34c7](https://github.com/wmnjuguna/Mpesa-Gateway/commit/79a34c7dd28ec14cfcc9bbb69f617735cb18f0be))

## [1.1.1](https://github.com/wmnjuguna/Mpesa-Gateway/compare/v1.1.0...v1.1.1) (2025-09-21)


### Bug Fixes

* ensure deploy workflow runs only after successful build ([1b70f5b](https://github.com/wmnjuguna/Mpesa-Gateway/commit/1b70f5bcda2d2e9e7bb6eed8f59ba8d652fec10e))
* ensure release workflow runs only after successful build ([ac31c8d](https://github.com/wmnjuguna/Mpesa-Gateway/commit/ac31c8d3b9d8b80cde34a2413e0f01f36edbeef8))

# [1.1.0](https://github.com/wmnjuguna/Mpesa-Gateway/compare/v1.0.0...v1.1.0) (2025-09-21)


### Features

* implement comprehensive Swagger/OpenAPI documentation ([ca56591](https://github.com/wmnjuguna/Mpesa-Gateway/commit/ca565917fb0754374aba4094136e09e5af0e5bb2))

# 1.0.0 (2025-09-21)


### Bug Fixes

* remove npm audit signatures from release workflow ([ff414c6](https://github.com/wmnjuguna/Mpesa-Gateway/commit/ff414c6e175644dd75edede543065d3e1f85a17c))


### Features

* implement CI/CD pipeline with semantic release and Docker deployment ([059d1b0](https://github.com/wmnjuguna/Mpesa-Gateway/commit/059d1b0d4bcf55fe81c59093a3a8ecab874e41b6))
* migrate from MariaDB to PostgreSQL 17 with Flyway migrations ([f24b7a2](https://github.com/wmnjuguna/Mpesa-Gateway/commit/f24b7a2606d1caef4615930bef64d17997f1ca0c))
* upgrade to Spring Boot 3.5.5 and Java 21, remove messaging dependencies ([df9e6a1](https://github.com/wmnjuguna/Mpesa-Gateway/commit/df9e6a16be9af3a4e2def62c93bd0e77327a34df))
