package fr.delia.primetransport.configuration;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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

	private final DistanceLieuService service;

	public DistanceLieuController(DistanceLieuService service) {
		this.service = service;
	}

	@GetMapping
	public List<DistanceLieuDto> findDistances(@AuthenticationPrincipal Jwt jwt) {
		return service.findDistances(jwt.getSubject());
	}

	@PutMapping("/{lieuCode}")
	public DistanceLieuDto enregistrerDistance(@PathVariable String lieuCode,
			@Valid @RequestBody DistanceLieuRequest request, @AuthenticationPrincipal Jwt jwt) {
		return service.enregistrerDistance(jwt.getSubject(), lieuCode, request.distanceKm());
	}

}
