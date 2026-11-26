package tn.esprit.donationservice.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.donationservice.enums.StatutDon;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Don {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long donateurId;

    @NotNull
    private Long pharmacieId;

    @NotNull
    private Long medicamentId;

    @NotNull
    @Positive
    private Integer quantite;

    @NotNull
    @FutureOrPresent
    private LocalDate datePeremption;

    @NotNull
    @Enumerated(EnumType.STRING)
    private StatutDon statut;

    @OneToOne(mappedBy = "don", cascade = CascadeType.ALL, orphanRemoval = true)
    private ControleDon controleDon;

    @OneToMany(mappedBy = "don", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RemiseSolidaire> remisesSolidaires = new ArrayList<>();
}
