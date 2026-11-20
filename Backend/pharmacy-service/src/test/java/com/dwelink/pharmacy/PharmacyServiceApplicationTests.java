package com.dwelink.pharmacy;

import com.dwelink.pharmacy.client.UserClient;
import com.dwelink.pharmacy.client.dto.UserInternalResponse;
import com.dwelink.pharmacy.dto.request.CreatePharmacieRequest;
import com.dwelink.pharmacy.dto.response.MembreVerificationResponse;
import com.dwelink.pharmacy.dto.response.PharmacieResponse;
import com.dwelink.pharmacy.entity.StatutPharmacie;
import com.dwelink.pharmacy.repository.PharmacieRepository;
import com.dwelink.pharmacy.service.PharmacyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class PharmacyServiceApplicationTests {

    @Autowired
    private PharmacyService pharmacyService;

    @Autowired
    private PharmacieRepository pharmacieRepository;

    @MockBean
    private UserClient userClient;

    @BeforeEach
    void setUp() {
        pharmacieRepository.deleteAll();
    }

    @Test
    void testCreatePharmacieSuccessWithApprovedPharmacist() {
        UserInternalResponse mockUser = new UserInternalResponse(
                10L, "pharma@dwelink.com", "Dr", "House", "PHARMACIEN", "ACTIF"
        );
        when(userClient.getUserById(eq(10L))).thenReturn(mockUser);

        CreatePharmacieRequest request = new CreatePharmacieRequest();
        request.setTitulaireId(10L);
        request.setNom("Pharmacie Centrale");
        request.setNumeroAutorisation("AUTH-12345");
        request.setAdresse("10 Rue de la République");
        request.setVille("Tunis");
        request.setLatitude(36.8065);
        request.setLongitude(10.1815);
        request.setTelephone("71123456");

        PharmacieResponse response = pharmacyService.createPharmacie(request);

        assertNotNull(response.getId());
        assertEquals("Pharmacie Centrale", response.getNom());
        assertEquals(StatutPharmacie.EN_ATTENTE.name(), response.getStatut());
        assertEquals(1, response.getMembres().size());
        assertEquals(10L, response.getMembres().get(0).getUtilisateurId());
        assertEquals("TITULAIRE", response.getMembres().get(0).getRole());
        assertEquals("ACTIF", response.getMembres().get(0).getStatut());

        MembreVerificationResponse verif = pharmacyService.verifyMembre(response.getId(), 10L);
        assertTrue(verif.isEstMembreActif());
        assertEquals("TITULAIRE", verif.getRole());
    }

    @Test
    void testCreatePharmacieFailsWhenUserIsNotPharmacist() {
        UserInternalResponse mockPatient = new UserInternalResponse(
                20L, "patient@dwelink.com", "Jean", "Dupont", "PATIENT", "ACTIF"
        );
        when(userClient.getUserById(eq(20L))).thenReturn(mockPatient);

        CreatePharmacieRequest request = new CreatePharmacieRequest();
        request.setTitulaireId(20L);
        request.setNom("Pharmacie Invalide");
        request.setNumeroAutorisation("AUTH-99999");
        request.setAdresse("123 Street");
        request.setVille("Tunis");
        request.setLatitude(36.8);
        request.setLongitude(10.1);
        request.setTelephone("71000000");

        assertThrows(ResponseStatusException.class, () -> pharmacyService.createPharmacie(request));
    }
}
