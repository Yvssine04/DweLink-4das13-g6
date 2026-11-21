package com.dwelink.user.dto.request;

import jakarta.validation.constraints.NotNull;

public class StatutCompteRequest {
    @NotNull(message = "Le statut est obligatoire (ACTIF, SUSPENDU, DESACTIVE)")
    private String statut;

    public StatutCompteRequest() {}

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
