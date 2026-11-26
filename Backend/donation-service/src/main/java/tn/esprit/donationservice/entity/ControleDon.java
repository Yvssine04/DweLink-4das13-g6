package tn.esprit.donationservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ControleDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long pharmacienId;

    @NotNull
    private Boolean emballageIntact;

    @NotNull
    private Boolean dateValide;

    @NotNull
    private Boolean accepte;

    private String motifRefus;

    @OneToOne
    @JoinColumn(name = "don_id", unique = true)
    @JsonIgnoreProperties({"controleDon", "remisesSolidaires"})
    private Don don;
}
