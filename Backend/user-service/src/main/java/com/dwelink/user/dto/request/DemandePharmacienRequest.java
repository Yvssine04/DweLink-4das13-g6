package com.dwelink.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public class DemandePharmacienRequest {
    @NotBlank(message = "Le numéro d'ordre est obligatoire")
    private String numeroOrdre;

    @NotBlank(message = "Le diplôme est obligatoire")
    private String diplome;

    public DemandePharmacienRequest() {}

    public String getNumeroOrdre() { return numeroOrdre; }
    public void setNumeroOrdre(String numeroOrdre) { this.numeroOrdre = numeroOrdre; }

    public String getDiplome() { return diplome; }
    public void setDiplome(String diplome) { this.diplome = diplome; }
}
