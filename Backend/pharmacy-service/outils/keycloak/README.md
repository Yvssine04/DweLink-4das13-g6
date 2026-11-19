# Keycloak de test (realm `dwelink`)

Serveur d'authentification pour tester pharmacy-service avec de vrais jetons JWT.
Le realm est décrit dans `realm-dwelink.json` et importé au démarrage du conteneur.

**Uniquement pour le développement** : mots de passe triviaux, mode `start-dev`, HTTP sans TLS.

## Contenu du realm

- Rôles du realm : `ADMIN`, `PHARMACIEN`, `PATIENT` (pharmacy-service les reçoit dans `realm_access.roles`
  et les transforme en `ROLE_ADMIN`, `ROLE_PHARMACIEN`, `ROLE_PATIENT`).
- Client public `dwelink-frontend`, avec « Direct access grants » activé (obtention d'un jeton par
  identifiant / mot de passe, utilisé par Postman).
- Mapper `utilisateurId` : copie l'attribut utilisateur `utilisateurId` dans le jeton (claim numérique, type long).
  Cet attribut n'est modifiable que par un administrateur Keycloak, pas par l'utilisateur lui-même.
- Durée de vie d'un jeton d'accès : 30 minutes.

| Utilisateur | Mot de passe | utilisateurId | Rôle |
|---|---|---|---|
| `admin` | `admin` | 900 | ADMIN |
| `pharmacien` | `pharmacien` | 100 | PHARMACIEN |
| `patient` | `patient` | 102 | PATIENT |

Les identifiants 100 et 102 correspondent aux utilisateurs simulés par le faux user-service (`outils/wiremock`).

## Lancer sur le port 8180

Depuis `Backend/pharmacy-service` :

```bash
docker run -d --rm --name keycloak-dwelink -p 8180:8080 \
  -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
  -v "$(pwd)/outils/keycloak/realm-dwelink.json:/opt/keycloak/data/import/realm-dwelink.json:ro" \
  quay.io/keycloak/keycloak:26.4.5 start-dev --import-realm
```

Sous PowerShell, remplacer `$(pwd)` par `${PWD}` et les `\` de fin de ligne par des `` ` ``.
Sous Git Bash, préfixer la commande par `MSYS_NO_PATHCONV=1`.

Le démarrage prend environ 20 à 40 secondes. Vérifier :
`curl http://localhost:8180/realms/dwelink/.well-known/openid-configuration`

Console d'administration : http://localhost:8180 (compte `admin` / `admin` du realm `master`,
distinct de l'utilisateur `admin` du realm `dwelink`).

## Obtenir un jeton

```bash
curl -X POST http://localhost:8180/realms/dwelink/protocol/openid-connect/token \
  -d grant_type=password -d client_id=dwelink-frontend \
  -d username=pharmacien -d password=pharmacien
```

Puis appeler pharmacy-service avec l'en-tête `Authorization: Bearer <access_token>`.
Dans Postman, utiliser les requêtes du dossier « 0. Authentification » : elles enregistrent le jeton
dans la variable `{{token}}`.

## Arrêter

```bash
docker stop keycloak-dwelink
```

Le conteneur est lancé avec `--rm` : le realm est réimporté à neuf à chaque démarrage.
