# IMPLEMENTATION PLAN — DWELINK MICROSERVICES

## 1. Architecture Overview
DweLink is an e-health microservices platform connecting patients, pharmacists, and donors.
Per requirements, this project implements three independent Spring Boot 3 applications located inside `Backend/`:
1. **eureka-server**: Service discovery registry (Port 8761).
2. **user-service**: Complete user management, authentication (JWT/BCrypt), role and account statuses, pharmacist validation, and internal endpoints for pharmacy-service (Port 8081, DB: `utilisateur_db`).
3. **pharmacy-service**: Limited pharmacy service focusing on integration with user-service, pharmacist validation, pharmacy member management, and inter-service Feign discovery (Port 8082, DB: `pharmacie_db`).

```
                    +-------------------+
                    |   EUREKA SERVER   |
                    |    (Port 8761)    |
                    +---------+---------+
                              ^
             registers &      |     registers &
             discovers        |     discovers
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

Strict architectural restrictions followed:
- No Keycloak, RabbitMQ, Kafka, Redis, or API Gateway in the runtime stack.
- Dedicated standalone Spring Security + JWT authentication in user-service and JWT propagation in pharmacy-service.
- Synchronous inter-service communication using Spring Cloud OpenFeign and Eureka.
- Completely isolated PostgreSQL databases with Flyway migrations.
- Complete documentation via Springdoc OpenAPI / Swagger UI.

---

## 2. Responsibilities of Each Application

### 2.1 eureka-server (Port 8761)
- Spring Cloud Netflix Eureka Server.
- Service registration and discovery for all microservices.
- Web dashboard accessible at `http://localhost:8761`.

### 2.2 user-service (Port 8081)
- Central identity and user management service.
- Database: `utilisateur_db` (PostgreSQL).
- Entities: `ProfilUtilisateur` (user), `DemandePharmacien` (pharmacist registration request), `JustificatifDemande` (verification document).
- Security: Spring Security 6, BCrypt password hashing, stateless JWT generation and validation filter.
- Functionality: Registration, Login, Profile consultation and update, Pharmacist registration workflow, Administrative review and decision (approve/refuse), Account status management (activate/suspend/deactivate), Internal APIs for other microservices.

### 2.3 pharmacy-service (Port 8082)
- Limited pharmacy service strictly for user-service integration and member validation.
- Database: `pharmacie_db` (PostgreSQL).
- Entities: `Pharmacie`, `MembrePharmacie`, `Horaire`, `Garde`.
- Functionality: Pharmacy creation (validates pharmacist with user-service via OpenFeign), Pharmacy details/search, Member addition & approval/removal, Inter-service Feign client with JWT propagation, Resilience & error handling (503 Service Unavailable when user-service unreachable).

---

## 3. Complete Endpoint Inventory

### 3.1 user-service Endpoints (Port 8081)

| HTTP Method | URL | Description | Request DTO | Response DTO | Required Role / Auth | Business Rules / Status Codes |
|---|---|---|---|---|---|---|
| POST | `/api/auth/register` | Register a new user account | `RegisterRequest` | `AuthResponse` | Public | Validates email uniqueness, hashes password, defaults status to ACTIF (or EN_ATTENTE_VALIDATION for PHARMACIEN). Returns 201/400/409. |
| POST | `/api/auth/login` | Authenticate user and obtain JWT | `LoginRequest` | `AuthResponse` | Public | Validates credentials and account status (must not be SUSPENDU or DESACTIVE). Returns 200/401/403. |
| GET | `/api/utilisateurs/moi` | Get current user's profile | None | `ProfilResponse` | Authenticated | Extracts user from JWT Bearer token. Returns 200/401/404. |
| PATCH | `/api/utilisateurs/moi` | Update current user's profile & preferences | `UpdateProfilRequest` | `ProfilResponse` | Authenticated | Updates first name, last name, phone, language, search radius. Returns 200/400/401. |
| POST | `/api/demandes-pharmacien` | Submit pharmacist verification application | `DemandePharmacienRequest` | `DemandePharmacienResponse` | Authenticated | Creates pharmacist registration request (statut: DEPOSEE). Returns 201/400/401/409. |
| POST | `/api/demandes-pharmacien/{id}/justificatifs` | Add proof document to pharmacist request | `JustificatifRequest` | `JustificatifResponse` | Authenticated (Owner) | Adds document (DIPLOME, CARTE_ORDRE, PIECE_IDENTITE). Returns 201/400/404. |
| GET | `/api/demandes-pharmacien` | List pharmacist verification requests | Query: `?statut=` | `List<DemandePharmacienResponse>` | ADMIN | Filter by status (optional). Returns 200/403. |
| PATCH | `/api/demandes-pharmacien/{id}/decision` | Admin approves or rejects pharmacist request | `DecisionDemandeRequest` | `DemandePharmacienResponse` | ADMIN | Updates request status to APPROUVEE or REFUSEE. If APPROUVEE, upgrades user role to PHARMACIEN and status to ACTIF. Returns 200/400/403/404/409. |
| PATCH | `/api/utilisateurs/{id}/statut` | Suspend or reactivate user account | `StatutCompteRequest` | `ProfilResponse` | ADMIN | Modifies account status (ACTIF, SUSPENDU, DESACTIVE). Returns 200/400/403/404. |
| GET | `/api/utilisateurs/{id}` | Internal API: Get user status and role | None | `UserInternalResponse` | Internal / Authenticated | Used by pharmacy-service via Feign to verify pharmacist existence, role, and approval. Returns 200/404. |
| GET | `/api/utilisateurs/{id}/contact` | Internal API: Get user contact details | None | `UserContactResponse` | Internal / Authenticated | Returns email, phone, name for notifications and verification. Returns 200/404. |

### 3.2 pharmacy-service Integration Endpoints (Port 8082)

| HTTP Method | URL | Description | Request DTO | Response DTO | Required Role / Auth | Business Rules / Status Codes |
|---|---|---|---|---|---|---|
| POST | `/api/pharmacies` | Create a pharmacy | `CreatePharmacieRequest` | `PharmacieResponse` | PHARMACIEN | Calls user-service `/api/utilisateurs/{id}` to verify user is an approved active PHARMACIEN. Creates pharmacy with titulaire member. Returns 201/400/403/409/503. |
| GET | `/api/pharmacies/{id}` | Get pharmacy details | None | `PharmacieResponse` | Public / Authenticated | Retrieves pharmacy information, hours, guards, members. Returns 200/404. |
| PUT | `/api/pharmacies/{id}` | Update pharmacy details | `UpdatePharmacieRequest` | `PharmacieResponse` | TITULAIRE of pharmacy | Updates pharmacy name, address, phone. Returns 200/400/403/404. |
| GET | `/api/pharmacies` | List pharmacies with optional filter | Query: `?ville=&deGarde=` | `List<PharmacieResponse>` | Public | Search pharmacies. Returns 200. |
| GET | `/api/pharmacies/proches` | Find nearby pharmacies | Query: `?lat=&lng=&rayonKm=` | `List<PharmacieResponse>` | Public | Geographical distance calculation. Returns 200/400. |
| PATCH | `/api/pharmacies/{id}/statut` | Admin updates pharmacy status | `StatutPharmacieRequest` | `PharmacieResponse` | ADMIN | Updates status (EN_ATTENTE, VALIDEE, SUSPENDUE). Returns 200/403/404. |
| PUT | `/api/pharmacies/{id}/horaires` | Update opening hours | `List<HoraireRequest>` | `List<HoraireResponse>` | TITULAIRE | Sets schedule. Returns 200/400/403/404. |
| POST | `/api/pharmacies/{id}/gardes` | Add guard duty | `GardeRequest` | `GardeResponse` | TITULAIRE | Schedules duty window. Returns 201/400/403/409. |
| POST | `/api/pharmacies/{id}/membres` | Request to join pharmacy as member | `DemandeMembreRequest` | `MembrePharmacieResponse` | PHARMACIEN | Verifies via user-service that requesting user is active PHARMACIEN. Status: EN_ATTENTE. Returns 201/400/403/409/503. |
| PATCH | `/api/pharmacies/{id}/membres/{membreId}` | Titulaire accepts or rejects member | `DecisionMembreRequest` | `MembrePharmacieResponse` | TITULAIRE | Updates membership status to ACTIF or REFUSE. Returns 200/403/404/409. |
| DELETE | `/api/pharmacies/{id}/membres/{membreId}` | Titulaire removes member | None | None | TITULAIRE | Removes member. Returns 204/403/404. |
| GET | `/api/pharmacies/{id}/membres/verifier` | Internal API: Verify if user is active member | Query: `?utilisateurId=` | `MembreVerificationResponse` | Internal / Services | Returns boolean active status. Returns 200/404. |
