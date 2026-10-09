package fr.delia.primetransport.referentiel;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Référentiels en lecture, pour tous les utilisateurs. */
@RestController
@RequestMapping("/api/referentiels")
public class ReferentielController {

	private final ReferentielService service;

	public ReferentielController(ReferentielService service) {
		this.service = service;
	}

	@GetMapping("/lieux")
	public List<LieuTravailDto> findLieux() {
		return service.findLieux();
	}

	@GetMapping("/transports")
	public List<MoyenTransportDto> findTransports() {
		return service.findTransports();
	}

}
