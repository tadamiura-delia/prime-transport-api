package fr.delia.primetransport.configuration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.delia.primetransport.referentiel.LieuTravail;
import fr.delia.primetransport.referentiel.LieuTravailRepository;

/** Distances domicile-lieu de travail saisies par chaque utilisateur. */
@Service
public class DistanceLieuService {

	private final DistanceLieuRepository distanceLieuRepository;

	private final LieuTravailRepository lieuTravailRepository;

	public DistanceLieuService(DistanceLieuRepository distanceLieuRepository,
			LieuTravailRepository lieuTravailRepository) {
		this.distanceLieuRepository = distanceLieuRepository;
		this.lieuTravailRepository = lieuTravailRepository;
	}

	/**
	 * Tous les lieux, avec la distance de l'utilisateur, ou 0 s'il ne l'a jamais saisie.
	 */
	@Transactional(readOnly = true)
	public List<DistanceLieuDto> findDistances(String utilisateurId) {
		List<LieuTravail> lieux = lieuTravailRepository.findAllByOrderByOrdreAsc();
		Map<String, BigDecimal> distanceParLieu = distanceLieuRepository.findAllByUtilisateurId(utilisateurId)
			.stream()
			.collect(Collectors.toMap(distance -> distance.getLieu().getCode(), DistanceLieu::getDistanceKm));

		return lieux.stream()
			.map(lieu -> DistanceLieuDto.from(lieu, distanceParLieu.getOrDefault(lieu.getCode(), BigDecimal.ZERO)))
			.toList();
	}

	@Transactional
	public DistanceLieuDto enregistrerDistance(String utilisateurId, String lieuCode, BigDecimal distanceKm) {
		Optional<DistanceLieu> existante = distanceLieuRepository.findByUtilisateurIdAndLieuCode(utilisateurId,
				lieuCode);
		DistanceLieu distanceLieu;
		if (existante.isPresent()) {
			distanceLieu = existante.get();
			distanceLieu.setDistanceKm(distanceKm);
		}
		else {
			LieuTravail lieu = lieuTravailRepository.findById(lieuCode).orElseThrow();
			distanceLieu = distanceLieuRepository.save(new DistanceLieu(utilisateurId, lieu, distanceKm));
		}

		return DistanceLieuDto.from(distanceLieu.getLieu(), distanceLieu.getDistanceKm());
	}

}
