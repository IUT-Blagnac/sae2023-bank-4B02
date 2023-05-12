package model.data;

/*
 * Attributs mis en public car cette classe ne fait que "véhiculer" des données.
 * 
 * La classe AgenceBancaire représente une agence bancaire contenant des comptes et des clients.
 * Elle contient un identifiant unique, un nom, une adresse postale et l'identifiant de l'employé chef.
 */

public class AgenceBancaire {

	public int idAg;

	public String nomAg;
	public String adressePostaleAg;
	public int idEmployeChefAg;
	
	/**
	 * Constructeur d'une instance de la classe AgenceBancaire avec tous les paramètres.
	 *
	 * @param idAg l'identifiant unique de l'agence bancaire
	 * @param nomAg le nom de l'agence bancaire
	 * @param adressePostaleAg l'adresse postale de l'agence bancaire
	 * @param idEmployeChefAg l'identifiant de l'employé chef de l'agence bancaire
	 */
	public AgenceBancaire(int idAg, String nomAg, String adressePostaleAg, int idEmployeChefAg) {
		super();
		this.idAg = idAg;
		this.nomAg = nomAg;
		this.adressePostaleAg = adressePostaleAg;
		this.idEmployeChefAg = idEmployeChefAg;
	}
	
	/**
	 * Constructeur d'une instance de la classe AgenceBancaire à partir d'une autre instance de la classe AgenceBancaire.
	 *
	 * @param ag l'instance de la classe AgenceBancaire à copier
	 */
	public AgenceBancaire(AgenceBancaire ag) {
		this(ag.idAg, ag.nomAg, ag.adressePostaleAg, ag.idEmployeChefAg);
	}
	
	/**
	 * Constructeur d'une instance de la classe AgenceBancaire avec des valeurs par défaut.
	 */
	public AgenceBancaire() {
		this(-1000, null, null, -1000);
	}
	
	/**
	 * Retourne une représentation sous forme de chaîne de caractères de l'instance de la classe AgenceBancaire.
	 *
	 * @return une chaîne de caractères représentant l'instance de la classe AgenceBancaire
	 */
	@Override
	public String toString() {
		return "AgenceBancaire [idAg=" + this.idAg + ", nomAg=" + this.nomAg + ", adressePostaleAg="
				+ this.adressePostaleAg + ", idEmployeChefAg=" + this.idEmployeChefAg + "]";
	}
}
