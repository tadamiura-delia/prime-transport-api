package fr.delia.primetransport.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

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

	/** Un utilisateur connecté, avec le rôle user. */
	private static RequestPostProcessor utilisateur(String sub) {
		return jwt().jwt(jeton -> jeton.subject(sub)).authorities(new SimpleGrantedAuthority("ROLE_user"));
	}

	@Test
	void findDistancesRenvoieZeroPourUnNouvelUtilisateur() {
		assertThat(mvc.get().uri("/api/me/distances").with(utilisateur("alice"))).hasStatus(HttpStatus.OK)
			.bodyJson()
			.extractingPath("$[*].distanceKm")
			.asArray()
			.containsOnly(0);
	}

	@Test
	void enregistrerDistanceCreeLaDistance() {
		assertThat(mvc.put()
			.uri("/api/me/distances/{lieuCode}", "agence-rennes")
			.with(utilisateur("alice"))
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
			.with(utilisateur("alice"))
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
					{"distanceKm": 10}
					""")).hasStatus(HttpStatus.OK).bodyJson().isLenientlyEqualTo("""
					{"lieuCode": "agence-rennes", "distanceKm": 10}
					""");

		assertThat(repository.findByUtilisateurIdAndLieuCode("alice", "agence-rennes")).isPresent();

		assertThat(mvc.put()
			.uri("/api/me/distances/{lieuCode}", "agence-rennes")
			.with(utilisateur("alice"))
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
					{"distanceKm": 15}
					""")).hasStatus(HttpStatus.OK).bodyJson().isLenientlyEqualTo("""
					{"lieuCode": "agence-rennes", "distanceKm": 15}
					""");

		assertThat(repository.findByUtilisateurIdAndLieuCode("alice", "agence-rennes")).isPresent();

		assertThat(mvc.get().uri("/api/me/distances").with(utilisateur("alice"))).hasStatus(HttpStatus.OK)
			.bodyJson()
			.extractingPath("$[?(@.lieuCode == 'agence-rennes')].distanceKm")
			.asArray()
			.containsExactly(15.0);
	}

	@ParameterizedTest
	@ValueSource(strings = { "{ \"distanceKm\": -3}", "{\"distanceKm\": 1500}", "{}" })
	void enregistrerDistanceRefuseUneDistanceInvalide(String body) {
		assertThat(mvc.put()
			.uri("/api/me/distances/{lieuCode}", "agence-rennes")
			.with(utilisateur("alice"))
			.contentType(MediaType.APPLICATION_JSON)
			.content(body)).hasStatus(HttpStatus.BAD_REQUEST);

		assertThat(repository.count()).isZero();
	}

	@Test
	void findDistancesSansJetonRenvoie401() {
		assertThat(mvc.get().uri("/api/me/distances")).hasStatus(HttpStatus.UNAUTHORIZED);
	}

	@Test
	void findDistancesSansRoleUtilisateurRenvoie403() {
		assertThat(mvc.get().uri("/api/me/distances").with(jwt())).hasStatus(HttpStatus.FORBIDDEN);
	}

	@Test
	void enregistrerUneDistanceDoitEtreLisibleQueParSonUtilisateur() {
		assertThat(mvc.put()
			.uri("/api/me/distances/{lieuCode}", "agence-rennes")
			.with(utilisateur("alice"))
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
					{"distanceKm": 15}
					""")).hasStatus(HttpStatus.OK).bodyJson().isLenientlyEqualTo("""
					{"lieuCode": "agence-rennes", "distanceKm": 15}
					""");

		assertThat(mvc.get().uri("/api/me/distances").with(utilisateur("alice"))).hasStatus(HttpStatus.OK)
			.bodyJson()
			.extractingPath("$[?(@.lieuCode == 'agence-rennes')].distanceKm")
			.asArray()
			.containsExactly(15.0);

		assertThat(mvc.get().uri("/api/me/distances").with(utilisateur("bob"))).hasStatus(HttpStatus.OK)
			.bodyJson()
			.extractingPath("$[?(@.lieuCode == 'agence-rennes')].distanceKm")
			.asArray()
			.containsExactly(0);
	}

	@Test
	void enregistrerDistanceAvecUnLieuInconnuRenvoie404() {
		assertThat(mvc.put()
			.uri("/api/me/distances/{lieuCode}", "agence-renne")
			.with(utilisateur("alice"))
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
						{"distanceKm": 15}
					""")).hasStatus(HttpStatus.NOT_FOUND)
			.bodyJson()
			.extractingPath("$.detail")
			.asString()
			.contains("agence-renne");

		assertThat(repository.count()).isZero();
	}

}
