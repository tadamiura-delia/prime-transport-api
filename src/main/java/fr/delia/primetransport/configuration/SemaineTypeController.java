package fr.delia.primetransport.configuration;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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

	private final SemaineTypeService service;

	public SemaineTypeController(SemaineTypeService service) {
		this.service = service;
	}

	/**
	 * Trouve la semaine type d'un utilisateur connecté. Retourne une 404 si l'utilisateur
	 * n'a jamais renseigné une semaine type.
	 */
	@GetMapping
	public ResponseEntity<List<JourTypeDto>> findSemaineType(@AuthenticationPrincipal Jwt jwt) {
		List<JourTypeDto> jours = service.findSemaineType(jwt.getSubject());
		return jours.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(jours);
	}

	/** Enregistrement d'une semaine type */
	@PutMapping
	public List<JourTypeDto> enregistrerSemaineType(@Valid @RequestBody SemaineTypeRequest request,
			@AuthenticationPrincipal Jwt jwt) {
		return service.enregistrerSemaineType(jwt.getSubject(), request);
	}

}
