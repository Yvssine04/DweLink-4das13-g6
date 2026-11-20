package com.dwelink.pharmacy.client.dto;

public class UserContactResponse {
    private Long id;
    private String email;
    private String prenom;
    private String nom;
    private String telephone;

    public UserContactResponse() {}

    public UserContactResponse(Long id, String email, String prenom, String nom, String telephone) {
        this.id = id;
        this.email = email;
        this.prenom = prenom;
        this.nom = nom;
        this.telephone = telephone;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
}
