package com.dwelink.pharmacy.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "horaire")
public class Horaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacie_id", nullable = false)
    private Pharmacie pharmacie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JourSemaine jour;

    @Column(name = "heure_ouverture", nullable = false)
    private LocalTime heureOuverture;

    @Column(name = "heure_fermeture", nullable = false)
    private LocalTime heureFermeture;

    public Horaire() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Pharmacie getPharmacie() { return pharmacie; }
    public void setPharmacie(Pharmacie pharmacie) { this.pharmacie = pharmacie; }

    public JourSemaine getJour() { return jour; }
    public void setJour(JourSemaine jour) { this.jour = jour; }

    public LocalTime getHeureOuverture() { return heureOuverture; }
    public void setHeureOuverture(LocalTime heureOuverture) { this.heureOuverture = heureOuverture; }

    public LocalTime getHeureFermeture() { return heureFermeture; }
    public void setHeureFermeture(LocalTime heureFermeture) { this.heureFermeture = heureFermeture; }
}
