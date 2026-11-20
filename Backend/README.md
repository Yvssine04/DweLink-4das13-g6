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
             enregistre et    |     enregistre et
             découvre         |     découvre
         +--------------------+--------------------+
         |                                         |
+--------+---------+                     +---------+--------+
|   USER-SERVICE   |<--- OpenFeign ------| PHARMACY-SERVICE |
|   (Port 8081)    |   (Sync REST via    |   (Port 8082)    |
|                  |    Eureka discovery)|                  |
+--------+---------+                     +---------+--------+
         |                                         |
  PostgreSQL (5432)                         PostgreSQL (5432)
   utilisateur_db                             pharmacie_db
```

### Synthèse des composants

| Composant | Rôle | Port | Base de données | Découverte & Communication |
|---|---|---|---|---|
| **eureka-server** | Serveur d'enregistrement & découverte | 8761 | Aucune | Eureka Server |
| **user-service** | Gestion des profils, auth JWT, validation pharmaciens | 8081 | `utilisateur_db` (PostgreSQL) | Client Eureka |
| **pharmacy-service** | Officines, membres, vérification via user-service | 8082 | `pharmacie_db` (PostgreSQL) | Client Eureka + OpenFeign + LoadBalancer |

---

## 2. Configuration des bases de données PostgreSQL

Chaque microservice dispose de sa propre base de données isolée.

### 2.1 Création des bases de données
Connectez-vous à votre instance PostgreSQL locale (par défaut sur le port 5432) :
```sql
CREATE DATABASE utilisateur_db;
CREATE DATABASE pharmacie_db;
```

### 2.2 Migrations Flyway
Les scripts de migration Flyway s'exécutent automatiquement au démarrage de chaque microservice :
- `user-service` : `src/main/resources/db/migration/V1__init_user_schema.sql` (Tables `profil_utilisateur`, `demande_pharmacien`, `justificatif_demande`).
- `pharmacy-service` : `src/main/resources/db/migration/V1__init_pharmacy_schema.sql` (Tables `pharmacie`, `membre_pharmacie`, `horaire`, `garde`).

---

## 3. Ordre et commandes de démarrage

Démarrez les services dans l'ordre strict suivant :

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

### Étape 4 : Pharmacy Service
```bash
cd Backend/pharmacy-service
mvn spring-boot:run
```
*Accédez à la documentation Swagger :* [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html)

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
| GET | `/api/utilisateurs/{id}` | Interne | Statut, nom et rôle (utilisé par Feign dans `pharmacy-service`) |
| GET | `/api/utilisateurs/{id}/contact` | Interne | Coordonnées et email |

### 4.2 pharmacy-service (`http://localhost:8082`)

| Méthode | URL | Rôle | Description |
|---|---|---|---|
| POST | `/api/pharmacies` | PHARMACIEN | Création d'une pharmacie (valide le titulaire via OpenFeign) |
| GET | `/api/pharmacies/{id}` | Public | Détails d'une pharmacie (horaires, gardes, membres) |
| PUT | `/api/pharmacies/{id}` | TITULAIRE | Modification des coordonnées d'une pharmacie |
| GET | `/api/pharmacies` | Public | Liste et recherche de pharmacies (`?ville=&deGarde=`) |
| GET | `/api/pharmacies/proches` | Public | Recherche géolocalisée (`?lat=&lng=&rayonKm=`) |
| PATCH | `/api/pharmacies/{id}/statut` | ADMIN | Validation ou suspension d'une pharmacie |
| PUT | `/api/pharmacies/{id}/horaires` | TITULAIRE | Configuration des horaires d'ouverture |
| POST | `/api/pharmacies/{id}/gardes` | TITULAIRE | Programmation d'une période de garde |
| POST | `/api/pharmacies/{id}/membres` | PHARMACIEN | Demande de rattachement à une pharmacie (vérifie via Feign) |
| PATCH | `/api/pharmacies/{id}/membres/{membreId}` | TITULAIRE | Validation/Refus d'un membre |
| DELETE | `/api/pharmacies/{id}/membres/{membreId}` | TITULAIRE | Suppression d'un membre |
| GET | `/api/pharmacies/{id}/membres/verifier` | Interne | Vérification de membre actif pour réservations/stocks |

---

## 5. Exemples JSON par Endpoint

Pour consulter l'ensemble des payloads de requêtes et de réponses avec des exemples détaillés (cas nominaux et erreurs), reportez-vous au fichier dédié :
👉 [API_ENDPOINTS_GUIDE.md](file:///c:/Users/funpa/DweLink-4das13-g6/Backend/API_ENDPOINTS_GUIDE.md)

---

## 6. Authentification et Sécurité JWT

- **Algorithme & Signature** : HMAC SHA-256 avec clé sécurisée.
- **En-tête HTTP** : `Authorization: Bearer <token>`
- **Utilisation dans Swagger UI** : Cliquez sur le bouton vert **Authorize** en haut à droite et saisissez votre token sous la forme `Bearer <token>`.
- **Propagation inter-services** : `pharmacy-service` utilise un `FeignClientInterceptor` pour propager automatiquement l'en-tête JWT vers `user-service`.

---

## 7. Exécution des tests

Pour exécuter la suite de tests automatisée (unitaires, mockito, validation d'intégration et Feign) :

```bash
cd Backend
mvn test
```
