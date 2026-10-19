package fr.delia.primetransport.configuration;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Requête pour modifier la distance d'un lieu de travail
 *
 * @param distanceKm distance aller simple en km
 */
public record DistanceLieuRequest(@NotNull @Digits(integer = 3, fraction = 2) @PositiveOrZero BigDecimal distanceKm) {

}
