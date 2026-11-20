package fr.delia.primetransport.common;

/**
 * La requête enfreint une règle métier. Traduite en 400. RequeteInvalideException
 */
public class RequeteInvalideException extends RuntimeException {

	public RequeteInvalideException(String message) {
		super(message);
	}

}
