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
class SemaineTypeControllerTests {

	private static final String SEMAINE = """
			{ "jours": [
				{ "jour": 0, "lieuCode": "conges-ferie" },
				{ "jour": 1, "lieuCode": "agence-rennes", "transportCode": "velo" },
				{ "jour": 2, "lieuCode": "teletravail" },
				{ "jour": 3, "lieuCode": "agence-rennes", "transportCode": "covoiturage" },
				{ "jour": 4, "lieuCode": "teletravail" },
				{ "jour": 5, "lieuCode": "agence-nantes" },
				{ "jour": 6, "lieuCode": "agence-rennes", "transportCode": "velo" }
			] }
			""";

	@Autowired
	private MockMvcTester mvc;

	@Autowired
	private JourTypeRepository repository;

	@BeforeEach
	void viderLaTable() {
		repository.deleteAll();
	}

	@Test
	void findSemaineTypeRenvoie404SiJamaisEnregistree() {
		assertThat(mvc.get().uri("/api/me/semaine-type")).hasStatus(HttpStatus.NOT_FOUND);
	}

	@Test
	void enregistrerSemaineTypeComplete() {
		assertThat(mvc.put().uri("/api/me/semaine-type").contentType(MediaType.APPLICATION_JSON).content(SEMAINE))
			.hasStatus(HttpStatus.OK);

		assertThat(mvc.get().uri("/api/me/semaine-type")).hasStatus(HttpStatus.OK)
			.bodyJson()
			.extractingPath("$[*].jour")
			.asArray()
			.containsExactly(0, 1, 2, 3, 4, 5, 6);
	}

	@Test
	void enregistrerSemaineTypeForceLeSamediEnCongesFerie() {
		assertThat(mvc.put().uri("/api/me/semaine-type").contentType(MediaType.APPLICATION_JSON).content(SEMAINE))
			.hasStatus(HttpStatus.OK);

		assertThat(mvc.get().uri("/api/me/semaine-type")).hasStatus(HttpStatus.OK)
			.bodyJson()
			.extractingPath("$[?(@.jour == 6)].lieuCode")
			.asArray()
			.containsExactly("conges-ferie");

		assertThat(mvc.get().uri("/api/me/semaine-type")).hasStatus(HttpStatus.OK)
			.bodyJson()
			.extractingPath("$[?(@.jour == 6)].transportCode")
			.asArray()
			.containsOnlyNulls();
	}

	@Test
	void enregistrerDeuxFoisModifieSansDupliquer() {
		assertThat(mvc.put().uri("/api/me/semaine-type").contentType(MediaType.APPLICATION_JSON).content(SEMAINE))
			.hasStatus(HttpStatus.OK);

		assertThat(mvc.put().uri("/api/me/semaine-type").contentType(MediaType.APPLICATION_JSON).content("""
				{ "jours": [
					{ "jour": 0, "lieuCode": "conges-ferie" },
					{ "jour": 1, "lieuCode": "teletravail" },
					{ "jour": 2, "lieuCode": "teletravail" },
					{ "jour": 3, "lieuCode": "agence-rennes", "transportCode": "covoiturage" },
					{ "jour": 4, "lieuCode": "teletravail" },
					{ "jour": 5, "lieuCode": "agence-nantes" },
					{ "jour": 6, "lieuCode": "agence-rennes", "transportCode": "velo" }
				] }
				""")).hasStatus(HttpStatus.OK);

		assertThat(mvc.get().uri("/api/me/semaine-type")).hasStatus(HttpStatus.OK)
			.bodyJson()
			.extractingPath("$[?(@.jour == 1)].lieuCode")
			.asArray()
			.containsExactly("teletravail");

		assertThat(mvc.get().uri("/api/me/semaine-type")).hasStatus(HttpStatus.OK)
			.bodyJson()
			.extractingPath("$[?(@.jour == 1)].transportCode")
			.asArray()
			.containsOnlyNulls();
		assertThat(repository.count()).isEqualTo(7);

	}

	@ParameterizedTest
	@ValueSource(strings = { """
			{ "jours": [
			    { "jour": 0, "lieuCode": "conges-ferie" },
			    { "jour": 1, "lieuCode": "agence-rennes", "transportCode": "velo" },
			    { "jour": 2, "lieuCode": "teletravail" },
			    { "jour": 3, "lieuCode": "agence-rennes", "transportCode": "covoiturage" },
			    { "jour": 4, "lieuCode": "teletravail" },
			    { "jour": 5, "lieuCode": "agence-nantes" }
			] }
			""", """
			{ "jours": [
			    { "jour": 0, "lieuCode": "conges-ferie" },
			    { "jour": 1, "lieuCode": "agence-rennes", "transportCode": "velo" },
			    { "jour": 2, "lieuCode": "teletravail" },
			    { "jour": 3, "lieuCode": "agence-rennes", "transportCode": "covoiturage" },
			    { "jour": 4, "lieuCode": "teletravail" },
			    { "jour": 5, "lieuCode": "agence-nantes" },
			    { "jour": 7, "lieuCode": "agence-nantes" }
			] }
			""", """
			{ "jours": [
			    { "jour": 0, "lieuCode": "conges-ferie" },
			    { "jour": 1, "lieuCode": "", "transportCode": "velo" },
			    { "jour": 2, "lieuCode": "teletravail" },
			    { "jour": 3, "lieuCode": "agence-rennes", "transportCode": "covoiturage" },
			    { "jour": 4, "lieuCode": "teletravail" },
			    { "jour": 5, "lieuCode": "agence-nantes" },
			    { "jour": 6, "lieuCode": "agence-nantes" }
			] }
			""", """
				{ "jours": []}
			""" })
	void enregistrerSemaineTypeRefuseUneSemaineTypeInvalide(String body) {
		assertThat(mvc.put().uri("/api/me/semaine-type").contentType(MediaType.APPLICATION_JSON).content(body))
			.hasStatus(HttpStatus.BAD_REQUEST);

		assertThat(repository.count()).isZero();
	}

}
