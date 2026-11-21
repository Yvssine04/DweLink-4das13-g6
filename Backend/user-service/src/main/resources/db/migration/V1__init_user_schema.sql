-- V1__init_user_schema.sql

CREATE TABLE IF NOT EXISTS profil_utilisateur (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    telephone VARCHAR(50),
    role VARCHAR(50) NOT NULL,
    statut VARCHAR(50) NOT NULL,
    langue VARCHAR(10) DEFAULT 'fr',
    rayon_recherche_km INTEGER DEFAULT 10,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP
);

CREATE TABLE IF NOT EXISTS demande_pharmacien (
    id BIGSERIAL PRIMARY KEY,
    utilisateur_id BIGINT NOT NULL,
    numero_ordre VARCHAR(100) NOT NULL,
    diplome VARCHAR(255) NOT NULL,
    statut VARCHAR(50) NOT NULL,
    date_depot TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_decision TIMESTAMP,
    examinee_par BIGINT,
    commentaire_decision TEXT,
    CONSTRAINT fk_demande_utilisateur FOREIGN KEY (utilisateur_id) REFERENCES profil_utilisateur(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS justificatif_demande (
    id BIGSERIAL PRIMARY KEY,
    demande_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    nom_fichier VARCHAR(255) NOT NULL,
    date_ajout TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_justificatif_demande FOREIGN KEY (demande_id) REFERENCES demande_pharmacien(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_profil_email ON profil_utilisateur(email);
CREATE INDEX IF NOT EXISTS idx_demande_statut ON demande_pharmacien(statut);
CREATE INDEX IF NOT EXISTS idx_demande_user ON demande_pharmacien(utilisateur_id);
