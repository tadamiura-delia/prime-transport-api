package fr.delia.primetransport.common;

/**
 * Ce que demande la requête n'existe pas. Traduite en 404 RequeteInvalideException
 */
public class RessourceIntrouvableException extends RuntimeException {

	public RessourceIntrouvableException(String message) {
		super(message);
	}

}
