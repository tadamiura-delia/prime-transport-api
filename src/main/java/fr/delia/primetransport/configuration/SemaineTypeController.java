package fr.delia.primetransport.configuration;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/** Semaine type de l'utilisateur connecté, en lecture et en écriture. */
@RestController
@RequestMapping("/api/me/semaine-type")
public class SemaineTypeController {

	private static final String UTILISATEUR_PROVISOIRE = "alice";

	private final SemaineTypeService service;

	public SemaineTypeController(SemaineTypeService service) {
		this.service = service;
	}

	/**
	 * Trouve la semaine type d'un utilisateur connecté. Retourne une 404 si l'utilisateur
	 * n'a jamais renseigné une semaine type.
	 */
	@GetMapping
	public ResponseEntity<List<JourTypeDto>> findSemaineType() {
		List<JourTypeDto> jours = service.findSemaineType(UTILISATEUR_PROVISOIRE);
		return jours.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(jours);
	}

	/** Enregistrement d'une semaine type */
	@PutMapping
	public List<JourTypeDto> enregistrerSemaineType(@Valid @RequestBody SemaineTypeRequest request) {
		return service.enregistrerSemaineType(UTILISATEUR_PROVISOIRE, request);
	}

}
