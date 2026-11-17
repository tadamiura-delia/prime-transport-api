package fr.delia.primetransport.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Toutes les routes exigent un JWT émis par Keycloak, sauf la santé et la documentation
 * de l'API. Les routes {@code /api/me/**} exigent en plus le rôle {@code user}.
 */
@Configuration
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) {
		// CSRF désactivé : l'API est sans état et authentifiée par un jeton dans
		// l'en-tête, jamais par un cookie que le navigateur enverrait tout seul.
		return http.csrf(csrf -> csrf.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/actuator/health", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
				.permitAll()
				.requestMatchers("/api/me/**")
				.hasRole("user")
				.anyRequest()
				.authenticated())
			.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
			.build();
	}

	/**
	 * Convertit les rôles du realm Keycloak ({@code realm_access.roles}) en rôles Spring
	 * ({@code ROLE_...}).
	 */
	@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(SecurityConfig::rolesDuRealm);
		return converter;
	}

	private static Collection<GrantedAuthority> rolesDuRealm(Jwt jwt) {
		Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
		if (realmAccess == null || !(realmAccess.get("roles") instanceof Collection<?> roles)) {
			return List.of();
		}
		return AuthorityUtils.createAuthorityList(roles.stream().map(role -> "ROLE_" + role).toList());
	}

}
