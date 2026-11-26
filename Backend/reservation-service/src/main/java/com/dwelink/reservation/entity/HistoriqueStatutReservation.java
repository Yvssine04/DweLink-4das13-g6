package com.dwelink.reservation.entity;

import com.dwelink.reservation.enums.StatutReservation;
import com.dwelink.reservation.enums.TypeActeur;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "historique_statut_reservation")
@Getter
@Setter
@NoArgsConstructor
public class HistoriqueStatutReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatutReservation ancienStatut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutReservation nouveauStatut;

    private Long acteurId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeActeur typeActeur;

    @Column(length = 500)
    private String motif;

    @Column(nullable = false)
    private LocalDateTime dateChangement;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;
}
