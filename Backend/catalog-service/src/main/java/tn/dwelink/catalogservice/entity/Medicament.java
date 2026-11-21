package tn.dwelink.catalogservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.dwelink.catalogservice.enums.ConditionConservation;
import tn.dwelink.catalogservice.enums.FormePharmaceutique;
import tn.dwelink.catalogservice.enums.StatutMedicament;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medicament")
@Getter @Setter @NoArgsConstructor
public class Medicament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomCommercial;

    @Column(nullable = false, unique = true)
    private String codeBarres;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormePharmaceutique forme;

    private Boolean ordonnanceRequise = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConditionConservation conditionConservation = ConditionConservation.AMBIANTE;

    private Boolean stupefiant = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutMedicament statut = StatutMedicament.ACTIF;

    private LocalDate dateRetrait;

    private String categorie;

    private String laboratoire;

    private String imageUrl;

    @OneToMany(mappedBy = "medicament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompositionMedicament> compositions = new ArrayList<>();

    public void addComposition(CompositionMedicament c) {
        compositions.add(c);
        c.setMedicament(this);
    }

    public void removeComposition(CompositionMedicament c) {
        compositions.remove(c);
        c.setMedicament(null);
    }
}
