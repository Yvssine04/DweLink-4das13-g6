package tn.dwelink.pharmacyservice.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.dwelink.pharmacyservice.enums.StatutPharmacie;

@Entity
@Table(name = "pharmacie", indexes = @Index(name = "idx_pharmacie_ville", columnList = "ville"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pharmacie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nom;

    @Column(nullable = false, unique = true, length = 30)
    private String numeroAutorisation;

    @Column(length = 200)
    private String adresse;

    @Column(nullable = false, length = 60)
    private String ville;

    private Double latitude;

    private Double longitude;

    @Column(length = 20)
    private String telephone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatutPharmacie statut = StatutPharmacie.EN_ATTENTE;

    @OneToMany(mappedBy = "pharmacie", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Horaire> horaires = new ArrayList<>();

    @OneToMany(mappedBy = "pharmacie", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Garde> gardes = new ArrayList<>();

    @OneToMany(mappedBy = "pharmacie", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MembrePharmacie> membres = new ArrayList<>();
}
