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
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import tn.dwelink.pharmacyservice.enums.JourSemaine;

@Entity
@Table(name = "horaire")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Horaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private JourSemaine jour;

    @Column(nullable = false)
    private LocalTime heureOuverture;

    @Column(nullable = false)
    private LocalTime heureFermeture;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pharmacie_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Pharmacie pharmacie;
}
