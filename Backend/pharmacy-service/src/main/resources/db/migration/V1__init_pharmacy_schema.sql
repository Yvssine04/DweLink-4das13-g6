-- V1__init_pharmacy_schema.sql

CREATE TABLE IF NOT EXISTS pharmacie (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    numero_autorisation VARCHAR(100) NOT NULL UNIQUE,
    adresse VARCHAR(255) NOT NULL,
    ville VARCHAR(100) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    telephone VARCHAR(50) NOT NULL,
    statut VARCHAR(50) NOT NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS membre_pharmacie (
    id BIGSERIAL PRIMARY KEY,
    pharmacie_id BIGINT NOT NULL,
    utilisateur_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    statut VARCHAR(50) NOT NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_membre_pharmacie FOREIGN KEY (pharmacie_id) REFERENCES pharmacie(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS horaire (
    id BIGSERIAL PRIMARY KEY,
    pharmacie_id BIGINT NOT NULL,
    jour VARCHAR(20) NOT NULL,
    heure_ouverture TIME NOT NULL,
    heure_fermeture TIME NOT NULL,
    CONSTRAINT fk_horaire_pharmacie FOREIGN KEY (pharmacie_id) REFERENCES pharmacie(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS garde (
    id BIGSERIAL PRIMARY KEY,
    pharmacie_id BIGINT NOT NULL,
    debut TIMESTAMP NOT NULL,
    fin TIMESTAMP NOT NULL,
    CONSTRAINT fk_garde_pharmacie FOREIGN KEY (pharmacie_id) REFERENCES pharmacie(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_pharmacie_ville ON pharmacie(ville);
CREATE INDEX IF NOT EXISTS idx_membre_user ON membre_pharmacie(utilisateur_id);
