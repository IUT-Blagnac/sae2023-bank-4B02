package model.data;

/*
 * Attributs mis en public car cette classe ne fait que "véhiculer" des données.
 * 
 * Classe représentant un employé de la banque.
 */

public class Employe {

    // Identifiant unique de l'employé.
    public int idEmploye;

    // Nom de l'employé.
    public String nom;

    // Prénom de l'employé.
    public String prenom;

    // Droits d'accès de l'employé.
    public String droitsAccess;

    // Login de l'employé.
    public String login;

    // Mot de passe de l'employé.
    public String motPasse;

    // Identifiant de l'agence à laquelle est rattaché l'employé.
    public int idAg;
	
	/**
     * Constructeur avec paramètres.
     *
     * @param idEmploye    Identifiant unique de l'employé.
     * @param nom          Nom de l'employé.
     * @param prenom       Prénom de l'employé.
     * @param droitsAccess Droits d'accès de l'employé.
     * @param login        Login de l'employé.
     * @param motPasse     Mot de passe de l'employé.
     * @param idAg         Identifiant de l'agence à laquelle est rattaché l'employé.
     */
	public Employe(int idEmploye, String nom, String prenom, String droitsAccess, String login, String motPasse,
			int idAg) {
		super();
		this.idEmploye = idEmploye;
		this.nom = nom;
		this.prenom = prenom;
		this.droitsAccess = droitsAccess;
		this.login = login;
		this.motPasse = motPasse;
		this.idAg = idAg;
	}

	 /**
     * Constructeur par copie.
     *
     * @param e Objet Employe à copier.
     */
	public Employe(Employe e) {
		this(e.idEmploye, e.nom, e.prenom, e.droitsAccess, e.login, e.motPasse, e.idAg);
	}
	
	 /**
     * Constructeur par défaut.
     */
	public Employe() {
		this(-1000, null, null, null, null, null, -1000);
	}
	
	/**
     * Redéfinition de la méthode toString pour afficher les informations de l'employé.
     *
     * @return Les informations de l'employé sous forme de chaîne de caractères.
     */
	@Override
	public String toString() {
		return "Employe [idEmploye=" + this.idEmploye + ", nom=" + this.nom + ", prenom=" + this.prenom
				+ ", droitsAccess=" + this.droitsAccess + ", login=" + this.login + ", motPasse=" + this.motPasse
				+ ", idAg=" + this.idAg + "]";
	}

}
