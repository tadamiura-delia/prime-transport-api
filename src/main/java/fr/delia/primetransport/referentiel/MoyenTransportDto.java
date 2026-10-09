package fr.delia.primetransport.referentiel;

/** Représentation JSON d'un moyen de transport. */
public record MoyenTransportDto(String code, String libelle, int ordre) {

	static MoyenTransportDto from(MoyenTransport transport) {
		return new MoyenTransportDto(transport.getCode(), transport.getLibelle(), transport.getOrdre());
	}

}
