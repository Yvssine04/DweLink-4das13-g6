package com.dwelink.user.dto.response;

import java.time.LocalDateTime;

public class JustificatifResponse {
    private Long id;
    private String type;
    private String nomFichier;
    private LocalDateTime dateAjout;

    public JustificatifResponse() {}

    public JustificatifResponse(Long id, String type, String nomFichier, LocalDateTime dateAjout) {
        this.id = id;
        this.type = type;
        this.nomFichier = nomFichier;
        this.dateAjout = dateAjout;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getNomFichier() { return nomFichier; }
    public void setNomFichier(String nomFichier) { this.nomFichier = nomFichier; }

    public LocalDateTime getDateAjout() { return dateAjout; }
    public void setDateAjout(LocalDateTime dateAjout) { this.dateAjout = dateAjout; }
}
