package fr.delia.primetransport.referentiel;

/**
 * Représentation JSON d'un lieu de travail. On n'expose jamais l'entité JPA directement.
 */
public record LieuTravailDto(String code, String libelle, int ordre, boolean systeme) {

	static LieuTravailDto from(LieuTravail lieu) {
		return new LieuTravailDto(lieu.getCode(), lieu.getLibelle(), lieu.getOrdre(), lieu.isSysteme());
	}

}
