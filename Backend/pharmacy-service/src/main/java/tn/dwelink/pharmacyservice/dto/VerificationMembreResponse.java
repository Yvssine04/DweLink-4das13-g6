package tn.dwelink.pharmacyservice.dto;

import tn.dwelink.pharmacyservice.enums.RoleMembre;

public record VerificationMembreResponse(boolean membreActif, RoleMembre role) {
}
