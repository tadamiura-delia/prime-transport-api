package fr.delia.primetransport.configuration;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Requête pour modifier une semaine type.
 *
 * @param jours les 7 jours de la semaine, un par jour
 */
public record SemaineTypeRequest(@Size(min = 7, max = 7) @NotNull List<@Valid JourTypeRequest> jours) {
}
