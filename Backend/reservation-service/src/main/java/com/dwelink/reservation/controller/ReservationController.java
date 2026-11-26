package com.dwelink.reservation.controller;

import com.dwelink.reservation.dto.request.CreerReservationRequest;
import com.dwelink.reservation.dto.request.RefuserReservationRequest;
import com.dwelink.reservation.dto.request.TerminerReservationRequest;
import com.dwelink.reservation.dto.response.HistoriqueStatutResponse;
import com.dwelink.reservation.dto.response.ReservationResponse;
import com.dwelink.reservation.enums.StatutReservation;
import com.dwelink.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> creer(
            // TODO: replace with Keycloak JWT later
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreerReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.creer(userId, request));
    }

    @GetMapping("/mes-reservations")
    public ResponseEntity<List<ReservationResponse>> mesReservations(
            // TODO: replace with Keycloak JWT later
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) StatutReservation statut) {
        return ResponseEntity.ok(reservationService.mesReservations(userId, statut));
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> reservationsPharmacie(
            // TODO: replace with Keycloak JWT later
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long pharmacieId,
            @RequestParam(required = false) StatutReservation statut) {
        return ResponseEntity.ok(reservationService.reservationsPharmacie(userId, pharmacieId, statut));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> trouver(
            @PathVariable Long id,
            // TODO: replace with Keycloak JWT later
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(reservationService.trouverPourPatient(id, userId));
    }

    @PatchMapping("/{id}/confirmer")
    public ResponseEntity<ReservationResponse> confirmer(
            @PathVariable Long id,
            // TODO: replace with Keycloak JWT later
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(reservationService.confirmer(id, userId));
    }

    @PatchMapping("/{id}/refuser")
    public ResponseEntity<ReservationResponse> refuser(
            @PathVariable Long id,
            // TODO: replace with Keycloak JWT later
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody RefuserReservationRequest request) {
        return ResponseEntity.ok(reservationService.refuser(id, userId, request.motif()));
    }

    @PatchMapping("/{id}/annuler")
    public ResponseEntity<ReservationResponse> annuler(
            @PathVariable Long id,
            // TODO: replace with Keycloak JWT later
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(reservationService.annuler(id, userId));
    }

    @PatchMapping("/{id}/terminer")
    public ResponseEntity<ReservationResponse> terminer(
            @PathVariable Long id,
            // TODO: replace with Keycloak JWT later
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody TerminerReservationRequest request) {
        return ResponseEntity.ok(reservationService.terminer(id, userId, request.codeRetrait()));
    }

    @GetMapping("/{id}/historique")
    public ResponseEntity<List<HistoriqueStatutResponse>> historique(
            @PathVariable Long id,
            // TODO: replace with Keycloak JWT later
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(reservationService.historique(id, userId));
    }
}
