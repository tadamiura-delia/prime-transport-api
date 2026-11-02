package fr.delia.primetransport.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import fr.delia.primetransport.TestcontainersConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class DistanceLieuControllerTests {

	@Autowired
	private MockMvcTester mvc;

	@Autowired
	private DistanceLieuRepository repository;

	@BeforeEach
	void viderLaTable() {
		repository.deleteAll();
	}

	@Test
	void findDistancesRenvoieZeroPourUnNouvelUtilisateur() {
		assertThat(mvc.get().uri("/api/me/distances")).hasStatus(HttpStatus.OK)
			.bodyJson()
			.extractingPath("$[*].distanceKm")
			.asArray()
			.containsOnly(0);
	}

	@Test
	void enregistrerDistanceCreeLaDistance() {
		assertThat(mvc.put()
			.uri("/api/me/distances/{lieuCode}", "agence-rennes")
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
					{"distanceKm": 13.5}
					""")).hasStatus(HttpStatus.OK).bodyJson().isLenientlyEqualTo("""
						{
					"lieuCode" : "agence-rennes",
					"distanceKm": 13.5
					}
						""");

		assertThat(repository.findByUtilisateurIdAndLieuCode("alice", "agence-rennes")).isPresent();
	}

	@Test
	void enregistrerDistanceModifieLaDistance() {
		assertThat(mvc.put()
			.uri("/api/me/distances/{lieuCode}", "agence-rennes")
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
					{"distanceKm": 10}
					""")).hasStatus(HttpStatus.OK).bodyJson().isLenientlyEqualTo("""
					{"lieuCode": "agence-rennes", "distanceKm": 10}
					""");

		assertThat(repository.findByUtilisateurIdAndLieuCode("alice", "agence-rennes")).isPresent();

		assertThat(mvc.put()
			.uri("/api/me/distances/{lieuCode}", "agence-rennes")
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
					{"distanceKm": 15}
					""")).hasStatus(HttpStatus.OK).bodyJson().isLenientlyEqualTo("""
					{"lieuCode": "agence-rennes", "distanceKm": 15}
					""");

		assertThat(repository.findByUtilisateurIdAndLieuCode("alice", "agence-rennes")).isPresent();

		assertThat(mvc.get().uri("/api/me/distances")).hasStatus(HttpStatus.OK)
			.bodyJson()
			.extractingPath("$[?(@.lieuCode == 'agence-rennes')].distanceKm")
			.asArray()
			.containsExactly(15.0);
	}

	// @Test
	// void enregistrerDistanceRefuseUneDistanceNegative() {
	// assertThat(mvc.put()
	// .uri("/api/me/distances/{lieuCode", "agence-rennes")
	// .contentType(MediaType.APPLICATION_JSON)
	// .content("""
	// {"distanceKm": -3}
	// """)).hasStatus(HttpStatus.BAD_REQUEST);

	// assertThat(repository.count()).isZero();
	// }

	// @Test
	// void enregistrerDistanceRefuseUneDistanceTropGrande() {
	// assertThat(mvc.put()
	// .uri("/api/me/distances/{lieuCode}", "agence-rennes")
	// .contentType(MediaType.APPLICATION_JSON)
	// .content("""
	// {"distanceKm": 1200}
	// """)).hasStatus(HttpStatus.BAD_REQUEST);

	// assertThat(repository.count()).isZero();
	// }

	// @Test
	// void enregistrerDistanceRefuseUneDistanceAbsente() {
	// assertThat(mvc.put()
	// .uri("/api/me/distances/{lieuCode}", "agence-rennes")
	// .contentType(MediaType.APPLICATION_JSON)
	// .content("""
	// {}
	// """)).hasStatus(HttpStatus.BAD_REQUEST);

	// assertThat(repository.count()).isZero();
	// }

	@ParameterizedTest
	@ValueSource(strings = { "{ \"distanceKm\": -3}", "{\"distanceKm\": 1500}", "{}" })
	void enregistrerDistanceRefuseUneDistanceInvalide(String body) {
		assertThat(mvc.put()
			.uri("/api/me/distances/{lieuCode}", "agence-rennes")
			.contentType(MediaType.APPLICATION_JSON)
			.content(body)).hasStatus(HttpStatus.BAD_REQUEST);

		assertThat(repository.count()).isZero();
	}

}
