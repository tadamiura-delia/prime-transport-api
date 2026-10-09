# prime-transport-api

API REST de l'application « prime de transport » (front : `tuto_angular_dsfr`).
Elle persiste la configuration de chaque utilisateur : distances par lieu et semaine type.

Stack : Java 25, Spring Boot 4, Maven, PostgreSQL 17, Flyway.

## Prérequis

- Un JDK 25. Sans droits root, on peut le décompresser dans `~/.jdks` :

  ```bash
  mkdir -p ~/.jdks && curl -sSL "https://api.adoptium.net/v3/binary/latest/25/ga/linux/x64/jdk/hotspot/normal/eclipse" | tar xz -C ~/.jdks
  export JAVA_HOME=$(echo ~/.jdks/jdk-25*) PATH="$(echo ~/.jdks/jdk-25*)/bin:$PATH"
  ```

- Docker, pour la base de données et les tests (Testcontainers).

Maven n'a pas besoin d'être installé : le wrapper `./mvnw` le télécharge.

## Lancer

```bash
docker compose up -d   # Postgres sur le port 5432
./mvnw spring-boot:run # API sur http://localhost:8080
```

- Swagger UI : http://localhost:8080/swagger-ui/index.html
- Santé : http://localhost:8080/actuator/health

## Tester

```bash
./mvnw verify
```

Les tests d'intégration démarrent leur propre Postgres jetable via Testcontainers.

## Organisation du code

Découpage par fonctionnalité, sous `fr.delia.primetransport` :

| Package | Contenu |
| --- | --- |
| `config` | Configuration transverse (sécurité) |
| `referentiel` | Lieux de travail et moyens de transport |

Chaque fonctionnalité suit le même découpage en couches :
`Controller` (HTTP) → `Service` (règles métier, transactions) → `Repository` (accès base) → `Entity` (table).
Les `Dto` (records Java) sont ce qui transite en JSON ; les entités JPA ne sortent jamais du service.

Le schéma de base est versionné dans `src/main/resources/db/migration` (Flyway).
Une migration déjà appliquée ne se modifie jamais : on en ajoute une nouvelle.
