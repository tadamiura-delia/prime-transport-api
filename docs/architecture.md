# Architecture

Ce document décrit l'état **actuel** du backend. Il est mis à jour à chaque étape :
un élément en pointillés est prévu mais pas encore réalisé.

> Les schémas sont écrits en [Mermaid](https://mermaid.js.org/). Ils s'affichent
> directement sur GitLab/GitHub et dans l'aperçu Markdown de VS Code
> (`Ctrl+Shift+V`, avec l'extension « Markdown Preview Mermaid Support »).

## Vue d'ensemble

```mermaid
flowchart LR
    navigateur["Navigateur<br/>Front Angular<br/>:4200"]
    keycloak["Keycloak<br/>:8180"]

    subgraph api["API Spring Boot :8080"]
        direction TB
        controller["Controllers<br/>(HTTP / JSON)"]
        service["Services<br/>(règles métier, transactions)"]
        repository["Repositories<br/>(Spring Data JPA)"]
        controller --> service --> repository
    end

    subgraph docker["Docker Compose"]
        postgres[("PostgreSQL 17<br/>:5432<br/>base app")]
        keycloakdb[("base keycloak")]
    end

    navigateur -- "REST /api/**" --> controller
    repository -- "JDBC / SQL" --> postgres
    navigateur -. "connexion (OIDC + PKCE)" .-> keycloak
    controller -. "vérifie le JWT" .-> keycloak
    keycloak -.-> keycloakdb

    classDef prevu stroke-dasharray: 5 5,color:#888
    class keycloak,keycloakdb prevu
```

| Élément | Rôle | État |
| --- | --- | --- |
| Front Angular | Interface utilisateur (dépôt `tuto_angular_dsfr`) | Existant, pas encore branché sur l'API |
| API Spring Boot | Expose la configuration en REST et applique les règles métier | Étapes 1 et 2 faites |
| PostgreSQL | Stocke référentiels et configuration ; schéma géré par Flyway | Étapes 1 et 2 faites |
| Keycloak | Authentification, rôles `user` et `admin` | Prévu (étape 3) |

## Découpage du code

Un package par fonctionnalité, et dans chacun le même empilement de couches.
Une requête traverse les couches de haut en bas, la réponse remonte sous forme de DTO.

```mermaid
flowchart TB
    subgraph referentiel["package referentiel"]
        direction TB
        rc["ReferentielController<br/>GET /api/referentiels/*"]
        rs["ReferentielService"]
        rr["LieuTravailRepository<br/>MoyenTransportRepository"]
        re["LieuTravail, MoyenTransport<br/>(entités JPA)"]
        rd["LieuTravailDto, MoyenTransportDto<br/>(records JSON)"]
        rc --> rs --> rr --> re
        rs -- "convertit en" --> rd
    end

    subgraph configuration["package configuration"]
        direction TB
        cc["DistanceLieuController<br/>/api/me/distances<br/>SemaineTypeController<br/>/api/me/semaine-type"]
        cs["DistanceLieuService<br/>SemaineTypeService"]
        cr["DistanceLieuRepository<br/>JourTypeRepository"]
        ce["DistanceLieu, JourType<br/>(entités JPA)"]
        cin["DistanceLieuRequest, SemaineTypeRequest,<br/>JourTypeRequest (records validés)"]
        cd["DistanceLieuDto, JourTypeDto<br/>(records JSON)"]
        cc --> cs --> cr --> ce
        cin -- "reçu par" --> cc
        cs -- "convertit en" --> cd
    end

    cs -- "retrouve lieux et transports" --> rr

    subgraph config["package config"]
        sec["SecurityConfig<br/>(tout ouvert pour l'instant)"]
    end
```

## API

Toutes les routes `/api/me/...` concernent l'utilisateur connecté. En attendant
Keycloak (étape 3), les controllers utilisent un utilisateur provisoire, `alice`.

| Méthode | Route | Effet | Réponses |
| --- | --- | --- | --- |
| GET | `/api/referentiels/lieux` | Les 6 lieux, triés | 200 |
| GET | `/api/referentiels/transports` | Les transports, triés | 200 |
| GET | `/api/me/distances` | Tous les lieux avec leur distance, 0 si jamais saisie | 200 |
| PUT | `/api/me/distances/{lieuCode}` | Crée ou modifie la distance d'un lieu | 200, 400 si invalide |
| GET | `/api/me/semaine-type` | Les 7 jours, du dimanche (0) au samedi (6) | 200, 404 si jamais enregistrée |
| PUT | `/api/me/semaine-type` | Remplace la semaine ; samedi et dimanche forcés à `conges-ferie` sans transport | 200, 400 si invalide |

Erreurs encore provisoires (traitées à l'étape 4) : un lieu ou un transport
inexistant, et un jour présent deux fois, répondent 500.

## Base de données

Schéma de la base `app`, tel que créé par les migrations Flyway
(`src/main/resources/db/migration`).

```mermaid
erDiagram
    lieu_travail {
        varchar code PK "ex. teletravail"
        varchar libelle
        int ordre "position dans les listes"
        boolean systeme "true = non supprimable"
    }

    moyen_transport {
        varchar code PK "ex. velo"
        varchar libelle
        int ordre
    }

    distance_lieu {
        bigint id PK "généré (identity)"
        varchar utilisateur_id UK "sub Keycloak ; unique avec lieu_code"
        varchar lieu_code FK,UK "unique avec utilisateur_id"
        numeric distance_km "5,2 ; >= 0"
    }

    jour_type {
        bigint id PK "généré (identity)"
        varchar utilisateur_id UK "sub Keycloak ; unique avec jour"
        smallint jour UK "0 = dimanche ... 6 = samedi"
        varchar lieu_code FK "obligatoire"
        varchar transport_code FK "facultatif : null = aucun"
    }

    lieu_travail ||--o{ distance_lieu : "a pour distances"
    lieu_travail ||--o{ jour_type : "lieu du jour"
    moyen_transport |o--o{ jour_type : "transport du jour"
```

| Migration | Contenu |
| --- | --- |
| `V1__referentiels.sql` | Tables `lieu_travail` et `moyen_transport`, avec les 6 lieux et les 2 transports initiaux |
| `V2__distance_lieu.sql` | Table `distance_lieu` : une distance par utilisateur et par lieu |
| `V3__jour_type.sql` | Table `jour_type` : un lieu et un transport facultatif par utilisateur et par jour |

Points à noter :

- `utilisateur_id` n'est pas une clé étrangère : les utilisateurs vivent dans Keycloak, dans une autre base.
- Les clés étrangères vers `lieu_travail` n'ont pas de `ON DELETE CASCADE`. Postgres refuse donc de supprimer un lieu qui a encore des distances ou des jours type.
- Dans `jour_type`, `transport_code` accepte `NULL` : « aucun transport » n'est pas une ligne de `moyen_transport`.
- `jour` suit la convention de `Date.getDay()` côté front : 0 = dimanche, 6 = samedi.

## Tenir ce document à jour

À chaque étape :

1. **Nouvelle migration Flyway** : ajouter les tables dans le schéma `erDiagram`, avec leurs relations, et une ligne dans le tableau des migrations.
2. **Nouveau package ou composant** : l'ajouter au découpage du code.
3. **Élément réalisé** : retirer la classe `prevu` pour qu'il passe en trait plein, et mettre à jour la colonne « État ».
