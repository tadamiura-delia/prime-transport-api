package fr.delia.primetransport.configuration;

import java.math.BigDecimal;

import fr.delia.primetransport.referentiel.LieuTravail;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Représente la distance entre le domicile et le lieu de travail.
 */
@Entity
@Table(name = "distance_lieu")
public class DistanceLieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String utilisateurId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lieu_code", nullable = false)
    private LieuTravail lieu;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal distanceKm;

    protected DistanceLieu() {
    }

    public DistanceLieu(String utilisateurId, LieuTravail lieu, BigDecimal distanceKm) {
        this.utilisateurId = utilisateurId;
        this.lieu = lieu;
        this.distanceKm = distanceKm;
    }

    public Long getId() {
        return id;
    }

    public String getUtilisateurId() {
        return utilisateurId;
    }

    public LieuTravail getLieu() {
        return lieu;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }
}
