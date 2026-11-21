package com.dwelink.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class JustificatifRequest {
    @NotNull(message = "Le type de justificatif est obligatoire (DIPLOME, CARTE_ORDRE, PIECE_IDENTITE)")
    private String type;

    @NotBlank(message = "Le nom du fichier est obligatoire")
    private String nomFichier;

    public JustificatifRequest() {}

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getNomFichier() { return nomFichier; }
    public void setNomFichier(String nomFichier) { this.nomFichier = nomFichier; }
}
