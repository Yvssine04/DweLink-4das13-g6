package com.dwelink.user.service;

import com.dwelink.user.dto.request.*;
import com.dwelink.user.dto.response.*;

import java.util.List;

public interface UserService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    ProfilResponse getCurrentUserProfile(String email);
    ProfilResponse updateCurrentUserProfile(String email, UpdateProfilRequest request);
    
    DemandePharmacienResponse submitDemandePharmacien(String email, DemandePharmacienRequest request);
    JustificatifResponse addJustificatifToDemande(Long demandeId, String email, JustificatifRequest request);
    List<DemandePharmacienResponse> getDemandesPharmacien(String statut);
    DemandePharmacienResponse decideDemande(Long demandeId, String adminEmail, DecisionDemandeRequest request);

    ProfilResponse updateAccountStatus(Long userId, StatutCompteRequest request);
    UserInternalResponse getUserInternalById(Long userId);
    UserContactResponse getUserContactById(Long userId);
}
