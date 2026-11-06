package fr.delia.primetransport.configuration;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JourTypeRepository extends JpaRepository<JourType, Long> {

	/** Les jours type de l'utilisateur, du dimanche (0) au samedi (6). */
	List<JourType> findByUtilisateurIdOrderByJourAsc(String utilisateurId);

}
