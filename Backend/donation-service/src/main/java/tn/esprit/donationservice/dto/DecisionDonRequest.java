package tn.esprit.donationservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.donationservice.enums.StatutDon;

@Getter
@Setter
@NoArgsConstructor
public class DecisionDonRequest {

    @NotNull
    private StatutDon statut;
}
