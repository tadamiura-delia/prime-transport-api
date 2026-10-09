package fr.delia.primetransport.referentiel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Lieu de travail proposé aux collaborateurs. */
@Entity
@Table(name = "lieu_travail")
public class LieuTravail {

	@Id
	private String code;

	@Column(nullable = false)
	private String libelle;

	/** Position dans les listes déroulantes, croissante. */
	@Column(nullable = false)
	private int ordre;

	/** Lieu dont l'application dépend (samedi et dimanche) : il ne peut pas être supprimé. */
	@Column(nullable = false)
	private boolean systeme;

	/** Requis par JPA. */
	protected LieuTravail() {
	}

	public LieuTravail(String code, String libelle, int ordre) {
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

	public boolean isSysteme() {
		return systeme;
	}

}
