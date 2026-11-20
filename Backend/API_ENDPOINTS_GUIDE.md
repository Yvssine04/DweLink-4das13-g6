# DweLink Microservices — API Reference & JSON Examples

Ce document présente l'architecture globale, la configuration, l'ordre d'exécution ainsi que les spécifications détaillées de chaque endpoint avec des exemples concrets de requêtes et de réponses JSON pour **user-service** et **pharmacy-service**.

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
4. [Microservice : pharmacy-service (Port 8082)](#4-microservice--pharmacy-service-port-8082)
   - [4.1 Pharmacie — Créer une officine](#41-pharmacie--créer-une-officine)
   - [4.2 Pharmacie — Consulter par ID](#42-pharmacie--consulter-par-id)
   - [4.3 Pharmacie — Modifier les détails](#43-pharmacie--modifier-les-détails)
   - [4.4 Pharmacie — Rechercher par ville et garde](#44-pharmacie--rechercher-par-ville-et-garde)
   - [4.5 Pharmacie — Trouver les pharmacies proches](#45-pharmacie--trouver-les-pharmacies-proches)
   - [4.6 Admin — Modifier le statut d'une pharmacie](#46-admin--modifier-le-statut-dune-pharmacie)
   - [4.7 Horaires — Mettre à jour les horaires d'ouverture](#47-horaires--mettre-à-jour-les-horaires-douverture)
   - [4.8 Gardes — Programmer une période de garde](#48-gardes--programmer-une-période-de-garde)
   - [4.9 Membres — Demander à rejoindre une pharmacie](#49-membres--demander-à-rejoindre-une-pharmacie)
   - [4.10 Membres — Décider d'une adhésion](#410-membres--décider-dune-adhésion)
   - [4.11 Membres — Retirer un membre](#411-membres--retirer-un-membre)
   - [4.12 Interne — Vérifier l'adhésion d'un membre](#412-interne--vérifier-ladhésion-dun-membre)
5. [Gestion des erreurs](#5-gestion-des-erreurs)

---

## 1. Vue d'ensemble de l'architecture

```
                        +--------------------+
                        |   EUREKA SERVER    |
                        |    (Port 8761)     |
                        +---------+----------+
                                  ^
             enregistre et        |       enregistre et
             découvre             |       découvre
         +------------------------+------------------------+
         |                                                 |
+--------+---------+                             +---------+--------+
|   USER-SERVICE   |<--------- OpenFeign --------| PHARMACY-SERVICE |
|   (Port 8081)    |    (Sync REST via Eureka)   |   (Port 8082)    |
+--------+---------+                             +---------+--------+
         |                                                 |
  PostgreSQL (5432)                                 PostgreSQL (5432)
   utilisateur_db                                     pharmacie_db
```

- **eureka-server** : Dashboard accessible sur `http://localhost:8761`.
- **user-service** : API OpenAPI / Swagger sur `http://localhost:8081/swagger-ui/index.html`.
- **pharmacy-service** : API OpenAPI / Swagger sur `http://localhost:8082/swagger-ui/index.html`.

---

## 2. Configuration et démarrage

### 2.1 Bases de données PostgreSQL
```sql
CREATE DATABASE utilisateur_db;
CREATE DATABASE pharmacie_db;
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
3. **pharmacy-service** :
   ```bash
   cd Backend/pharmacy-service && mvn spring-boot:run
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
- **Usage** : Invoqué de manière synchrone par `pharmacy-service` via OpenFeign.

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

## 4. Microservice : pharmacy-service (Port 8082)

### 4.1 Pharmacie — Créer une officine
- **Méthode / URL** : `POST /api/pharmacies`
- **Sécurité** : Rôle `PHARMACIEN` ou `ADMINISTRATEUR`
- **Logique inter-services** : Vérifie via OpenFeign `GET /api/utilisateurs/{titulaireId}` que l'utilisateur existe, possède le rôle `PHARMACIEN` et le statut `ACTIF`. Rétablit une erreur 503 si `user-service` est inaccessible.

#### Requête JSON
```json
{
  "titulaireId": 1,
  "nom": "Pharmacie Centrale de Tunis",
  "numeroAutorisation": "AUT-PH-2026-0012",
  "adresse": "12 Avenue Habib Bourguiba",
  "ville": "Tunis",
  "latitude": 36.8002,
  "longitude": 10.1865,
  "telephone": "+216 71 300 400"
}
```

#### Réponse JSON (201 Created)
```json
{
  "id": 5,
  "nom": "Pharmacie Centrale de Tunis",
  "numeroAutorisation": "AUT-PH-2026-0012",
  "adresse": "12 Avenue Habib Bourguiba",
  "ville": "Tunis",
  "latitude": 36.8002,
  "longitude": 10.1865,
  "telephone": "+216 71 300 400",
  "statut": "EN_ATTENTE",
  "dateCreation": "2026-11-20T21:30:00",
  "membres": [
    {
      "id": 50,
      "pharmacieId": 5,
      "utilisateurId": 1,
      "role": "TITULAIRE",
      "statut": "ACTIF",
      "dateCreation": "2026-11-20T21:30:00"
    }
  ],
  "horaires": [],
  "gardes": []
}
```

---

### 4.2 Pharmacie — Consulter par ID
- **Méthode / URL** : `GET /api/pharmacies/{id}`
- **Sécurité** : Public

#### Réponse JSON (200 OK)
```json
{
  "id": 5,
  "nom": "Pharmacie Centrale de Tunis",
  "numeroAutorisation": "AUT-PH-2026-0012",
  "adresse": "12 Avenue Habib Bourguiba",
  "ville": "Tunis",
  "latitude": 36.8002,
  "longitude": 10.1865,
  "telephone": "+216 71 300 400",
  "statut": "VALIDEE",
  "dateCreation": "2026-11-20T21:30:00",
  "membres": [
    {
      "id": 50,
      "pharmacieId": 5,
      "utilisateurId": 1,
      "role": "TITULAIRE",
      "statut": "ACTIF",
      "dateCreation": "2026-11-20T21:30:00"
    }
  ],
  "horaires": [
    {
      "id": 1,
      "jour": "LUNDI",
      "heureOuverture": "08:30:00",
      "heureFermeture": "19:30:00"
    }
  ],
  "gardes": [
    {
      "id": 12,
      "debut": "2026-11-21T20:00:00",
      "fin": "2026-11-22T08:00:00"
    }
  ]
}
```

---

### 4.3 Pharmacie — Modifier les détails
- **Méthode / URL** : `PUT /api/pharmacies/{id}`
- **Sécurité** : Authentifié (Pharmacien Titulaire)

#### Requête JSON
```json
{
  "nom": "Pharmacie Centrale de Tunis (Rénovée)",
  "adresse": "14 Avenue Habib Bourguiba",
  "ville": "Tunis",
  "latitude": 36.8002,
  "longitude": 10.1865,
  "telephone": "+216 71 300 444"
}
```

#### Réponse JSON (200 OK)
```json
{
  "id": 5,
  "nom": "Pharmacie Centrale de Tunis (Rénovée)",
  "numeroAutorisation": "AUT-PH-2026-0012",
  "adresse": "14 Avenue Habib Bourguiba",
  "ville": "Tunis",
  "latitude": 36.8002,
  "longitude": 10.1865,
  "telephone": "+216 71 300 444",
  "statut": "VALIDEE",
  "dateCreation": "2026-11-20T21:30:00",
  "membres": [],
  "horaires": [],
  "gardes": []
}
```

---

### 4.4 Pharmacie — Rechercher par ville et garde
- **Méthode / URL** : `GET /api/pharmacies?ville=Tunis&deGarde=true`
- **Sécurité** : Public

#### Réponse JSON (200 OK)
```json
[
  {
    "id": 5,
    "nom": "Pharmacie Centrale de Tunis",
    "numeroAutorisation": "AUT-PH-2026-0012",
    "adresse": "12 Avenue Habib Bourguiba",
    "ville": "Tunis",
    "latitude": 36.8002,
    "longitude": 10.1865,
    "telephone": "+216 71 300 400",
    "statut": "VALIDEE",
    "dateCreation": "2026-11-20T21:30:00",
    "membres": [],
    "horaires": [],
    "gardes": [
      {
        "id": 12,
        "debut": "2026-11-20T18:00:00",
        "fin": "2026-11-21T08:00:00"
      }
    ]
  }
]
```

---

### 4.5 Pharmacie — Trouver les pharmacies proches
- **Méthode / URL** : `GET /api/pharmacies/proches?lat=36.8000&lng=10.1800&rayonKm=5`
- **Sécurité** : Public

#### Réponse JSON (200 OK)
```json
[
  {
    "id": 5,
    "nom": "Pharmacie Centrale de Tunis",
    "numeroAutorisation": "AUT-PH-2026-0012",
    "adresse": "12 Avenue Habib Bourguiba",
    "ville": "Tunis",
    "latitude": 36.8002,
    "longitude": 10.1865,
    "telephone": "+216 71 300 400",
    "statut": "VALIDEE",
    "dateCreation": "2026-11-20T21:30:00",
    "membres": [],
    "horaires": [],
    "gardes": []
  }
]
```

---

### 4.6 Admin — Modifier le statut d'une pharmacie
- **Méthode / URL** : `PATCH /api/pharmacies/{id}/statut`
- **Sécurité** : Rôle `ADMINISTRATEUR`
- **Statuts** : `EN_ATTENTE`, `VALIDEE`, `SUSPENDUE`

#### Requête JSON
```json
{
  "statut": "VALIDEE"
}
```

#### Réponse JSON (200 OK)
```json
{
  "id": 5,
  "nom": "Pharmacie Centrale de Tunis",
  "numeroAutorisation": "AUT-PH-2026-0012",
  "adresse": "12 Avenue Habib Bourguiba",
  "ville": "Tunis",
  "latitude": 36.8002,
  "longitude": 10.1865,
  "telephone": "+216 71 300 400",
  "statut": "VALIDEE",
  "dateCreation": "2026-11-20T21:30:00",
  "membres": [],
  "horaires": [],
  "gardes": []
}
```

---

### 4.7 Horaires — Mettre à jour les horaires d'ouverture
- **Méthode / URL** : `PUT /api/pharmacies/{id}/horaires`
- **Sécurité** : Authentifié (Pharmacien Titulaire)

#### Requête JSON
```json
[
  {
    "jour": "LUNDI",
    "heureOuverture": "08:30:00",
    "heureFermeture": "19:30:00"
  },
  {
    "jour": "MARDI",
    "heureOuverture": "08:30:00",
    "heureFermeture": "19:30:00"
  },
  {
    "jour": "SAMEDI",
    "heureOuverture": "09:00:00",
    "heureFermeture": "14:00:00"
  }
]
```

#### Réponse JSON (200 OK)
```json
[
  {
    "id": 1,
    "jour": "LUNDI",
    "heureOuverture": "08:30:00",
    "heureFermeture": "19:30:00"
  },
  {
    "id": 2,
    "jour": "MARDI",
    "heureOuverture": "08:30:00",
    "heureFermeture": "19:30:00"
  },
  {
    "id": 3,
    "jour": "SAMEDI",
    "heureOuverture": "09:00:00",
    "heureFermeture": "14:00:00"
  }
]
```

---

### 4.8 Gardes — Programmer une période de garde
- **Méthode / URL** : `POST /api/pharmacies/{id}/gardes`
- **Sécurité** : Authentifié (Pharmacien Titulaire)

#### Requête JSON
```json
{
  "debut": "2026-11-28T20:00:00",
  "fin": "2026-11-29T08:00:00"
}
```

#### Réponse JSON (201 Created)
```json
{
  "id": 15,
  "debut": "2026-11-28T20:00:00",
  "fin": "2026-11-29T08:00:00"
}
```

---

### 4.9 Membres — Demander à rejoindre une pharmacie
- **Méthode / URL** : `POST /api/pharmacies/{id}/membres`
- **Sécurité** : Rôle `PHARMACIEN`
- **Logique inter-services** : Invoque `user-service` pour valider que l'utilisateur est bien un pharmacien actif homologué.

#### Requête JSON
```json
{
  "utilisateurId": 2,
  "role": "ASSISTANT"
}
```

#### Réponse JSON (201 Created)
```json
{
  "id": 51,
  "pharmacieId": 5,
  "utilisateurId": 2,
  "role": "ASSISTANT",
  "statut": "EN_ATTENTE",
  "dateCreation": "2026-11-20T21:40:00"
}
```

---

### 4.10 Membres — Décider d'une adhésion
- **Méthode / URL** : `PATCH /api/pharmacies/{id}/membres/{membreId}`
- **Sécurité** : Authentifié (Pharmacien Titulaire)
- **Décisions** : `ACTIF`, `REFUSE`

#### Requête JSON
```json
{
  "decision": "ACTIF"
}
```

#### Réponse JSON (200 OK)
```json
{
  "id": 51,
  "pharmacieId": 5,
  "utilisateurId": 2,
  "role": "ASSISTANT",
  "statut": "ACTIF",
  "dateCreation": "2026-11-20T21:40:00"
}
```

---

### 4.11 Membres — Retirer un membre
- **Méthode / URL** : `DELETE /api/pharmacies/{id}/membres/{membreId}`
- **Sécurité** : Authentifié (Pharmacien Titulaire)
- **Réponse HTTP** : `204 No Content` (corps vide)

---

### 4.12 Interne — Vérifier l'adhésion d'un membre
- **Méthode / URL** : `GET /api/pharmacies/{id}/membres/verifier?utilisateurId=2`
- **Usage** : Invoqué par d'autres microservices (ex. stock, réservations) pour valider qu'un pharmacien a le droit d'opérer sur cette officine.

#### Réponse JSON (200 OK)
```json
{
  "pharmacieId": 5,
  "utilisateurId": 2,
  "estMembreActif": true,
  "role": "ASSISTANT"
}
```

---

## 5. Gestion des erreurs

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
*Retourné par `pharmacy-service` si `user-service` est inaccessible lors d'une vérification de pharmacien :*
```json
{
  "timestamp": "2026-11-20T21:45:40.777888",
  "status": 503,
  "error": "Le service utilisateur (user-service) est actuellement indisponible: [503] during [GET] to [http://USER-SERVICE/api/utilisateurs/1]"
}
```
