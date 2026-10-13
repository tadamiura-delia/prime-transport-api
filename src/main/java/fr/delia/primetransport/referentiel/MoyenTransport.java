package fr.delia.primetransport.referentiel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Moyen de transport déclarable. « Aucun » n'en est pas un : c'est l'absence de valeur.
 */
@Entity
@Table(name = "moyen_transport")
public class MoyenTransport {

	@Id
	private String code;

	@Column(nullable = false)
	private String libelle;

	/** Position dans les listes déroulantes, croissante. */
	@Column(nullable = false)
	private int ordre;

	/** Requis par JPA. */
	protected MoyenTransport() {
	}

	public MoyenTransport(String code, String libelle, int ordre) {
		this.code = code;
		this.libelle = libelle;
		this.ordre = ordre;
	}

	public String getCode() {
		return code;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public int getOrdre() {
		return ordre;
	}

	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

}
