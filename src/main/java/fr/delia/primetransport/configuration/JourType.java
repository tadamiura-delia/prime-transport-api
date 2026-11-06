package fr.delia.primetransport.configuration;

import fr.delia.primetransport.referentiel.LieuTravail;
import fr.delia.primetransport.referentiel.MoyenTransport;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Représente le jour type avec son moyen de transport et son lieu de travail
 */
@Entity
@Table(name = "jour_type")
public class JourType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long id;

	@Column(nullable = false, updatable = false)
	private String utilisateurId;

	@Column(nullable = false, updatable = false)
	private short jour;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "lieu_code", nullable = false)
	private LieuTravail lieu;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "transport_code")
	private MoyenTransport transport;

	protected JourType() {
	}

	public JourType(String utilisateurId, short jour, LieuTravail lieu, MoyenTransport transport) {
		this.utilisateurId = utilisateurId;
		this.jour = jour;
		this.lieu = lieu;
		this.transport = transport;
	}

	public Long getId() {
		return id;
	}

	public String getUtilisateurId() {
		return utilisateurId;
	}

	public short getJour() {
		return jour;
	}

	public LieuTravail getLieu() {
		return lieu;
	}

	public MoyenTransport getTransport() {
		return transport;
	}

	public void modifier(LieuTravail lieu, MoyenTransport transport) {
		this.lieu = lieu;
		this.transport = transport;
	}

}
