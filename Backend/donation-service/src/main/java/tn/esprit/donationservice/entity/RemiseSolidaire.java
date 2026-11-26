package tn.esprit.donationservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RemiseSolidaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long pharmacienId;

    @NotNull
    private String beneficiaireRef;

    @NotNull
    @Positive
    private Integer quantite;

    @NotNull
    @PastOrPresent
    private LocalDate dateRemise;

    @ManyToOne
    @JoinColumn(name = "don_id", nullable = false)
    @JsonIgnoreProperties({"controleDon", "remisesSolidaires"})
    private Don don;
}
