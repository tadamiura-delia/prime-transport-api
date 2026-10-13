package fr.delia.primetransport.configuration;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DistanceLieuRepository extends JpaRepository<DistanceLieu, Long> {
    List<DistanceLieu> findAllByUtilisateurId(String utilisateurId);

    Optional<DistanceLieu> findByUtilisateurIdAndLieuCode(String utilisateurId, String lieuCode);
}
