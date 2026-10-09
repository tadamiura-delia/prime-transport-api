package fr.delia.primetransport.referentiel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MoyenTransportRepository extends JpaRepository<MoyenTransport, String> {

	List<MoyenTransport> findAllByOrderByOrdreAsc();

}
