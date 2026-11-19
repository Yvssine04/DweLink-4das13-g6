package tn.dwelink.pharmacyservice.entity;

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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import tn.dwelink.pharmacyservice.enums.RoleMembre;
import tn.dwelink.pharmacyservice.enums.StatutMembre;

@Entity
@Table(name = "membre_pharmacie",
        uniqueConstraints = @UniqueConstraint(name = "uk_membre_pharmacie_utilisateur",
                columnNames = {"pharmacie_id", "utilisateur_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembrePharmacie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "utilisateur_id", nullable = false)
    private Long utilisateurId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoleMembre role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatutMembre statut = StatutMembre.EN_ATTENTE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pharmacie_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Pharmacie pharmacie;
}
