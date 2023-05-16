package model.data;

/*
 * Attributs mis en public car cette classe ne fait que "véhiculer" des données.
 * 
 * La classe Client représente un client de la banque.
 */
public class Client {

	// Identifiant unique du client.
	public int idNumCli;
	
	// Nom du client.
	public String nom;
	
	// Prénom du client.
	public String prenom;
	
	// Adresse postale du client.
	public String adressePostale;
	
	// Adresse e-mail du client.
	public String email;
	
	// Numéro de téléphone du client.
	public String telephone;
	
	// Indique si le client est inactif ("Oui" ou "Non").
	public String estInactif;

	// Identifiant unique de l'agence à laquelle le client est rattaché.
	public int idAg;

	/**
	 * Constructeur de la classe Client.
	 * @param idNumCli Identifiant unique du client.
	 * @param nom Nom du client.
	 * @param prenom Prénom du client.
	 * @param adressePostale Adresse postale du client.
	 * @param email Adresse e-mail du client.
	 * @param telephone Numéro de téléphone du client.
	 * @param estInactif Indique si le client est inactif ("Oui" ou "Non").
	 * @param idAg Identifiant unique de l'agence à laquelle le client est rattaché.
	 */
	public Client(int idNumCli, String nom, String prenom, String adressePostale, String email, String telephone,
			String estInactif, int idAg) {
		super();
		this.idNumCli = idNumCli;
		this.nom = nom;
		this.prenom = prenom;
		this.adressePostale = adressePostale;
		this.email = email;
		this.telephone = telephone;
		this.estInactif = estInactif;
		this.idAg = idAg;
	}

	/**
	 * Constructeur par copie de la classe Client.
	 * @param c Client à copier.
	 */
	public Client(Client c) {
		this(c.idNumCli, c.nom, c.prenom, c.adressePostale, c.email, c.telephone, c.estInactif, c.idAg);
	}

	/**
	 * Constructeur par défaut de la classe Client.
	 */
	public Client() {
		this(-1000, null, null, null, null, null, "Non", -1000);
	}

	/**
	 * Retourne une chaîne de caractères représentant le client.
	 * @return Chaîne de caractères représentant le client.
	 */
	@Override
	public String toString() {
		return "Identifiant : " + this.idNumCli + " | " + this.nom.toUpperCase() + " " + this.prenom + " | " + this.email + " | "
				+ this.telephone;
	}

}

