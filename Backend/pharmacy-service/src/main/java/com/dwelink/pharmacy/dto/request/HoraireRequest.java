package com.dwelink.pharmacy.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public class HoraireRequest {
    @NotNull(message = "Le jour est obligatoire (LUNDI..DIMANCHE)")
    private String jour;

    @NotNull(message = "L'heure d'ouverture est obligatoire")
    private LocalTime heureOuverture;

    @NotNull(message = "L'heure de fermeture est obligatoire")
    private LocalTime heureFermeture;

    public HoraireRequest() {}

    public String getJour() { return jour; }
    public void setJour(String jour) { this.jour = jour; }

    public LocalTime getHeureOuverture() { return heureOuverture; }
    public void setHeureOuverture(LocalTime heureOuverture) { this.heureOuverture = heureOuverture; }

    public LocalTime getHeureFermeture() { return heureFermeture; }
    public void setHeureFermeture(LocalTime heureFermeture) { this.heureFermeture = heureFermeture; }
}
