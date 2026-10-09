package fr.delia.primetransport.referentiel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LieuTravailRepository extends JpaRepository<LieuTravail, String> {

	/** Requête dérivée du nom de la méthode : {@code ORDER BY ordre ASC}. */
	List<LieuTravail> findAllByOrderByOrdreAsc();

}
