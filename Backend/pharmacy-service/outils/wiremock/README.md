# Faux user-service (WireMock)

Simule `GET /api/utilisateurs/{id}` de user-service pour tester pharmacy-service sans le vrai service.

| Utilisateur | Réponse |
|---|---|
| 100 | 200 — `PHARMACIEN` / `ACTIF` |
| 101 | 200 — `PHARMACIEN` / `EN_ATTENTE` |
| 102 | 200 — `PATIENT` / `ACTIF` |
| tout autre id | 404 |

Les réponses sont définies dans `mappings/`.

## Lancer sur le port 8081

Depuis `Backend/pharmacy-service` :

```bash
docker run -d --rm --name faux-user-service -p 8081:8080 \
  -v "$(pwd)/outils/wiremock/mappings:/home/wiremock/mappings:ro" \
  wiremock/wiremock:3.13.2
```

Sous PowerShell, remplacer `$(pwd)` par `${PWD}` et les `\` de fin de ligne par des `` ` ``.

Vérifier : `curl http://localhost:8081/api/utilisateurs/100`

## Arrêter

```bash
docker stop faux-user-service
```

Le port 8081 doit être libre : c'est celui du vrai user-service (`services.utilisateur.url`).
