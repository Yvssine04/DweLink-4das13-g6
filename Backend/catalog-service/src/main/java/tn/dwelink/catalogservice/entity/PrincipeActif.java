package tn.dwelink.catalogservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "principe_actif")
@Getter @Setter @NoArgsConstructor
public class PrincipeActif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_dci", nullable = false, unique = true)
    private String nomDci;
}