package fr.delia.primetransport.referentiel;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import fr.delia.primetransport.TestcontainersConfiguration;

/**
 * Test d'intégration : vraie base Postgres (Testcontainers), migrations Flyway incluses.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ReferentielControllerTests {

	@Autowired
	private MockMvcTester mvc;

	@Test
	void findLieuxRenvoieLesSixLieuxDansLOrdre() {
		assertThat(mvc.get().uri("/api/referentiels/lieux")).hasStatus(HttpStatus.OK)
			.bodyJson()
			.extractingPath("$[*].code")
			.asArray()
			.containsExactly("teletravail", "conges-ferie", "agence-rennes", "agence-nantes", "mission-1", "mission-2");
	}

	@Test
	void findLieuxSignaleLeLieuSysteme() {
		assertThat(mvc.get().uri("/api/referentiels/lieux")).bodyJson()
			.extractingPath("$[?(@.systeme == true)].code")
			.asArray()
			.containsExactly("conges-ferie");
	}

	@Test
	void findTransportsRenvoieVeloEtCovoiturage() {
		assertThat(mvc.get().uri("/api/referentiels/transports")).hasStatus(HttpStatus.OK)
			.bodyJson()
			.extractingPath("$[*].code")
			.asArray()
			.containsExactly("velo", "covoiturage");
	}

}
