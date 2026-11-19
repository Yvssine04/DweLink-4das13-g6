package tn.dwelink.pharmacyservice.securite;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import tn.dwelink.pharmacyservice.exception.NonAuthentifieException;

/**
 * Donne l'identifiant de l'utilisateur connecté, lu dans le claim "utilisateurId" du jeton JWT Keycloak.
 * Remplace l'ancien en-tête X-Utilisateur-Id.
 */
@Component
public class UtilisateurCourant {

    public static final String CLAIM_UTILISATEUR_ID = "utilisateurId";

    public Long id() {
        Authentication authentification = SecurityContextHolder.getContext().getAuthentication();
        if (authentification == null || !(authentification.getPrincipal() instanceof Jwt jwt)) {
            throw new NonAuthentifieException("Authentification requise");
        }
        Object valeur = jwt.getClaim(CLAIM_UTILISATEUR_ID);
        if (valeur instanceof Number nombre) {
            return nombre.longValue();
        }
        if (valeur instanceof String texte) {
            try {
                return Long.valueOf(texte.trim());
            } catch (NumberFormatException e) {
                // traité comme un claim absent ci-dessous
            }
        }
        throw new NonAuthentifieException("Le jeton ne contient pas d'identifiant utilisateur (claim utilisateurId)");
    }
}
