package fr.delia.primetransport.referentiel;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Lecture des référentiels : lieux de travail et moyens de transport. */
@Service
@Transactional(readOnly = true)
public class ReferentielService {

	private final LieuTravailRepository lieuRepository;

	private final MoyenTransportRepository transportRepository;

	/** Injection par constructeur : Spring fournit les repositories. */
	public ReferentielService(LieuTravailRepository lieuRepository, MoyenTransportRepository transportRepository) {
		this.lieuRepository = lieuRepository;
		this.transportRepository = transportRepository;
	}

	public List<LieuTravailDto> findLieux() {
		return lieuRepository.findAllByOrderByOrdreAsc().stream().map(LieuTravailDto::from).toList();
	}

	public List<MoyenTransportDto> findTransports() {
		return transportRepository.findAllByOrderByOrdreAsc().stream().map(MoyenTransportDto::from).toList();
	}

}
