package fr.delia.primetransport.configuration;

import java.math.BigDecimal;

import fr.delia.primetransport.referentiel.LieuTravail;

/**
 * Représentation JSON d'une distance par rapport à un lieu. On n'expose jamais l'entité
 * JPA directement.
 */
public record DistanceLieuDto(String lieuCode, String libelle, BigDecimal distanceKm) {

	static DistanceLieuDto from(LieuTravail lieu, BigDecimal distanceKm) {
		return new DistanceLieuDto(lieu.getCode(), lieu.getLibelle(), distanceKm);
	}
}
