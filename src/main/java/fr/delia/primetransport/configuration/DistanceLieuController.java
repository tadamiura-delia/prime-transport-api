package fr.delia.primetransport.configuration;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/me/distances")
public class DistanceLieuController {

	private static final String UTILISATEUR_PROVISOIRE = "alice";

	private final DistanceLieuService service;

	public DistanceLieuController(DistanceLieuService service) {
		this.service = service;
	}

	@GetMapping
	public List<DistanceLieuDto> findDistances() {
		return service.findDistances(UTILISATEUR_PROVISOIRE);
	}

	@PutMapping("/{lieuCode}")
	public DistanceLieuDto enregistrerDistance(@PathVariable String lieuCode,
			@Valid @RequestBody DistanceLieuRequest request) {
		return service.enregistrerDistance(UTILISATEUR_PROVISOIRE, lieuCode, request.distanceKm());
	}

}
