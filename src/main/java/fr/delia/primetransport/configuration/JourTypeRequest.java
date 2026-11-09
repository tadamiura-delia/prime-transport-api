package fr.delia.primetransport.configuration;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Requête pour modifier un jour type.
 *
 * @param jour jour de la semaine, comme {@code Date.getDay()} : 0 = dimanche … 6 = samedi
 * @param lieuCode code du lieu de travail
 * @param transportCode code du moyen de transport, {@code null} si aucun
 */
public record JourTypeRequest(@Min(0) @Max(6) @NotNull Short jour, @NotBlank String lieuCode, String transportCode) {
}
