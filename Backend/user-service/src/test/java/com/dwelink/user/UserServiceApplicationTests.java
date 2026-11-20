package com.dwelink.user;

import com.dwelink.user.dto.request.LoginRequest;
import com.dwelink.user.dto.request.RegisterRequest;
import com.dwelink.user.dto.response.AuthResponse;
import com.dwelink.user.entity.ProfilUtilisateur;
import com.dwelink.user.entity.RoleUtilisateur;
import com.dwelink.user.entity.StatutCompte;
import com.dwelink.user.repository.ProfilUtilisateurRepository;
import com.dwelink.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private ProfilUtilisateurRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterAndLoginPatientSuccess() {
        RegisterRequest registerReq = new RegisterRequest();
        registerReq.setEmail("patient@dwelink.com");
        registerReq.setMotDePasse("Secret123!");
        registerReq.setPrenom("Jean");
        registerReq.setNom("Dupont");
        registerReq.setTelephone("0612345678");
        registerReq.setRole("PATIENT");

        AuthResponse authRes = userService.register(registerReq);
        assertNotNull(authRes.getToken());
        assertEquals("patient@dwelink.com", authRes.getEmail());
        assertEquals(RoleUtilisateur.PATIENT.name(), authRes.getRole());
        assertEquals(StatutCompte.ACTIF.name(), authRes.getStatut());

        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("patient@dwelink.com");
        loginReq.setMotDePasse("Secret123!");

        AuthResponse loginRes = userService.login(loginReq);
        assertNotNull(loginRes.getToken());
        assertEquals(authRes.getId(), loginRes.getId());
    }

    @Test
    void testDuplicateEmailRegistrationFails() {
        RegisterRequest req1 = new RegisterRequest();
        req1.setEmail("duplicate@dwelink.com");
        req1.setMotDePasse("Secret123!");
        req1.setPrenom("A");
        req1.setNom("B");

        userService.register(req1);

        RegisterRequest req2 = new RegisterRequest();
        req2.setEmail("duplicate@dwelink.com");
        req2.setMotDePasse("OtherSecret123!");
        req2.setPrenom("C");
        req2.setNom("D");

        assertThrows(ResponseStatusException.class, () -> userService.register(req2));
    }

    @Test
    void testPharmacienRegistrationStartsInPendingStatus() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("pharmacist@dwelink.com");
        req.setMotDePasse("PharmaPass123!");
        req.setPrenom("Dr");
        req.setNom("House");
        req.setRole("PHARMACIEN");

        AuthResponse res = userService.register(req);
        assertEquals(RoleUtilisateur.PHARMACIEN.name(), res.getRole());
        assertEquals(StatutCompte.EN_ATTENTE_VALIDATION.name(), res.getStatut());
    }
}
