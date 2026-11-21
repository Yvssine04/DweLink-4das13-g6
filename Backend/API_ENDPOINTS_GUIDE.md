# DweLink Microservices — API Reference & JSON Examples

Ce document présente l'architecture globale, la configuration, l'ordre d'exécution ainsi que les spécifications détaillées de chaque endpoint avec des exemples concrets de requêtes et de réponses JSON pour **user-service**.

---

## Sommaire

1. [Vue d'ensemble de l'architecture](#1-vue-densemble-de-larchitecture)
2. [Configuration et démarrage](#2-configuration-et-démarrage)
3. [Microservice : user-service (Port 8081)](#3-microservice--user-service-port-8081)
   - [3.1 Authentification — Inscription](#31-authentification--inscription)
   - [3.2 Authentification — Connexion](#32-authentification--connexion)
   - [3.3 Profil — Consulter mon profil](#33-profil--consulter-mon-profil)
   - [3.4 Profil — Modifier mon profil](#34-profil--modifier-mon-profil)
   - [3.5 Pharmacien — Déposer une demande d'inscription](#35-pharmacien--déposer-une-demande-dinscription)
   - [3.6 Pharmacien — Ajouter un justificatif](#36-pharmacien--ajouter-un-justificatif)
   - [3.7 Admin — Lister les demandes d'inscription](#37-admin--lister-les-demandes-dinscription)
   - [3.8 Admin — Décider d'une demande d'inscription](#38-admin--décider-dune-demande-dinscription)
   - [3.9 Admin — Modifier le statut d'un compte](#39-admin--modifier-le-statut-dun-compte)
   - [3.10 Interne — Consulter statut et rôle utilisateur](#310-interne--consulter-statut-et-rôle-utilisateur)
   - [3.11 Interne — Coordonnées de contact](#311-interne--coordonnées-de-contact)
4. [Gestion des erreurs](#4-gestion-des-erreurs)

---

## 1. Vue d'ensemble de l'architecture

```
                        +--------------------+
                        |   EUREKA SERVER    |
                        |    (Port 8761)     |
                        +---------+----------+
                                  ^
             enregistre et        |
             découvre             |
          +-----------------------+
          |
+--------+---------+
|   USER-SERVICE   |
|   (Port 8081)    |
+--------+---------+
         |
  PostgreSQL (5432)
   utilisateur_db
```

- **eureka-server** : Dashboard accessible sur `http://localhost:8761`.
- **user-service** : API OpenAPI / Swagger sur `http://localhost:8081/swagger-ui/index.html`.

---

## 2. Configuration et démarrage

### 2.1 Bases de données PostgreSQL
```sql
CREATE DATABASE utilisateur_db;
```

### 2.2 Ordre de démarrage
1. **eureka-server** :
   ```bash
   cd Backend/eureka-server && mvn spring-boot:run
   ```
2. **user-service** :
   ```bash
   cd Backend/user-service && mvn spring-boot:run
   ```

---

## 3. Microservice : user-service (Port 8081)

### 3.1 Authentification — Inscription
- **Méthode / URL** : `POST /api/auth/register`
- **Sécurité** : Public
- **Rôles acceptés** : `PATIENT` (statut immédiat `ACTIF`), `PHARMACIEN` (statut `EN_ATTENTE_VALIDATION`)

#### Requête JSON
```json
{
  "email": "dr.house@dwelink.com",
  "motDePasse": "SecurePassword123!",
  "prenom": "Gregory",
  "nom": "House",
  "telephone": "+216 71 123 456",
  "role": "PHARMACIEN"
}
```

#### Réponse JSON (201 Created)
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "email": "dr.house@dwelink.com",
  "prenom": "Gregory",
  "nom": "House",
  "role": "PHARMACIEN",
  "statut": "EN_ATTENTE_VALIDATION"
}
```

---

### 3.2 Authentification — Connexion
- **Méthode / URL** : `POST /api/auth/login`
- **Sécurité** : Public

#### Requête JSON
```json
{
  "email": "dr.house@dwelink.com",
  "motDePasse": "SecurePassword123!"
}
```

#### Réponse JSON (200 OK)
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "email": "dr.house@dwelink.com",
  "prenom": "Gregory",
  "nom": "House",
  "role": "PHARMACIEN",
  "statut": "EN_ATTENTE_VALIDATION"
}
```

---

### 3.3 Profil — Consulter mon profil
- **Méthode / URL** : `GET /api/utilisateurs/moi`
- **Sécurité** : Authentifié (`Authorization: Bearer <token>`)

#### Réponse JSON (200 OK)
```json
{
  "id": 1,
  "email": "dr.house@dwelink.com",
  "prenom": "Gregory",
  "nom": "House",
  "telephone": "+216 71 123 456",
  "role": "PHARMACIEN",
  "statut": "EN_ATTENTE_VALIDATION",
  "langue": "fr",
  "rayonRechercheKm": 10,
  "dateCreation": "2026-11-20T21:00:00"
}
```

---

### 3.4 Profil — Modifier mon profil
- **Méthode / URL** : `PATCH /api/utilisateurs/moi`
- **Sécurité** : Authentifié (`Authorization: Bearer <token>`)

#### Requête JSON
```json
{
  "prenom": "Gregory",
  "nom": "House",
  "telephone": "+216 71 999 888",
  "langue": "en",
  "rayonRechercheKm": 25
}
```

#### Réponse JSON (200 OK)
```json
{
  "id": 1,
  "email": "dr.house@dwelink.com",
  "prenom": "Gregory",
  "nom": "House",
  "telephone": "+216 71 999 888",
  "role": "PHARMACIEN",
  "statut": "EN_ATTENTE_VALIDATION",
  "langue": "en",
  "rayonRechercheKm": 25,
  "dateCreation": "2026-11-20T21:00:00"
}
```

---

### 3.5 Pharmacien — Déposer une demande d'inscription
- **Méthode / URL** : `POST /api/demandes-pharmacien`
- **Sécurité** : Authentifié (`Authorization: Bearer <token>`)

#### Requête JSON
```json
{
  "numeroOrdre": "ORDRE-PHARMA-78945",
  "diplome": "Doctorat d'État en Pharmacie"
}
```

#### Réponse JSON (201 Created)
```json
{
  "id": 10,
  "utilisateurId": 1,
  "emailUtilisateur": "dr.house@dwelink.com",
  "nomCompletUtilisateur": "Gregory House",
  "numeroOrdre": "ORDRE-PHARMA-78945",
  "diplome": "Doctorat d'État en Pharmacie",
  "statut": "DEPOSEE",
  "dateDepot": "2026-11-20T21:15:00",
  "dateDecision": null,
  "examineePar": null,
  "commentaireDecision": null,
  "justificatifs": []
}
```

---

### 3.6 Pharmacien — Ajouter un justificatif
- **Méthode / URL** : `POST /api/demandes-pharmacien/{id}/justificatifs`
- **Sécurité** : Authentifié (Propriétaire de la demande)
- **Types valides** : `DIPLOME`, `CARTE_ORDRE`, `PIECE_IDENTITE`

#### Requête JSON
```json
{
  "type": "CARTE_ORDRE",
  "nomFichier": "carte_ordre_professionnel_2026.pdf"
}
```

#### Réponse JSON (201 Created)
```json
{
  "id": 101,
  "type": "CARTE_ORDRE",
  "nomFichier": "carte_ordre_professionnel_2026.pdf",
  "dateAjout": "2026-11-20T21:20:00"
}
```

---

### 3.7 Admin — Lister les demandes d'inscription
- **Méthode / URL** : `GET /api/demandes-pharmacien` (Filtre optionnel : `?statut=DEPOSEE`)
- **Sécurité** : Rôle `ADMINISTRATEUR`

#### Réponse JSON (200 OK)
```json
[
  {
    "id": 10,
    "utilisateurId": 1,
    "emailUtilisateur": "dr.house@dwelink.com",
    "nomCompletUtilisateur": "Gregory House",
    "numeroOrdre": "ORDRE-PHARMA-78945",
    "diplome": "Doctorat d'État en Pharmacie",
    "statut": "DEPOSEE",
    "dateDepot": "2026-11-20T21:15:00",
    "dateDecision": null,
    "examineePar": null,
    "commentaireDecision": null,
    "justificatifs": [
      {
        "id": 101,
        "type": "CARTE_ORDRE",
        "nomFichier": "carte_ordre_professionnel_2026.pdf",
        "dateAjout": "2026-11-20T21:20:00"
      }
    ]
  }
]
```

---

### 3.8 Admin — Décider d'une demande d'inscription
- **Méthode / URL** : `PATCH /api/demandes-pharmacien/{id}/decision`
- **Sécurité** : Rôle `ADMINISTRATEUR`
- **Décisions** : `APPROUVEE` (passe le compte en rôle `PHARMACIEN` et statut `ACTIF`), `REFUSEE`

#### Requête JSON
```json
{
  "decision": "APPROUVEE",
  "commentaire": "Dossier et carte de l'Ordre vérifiés et conformes."
}
```

#### Réponse JSON (200 OK)
```json
{
  "id": 10,
  "utilisateurId": 1,
  "emailUtilisateur": "dr.house@dwelink.com",
  "nomCompletUtilisateur": "Gregory House",
  "numeroOrdre": "ORDRE-PHARMA-78945",
  "diplome": "Doctorat d'État en Pharmacie",
  "statut": "APPROUVEE",
  "dateDepot": "2026-11-20T21:15:00",
  "dateDecision": "2026-11-20T21:25:00",
  "examineePar": 99,
  "commentaireDecision": "Dossier et carte de l'Ordre vérifiés et conformes.",
  "justificatifs": [
    {
      "id": 101,
      "type": "CARTE_ORDRE",
      "nomFichier": "carte_ordre_professionnel_2026.pdf",
      "dateAjout": "2026-11-20T21:20:00"
    }
  ]
}
```

---

### 3.9 Admin — Modifier le statut d'un compte
- **Méthode / URL** : `PATCH /api/utilisateurs/{id}/statut`
- **Sécurité** : Rôle `ADMINISTRATEUR`
- **Statuts** : `ACTIF`, `SUSPENDU`, `DESACTIVE`

#### Requête JSON
```json
{
  "statut": "SUSPENDU"
}
```

#### Réponse JSON (200 OK)
```json
{
  "id": 1,
  "email": "dr.house@dwelink.com",
  "prenom": "Gregory",
  "nom": "House",
  "telephone": "+216 71 999 888",
  "role": "PHARMACIEN",
  "statut": "SUSPENDU",
  "langue": "en",
  "rayonRechercheKm": 25,
  "dateCreation": "2026-11-20T21:00:00"
}
```

---

### 3.10 Interne — Consulter statut et rôle utilisateur
- **Méthode / URL** : `GET /api/utilisateurs/{id}`
- **Usage** : Invoqué de manière synchrone par les autres microservices via OpenFeign.

#### Réponse JSON (200 OK)
```json
{
  "id": 1,
  "email": "dr.house@dwelink.com",
  "prenom": "Gregory",
  "nom": "House",
  "role": "PHARMACIEN",
  "statut": "ACTIF"
}
```

---

### 3.11 Interne — Coordonnées de contact
- **Méthode / URL** : `GET /api/utilisateurs/{id}/contact`
- **Usage** : Notifications et vérifications.

#### Réponse JSON (200 OK)
```json
{
  "id": 1,
  "email": "dr.house@dwelink.com",
  "prenom": "Gregory",
  "nom": "House",
  "telephone": "+216 71 999 888"
}
```

---

## 4. Gestion des erreurs

Les microservices utilisent un format d'erreur JSON standard et prévisible via `@RestControllerAdvice`.

### Exemple : Erreur de validation (400 Bad Request)
```json
{
  "timestamp": "2026-11-20T21:45:00.123456",
  "status": 400,
  "errors": {
    "email": "Format d'email invalide",
    "motDePasse": "Le mot de passe doit comporter au moins 6 caractères"
  }
}
```

### Exemple : Conflit de ressource (409 Conflict)
```json
{
  "timestamp": "2026-11-20T21:45:10.654321",
  "status": 409,
  "error": "Un compte existe déjà avec cet email"
}
```

### Exemple : Non autorisé / Non authentifié (401 Unauthorized)
```json
{
  "timestamp": "2026-11-20T21:45:20.111222",
  "status": 401,
  "error": "Email ou mot de passe incorrect"
}
```

### Exemple : Accès refusé (403 Forbidden)
```json
{
  "timestamp": "2026-11-20T21:45:30.333444",
  "status": 403,
  "error": "Accès refusé"
}
```

### Exemple : Défaillance inter-services OpenFeign (503 Service Unavailable)
*Retourné en cas de défaillance inter-services OpenFeign :*
```json
{
  "timestamp": "2026-11-20T21:45:40.777888",
  "status": 503,
  "error": "Le service utilisateur (user-service) est actuellement indisponible: [503] during [GET] to [http://USER-SERVICE/api/utilisateurs/1]"
}
```
