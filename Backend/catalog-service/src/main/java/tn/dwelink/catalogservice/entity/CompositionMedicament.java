package tn.dwelink.catalogservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "composition_medicament")
@Getter @Setter @NoArgsConstructor
public class CompositionMedicament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double dosage;

    @Column(nullable = false)
    private String unite;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Medicament medicament;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PrincipeActif principeActif;
}
