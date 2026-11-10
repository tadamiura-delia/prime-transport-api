package fr.delia.primetransport.configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.delia.primetransport.referentiel.LieuTravail;
import fr.delia.primetransport.referentiel.LieuTravailRepository;
import fr.delia.primetransport.referentiel.MoyenTransport;
import fr.delia.primetransport.referentiel.MoyenTransportRepository;

/**
 * Semaine type des déplacements saisie par chaque utilisateur.
 */
@Service
public class SemaineTypeService {

	private static final String LIEU_CONGES_FERIE = "conges-ferie";

	private final JourTypeRepository jourTypeRepository;

	private final MoyenTransportRepository moyenTransportRepository;

	private final LieuTravailRepository lieuTravailRepository;

	public SemaineTypeService(JourTypeRepository jourTypeRepository, MoyenTransportRepository moyenTransportRepository,
			LieuTravailRepository lieuTravailRepository) {
		this.jourTypeRepository = jourTypeRepository;
		this.moyenTransportRepository = moyenTransportRepository;
		this.lieuTravailRepository = lieuTravailRepository;
	}

	/** Lit la semaine dans la transaction de la méthode appelante. */
	private List<JourTypeDto> lireSemaine(String utilisateurId) {
		return jourTypeRepository.findByUtilisateurIdOrderByJourAsc(utilisateurId)
			.stream()
			.map(JourTypeDto::from)
			.toList();
	}

	/**
	 * Remplace la semaine type de l'utilisateur. Samedi et dimanche sont toujours
	 * enregistrés en congés/férié sans transport, quoi que contienne la requête.
	 * @throws IllegalArgumentException si un jour apparaît plusieurs fois
	 */

	@Transactional
	public List<JourTypeDto> enregistrerSemaineType(String utilisateurId, SemaineTypeRequest semaine) {

		long nombreDeJoursDifferents = semaine.jours().stream().map(JourTypeRequest::jour).distinct().count();

		if (nombreDeJoursDifferents != 7) {
			throw new IllegalArgumentException("Chaque jour de la semaine doit apparaître une fois");
		}

		Map<Short, JourType> joursExistants = jourTypeRepository.findByUtilisateurIdOrderByJourAsc(utilisateurId)
			.stream()
			.collect(Collectors.toMap(JourType::getJour, jourType -> jourType));

		for (JourTypeRequest jourRecu : semaine.jours()) {
			short jour = jourRecu.jour();
			boolean weekend = jour == 0 || jour == 6;

			String lieuCode = weekend ? LIEU_CONGES_FERIE : jourRecu.lieuCode();
			String transportCode = weekend ? null : jourRecu.transportCode();

			LieuTravail lieu = lieuTravailRepository.findById(lieuCode).orElseThrow();
			MoyenTransport moyenTransport = transportCode != null
					? moyenTransportRepository.findById(transportCode).orElseThrow() : null;

			JourType existant = joursExistants.get(jour);

			if (existant != null) {
				existant.modifier(lieu, moyenTransport);
			}
			else {
				jourTypeRepository.save(new JourType(utilisateurId, jour, lieu, moyenTransport));
			}

		}
		return lireSemaine(utilisateurId);
	}

}
