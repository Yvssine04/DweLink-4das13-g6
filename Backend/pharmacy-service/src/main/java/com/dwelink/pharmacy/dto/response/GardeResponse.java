package com.dwelink.pharmacy.dto.response;

import java.time.LocalDateTime;

public class GardeResponse {
    private Long id;
    private LocalDateTime debut;
    private LocalDateTime fin;

    public GardeResponse() {}

    public GardeResponse(Long id, LocalDateTime debut, LocalDateTime fin) {
        this.id = id;
        this.debut = debut;
        this.fin = fin;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDebut() { return debut; }
    public void setDebut(LocalDateTime debut) { this.debut = debut; }

    public LocalDateTime getFin() { return fin; }
    public void setFin(LocalDateTime fin) { this.fin = fin; }
}
