\# DweLink — Donation Service



\## Présentation



Microservice de gestion des dons de médicaments et des remises solidaires du projet DweLink.



\## Technologies utilisées



\- Java 17

\- Spring Boot 3.2.5

\- Spring Cloud 2023.0.1

\- Spring Data JPA

\- PostgreSQL

\- Netflix Eureka Client

\- Maven

\- Lombok

\- Postman



\## Configuration



| Paramètre | Valeur |

|---|---|

| Nom du microservice | donation-service |

| Port | 8086 |

| Base de données | don\_db |

| Port PostgreSQL par défaut | 5432 |

| Port Eureka | 8761 |

| Package | tn.dwelink.donationservice |



\## Installation et démarrage



\### 1. Base de données



Créer une base PostgreSQL :



```sql

CREATE DATABASE don\_db;

```



\### 2. Configuration locale



Depuis le dossier Backend/donation-service :



```powershell

Copy-Item src/main/resources/application.properties.example src/main/resources/application.properties

```



Configurer les variables d'environnement :



```powershell

$env:DB\_URL = "jdbc:postgresql://localhost:5432/don\_db"

$env:DB\_PASSWORD = \[System.Net.NetworkCredential]::new('', (Read-Host "Mot de passe PostgreSQL" -AsSecureString)).Password

```



Adapter le port PostgreSQL selon l'installation locale.



\### 3. Démarrer Eureka Server



Depuis la racine du dépôt :



```powershell

.\\Backend\\donation-service\\mvnw.cmd -f Backend/eureka-server/pom.xml spring-boot:run

```



Dashboard Eureka : http://localhost:8761



\### 4. Démarrer Donation Service



Depuis Backend/donation-service :



```powershell

.\\mvnw.cmd spring-boot:run

```



Adresse du microservice : http://localhost:8086



\## Entités



\- Don : gestion des dons de médicaments.

\- ControleDon : contrôle des médicaments par le pharmacien.

\- RemiseSolidaire : distribution des médicaments aux bénéficiaires.



\## Endpoints CRUD



| Méthode | Endpoint | Description |

|---|---|---|

| GET | /api/dons | Consulter les dons |

| GET | /api/dons/{id} | Consulter un don |

| POST | /api/dons | Créer un don |

| PUT | /api/dons/{id} | Modifier un don |

| DELETE | /api/dons/{id} | Supprimer un don |

| GET | /api/controles-dons | Consulter les contrôles |

| GET | /api/controles-dons/{id} | Consulter un contrôle |

| POST | /api/controles-dons | Créer un contrôle |

| PUT | /api/controles-dons/{id} | Modifier un contrôle |

| DELETE | /api/controles-dons/{id} | Supprimer un contrôle |

| GET | /api/remises-solidaires | Consulter les remises |

| GET | /api/remises-solidaires/{id} | Consulter une remise |

| POST | /api/remises-solidaires | Créer une remise |

| PUT | /api/remises-solidaires/{id} | Modifier une remise |

| DELETE | /api/remises-solidaires/{id} | Supprimer une remise |



\## Endpoints métier complémentaires



| Méthode | Endpoint |

|---|---|

| GET | /api/dons/mes-dons?donateurId={id} |

| GET | /api/dons?pharmacieId={id}\&statut={statut}\&page={page} |

| PATCH | /api/dons/{id}/annuler |

| PATCH | /api/dons/{id}/decision |

| POST | /api/dons/{id}/controle |

| POST | /api/dons/{id}/remises |



\## Compilation et tests



Compilation :



```powershell

.\\mvnw.cmd clean compile

```



Test du contexte Spring sans Eureka, avec PostgreSQL configuré :



```powershell

.\\mvnw.cmd clean test "-Deureka.client.enabled=false"

```



Collection Postman disponible dans :



`postman/donation-service.postman\_collection.json`



\## Sécurité et configuration



Le fichier application.properties est ignoré par Git. Un fichier application.properties.example est fourni pour faciliter la configuration locale.



Aucun mot de passe ne doit être enregistré dans le dépôt Git.



\## État d'avancement



\- CRUD implémenté.

\- Connexion PostgreSQL validée.

\- Enregistrement Eureka validé sur le port 8086.

\- Compilation Java 17 réussie.

\- Test du contexte Spring réussi.

\- Tests Postman effectués.



L'authentification, les autorisations, la validation complète des règles métier et la communication interservices seront traitées dans les prochaines phases du projet.



\## Développeur



Mohamed Yassine Azzouz — Groupe 6, 4 DAS 13.
