package fr.delia.primetransport.configuration;

/**
 * Représentation JSON d'un jour de la semaine type.
 *
 * @param jour jour de la semaine, comme {@code Date.getDay()} : 0 = dimanche … 6 = samedi
 * @param lieuCode code du lieu de travail
 * @param transportCode code du moyen de transport, {@code null} si aucun
 */
public record JourTypeDto(short jour, String lieuCode, String transportCode) {

	static JourTypeDto from(JourType jourType) {
		String transportCode = jourType.getTransport() != null ? jourType.getTransport().getCode() : null;
		return new JourTypeDto(jourType.getJour(), jourType.getLieu().getCode(), transportCode);
	}
}
