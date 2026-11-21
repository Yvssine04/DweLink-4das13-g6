# DweLink Microservices Architecture

Plateforme de gestion de santé connectée DweLink - Implémentation du backend Spring Boot en architecture microservices.

> 📘 **Guide détaillé des endpoints avec exemples JSON complets** : Consultez [API_ENDPOINTS_GUIDE.md](file:///c:/Users/funpa/DweLink-4das13-g6/Backend/API_ENDPOINTS_GUIDE.md) pour les exemples de requêtes et réponses JSON de chaque endpoint.

---

## 1. Vue d'ensemble de l'architecture

Le système backend DweLink repose sur une architecture orientée microservices moderne basée sur **Spring Boot 3 (v3.2.5)** et **Spring Cloud (2023.0.1)**.

```
                    +-------------------+
                    |   EUREKA SERVER   |
                    |    (Port 8761)    |
                    +---------+---------+
                              ^
             enregistre et    |
             découvre         |
          +--------------------+
          |
+--------+---------+
|   USER-SERVICE   |
|   (Port 8081)    |
+--------+---------+
         |
  PostgreSQL (5432)
   utilisateur_db
```

### Synthèse des composants

| Composant | Rôle | Port | Base de données | Découverte & Communication |
|---|---|---|---|---|
| **eureka-server** | Serveur d'enregistrement & découverte | 8761 | Aucune | Eureka Server |
| **user-service** | Gestion des profils, auth JWT, validation pharmaciens | 8081 | `utilisateur_db` (PostgreSQL) | Client Eureka |

---

## 2. Configuration des bases de données PostgreSQL

Chaque microservice dispose de sa propre base de données isolée.

### 2.1 Création des bases de données
Connectez-vous à votre instance PostgreSQL locale (par défaut sur le port 5432) :
```sql
CREATE DATABASE utilisateur_db;
```

### 2.2 Migrations Flyway
Les scripts de migration Flyway s'exécutent automatiquement au démarrage de chaque microservice :
- `user-service` : `src/main/resources/db/migration/V1__init_user_schema.sql` (Tables `profil_utilisateur`, `demande_pharmacien`, `justificatif_demande`).

---

## 3. Ordre et commandes de démarrage

Démarrez les services dans l'ordre suivant :

### Étape 1 : PostgreSQL
Assurez-vous que le service PostgreSQL est en cours d'exécution.

### Étape 2 : Eureka Server
```bash
cd Backend/eureka-server
mvn spring-boot:run
```
*Accédez au Dashboard Eureka :* [http://localhost:8761](http://localhost:8761)

### Étape 3 : User Service
```bash
cd Backend/user-service
mvn spring-boot:run
```
*Accédez à la documentation Swagger :* [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)

---

## 4. Documentation des API et Endpoints

### 4.1 user-service (`http://localhost:8081`)

| Méthode | URL | Rôle | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Inscription patient ou pharmacien (crée compte et JWT) |
| POST | `/api/auth/login` | Public | Authentification email/mot de passe (retourne JWT) |
| GET | `/api/utilisateurs/moi` | Authentifié | Profil de l'utilisateur connecté |
| PATCH | `/api/utilisateurs/moi` | Authentifié | Modification du profil et préférences |
| POST | `/api/demandes-pharmacien` | Authentifié | Dépôt d'une demande d'inscription pharmacien |
| POST | `/api/demandes-pharmacien/{id}/justificatifs` | Authentifié | Ajout d'un justificatif (DIPLOME, CARTE_ORDRE, PIECE_IDENTITE) |
| GET | `/api/demandes-pharmacien` | ADMIN | Consultation des demandes (filtre optionnel `?statut=`) |
| PATCH | `/api/demandes-pharmacien/{id}/decision` | ADMIN | Décision (APPROUVEE / REFUSEE) et activation du rôle pharmacien |
| PATCH | `/api/utilisateurs/{id}/statut` | ADMIN | Modification du statut (ACTIF, SUSPENDU, DESACTIVE) |
| GET | `/api/utilisateurs/{id}` | Interne | Statut, nom et rôle (utilisé par d'autres microservices via Feign) |
| GET | `/api/utilisateurs/{id}/contact` | Interne | Coordonnées et email |

---

## 5. Exemples JSON par Endpoint

Pour consulter l'ensemble des payloads de requêtes et de réponses avec des exemples détaillés (cas nominaux et erreurs), reportez-vous au fichier dédié :
👉 [API_ENDPOINTS_GUIDE.md](file:///c:/Users/funpa/DweLink-4das13-g6/Backend/API_ENDPOINTS_GUIDE.md)

---

## 6. Authentification et Sécurité JWT

- **Algorithme & Signature** : HMAC SHA-256 avec clé sécurisée.
- **En-tête HTTP** : `Authorization: Bearer <token>`
- **Utilisation dans Swagger UI** : Cliquez sur le bouton vert **Authorize** en haut à droite et saisissez votre token sous la forme `Bearer <token>`.

---

## 7. Exécution des tests

Pour exécuter la suite de tests automatisée (unitaires, mockito, validation d'intégration et Feign) :

```bash
cd Backend
mvn test
```
