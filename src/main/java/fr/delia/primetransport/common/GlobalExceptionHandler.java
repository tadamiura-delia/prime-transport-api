package fr.delia.primetransport.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Traduit les exceptions métier en réponses HTTP au format ProblemDetail (RFC 9457). */
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Retourne une 404 Not Found
	 * @param exception
	 * @return
	 */
	@ExceptionHandler(RessourceIntrouvableException.class)
	ProblemDetail ressourceIntrouvable(RessourceIntrouvableException exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	/**
	 * Retourne une 401 Bad Request
	 * @param exception
	 * @return
	 */
	@ExceptionHandler(RequeteInvalideException.class)
	ProblemDetail requeteInvalide(RequeteInvalideException exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

}
