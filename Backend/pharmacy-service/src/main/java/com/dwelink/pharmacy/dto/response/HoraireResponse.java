package com.dwelink.pharmacy.dto.response;

import java.time.LocalTime;

public class HoraireResponse {
    private Long id;
    private String jour;
    private LocalTime heureOuverture;
    private LocalTime heureFermeture;

    public HoraireResponse() {}

    public HoraireResponse(Long id, String jour, LocalTime heureOuverture, LocalTime heureFermeture) {
        this.id = id;
        this.jour = jour;
        this.heureOuverture = heureOuverture;
        this.heureFermeture = heureFermeture;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getJour() { return jour; }
    public void setJour(String jour) { this.jour = jour; }

    public LocalTime getHeureOuverture() { return heureOuverture; }
    public void setHeureOuverture(LocalTime heureOuverture) { this.heureOuverture = heureOuverture; }

    public LocalTime getHeureFermeture() { return heureFermeture; }
    public void setHeureFermeture(LocalTime heureFermeture) { this.heureFermeture = heureFermeture; }
}
