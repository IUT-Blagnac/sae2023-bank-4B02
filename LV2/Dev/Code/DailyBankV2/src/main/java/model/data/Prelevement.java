package model.data;

/**
 * @author KHALIL Ahmad
 * 
 * Cette classe représente un prélèvement.
 */
public class Prelevement {

    public int idprelev; // Identifiant du prélèvement
    public double montant; // Montant du prélèvement
    public int dateRecurrente; // Date récurrente du prélèvement
    public String beneficiaire; // Bénéficiaire du prélèvement
    public int idNumCompte; // Identifiant du compte associé au prélèvement
	
	/**
     * Constructeur par défaut de la classe Prelevement.
     * Initialise toutes les variables membres à leurs valeurs par défaut.
     */
	public Prelevement() {
		this.idprelev = 0;
		this.montant = 0;
		this.dateRecurrente = 0;
		this.beneficiaire = null;
		this.idNumCompte = 0;
	}
	
	/**
     * Constructeur de la classe Prelevement avec certains paramètres.
     * Initialise les variables membres avec les valeurs fournies en paramètre.
     * 
     * @param pfMontant le montant du prélèvement
     * @param pfDate la date récurrente du prélèvement
     * @param pfBenef le bénéficiaire du prélèvement
     */
	public Prelevement(double pfMontant, int pfDate, String pfBenef) {
		this.montant = pfMontant;
		this.dateRecurrente = pfDate;
		this.beneficiaire = pfBenef;
	}
	
	/**
     * Constructeur de la classe Prelevement avec tous les paramètres.
     * Initialise les variables membres avec les valeurs fournies en paramètre.
     * 
     * @param pfIdPrelev l'identifiant du prélèvement
     * @param pfMontant le montant du prélèvement
     * @param pfDate la date récurrente du prélèvement
     * @param pfBenef le bénéficiaire du prélèvement
     * @param pfIdNumCompte l'identifiant du compte associé au prélèvement
     */
	public Prelevement(int pfIdPrelev, double pfMontant, int pfDate, String pfBenef, int pfIdNumCompte) {
		this.idprelev = pfIdPrelev;
		this.montant = pfMontant;
		this.dateRecurrente = pfDate;
		this.beneficiaire = pfBenef;
		this.idNumCompte = pfIdNumCompte;
	}
	
	/**
     * Retourne une représentation sous forme de chaîne de caractères de l'objet Prelevement.
     * 
     * @return la représentation sous forme de chaîne de caractères de l'objet Prelevement
     */
	public String toString() {
		return "N°Prélèvement : " + this.idprelev + " | Montant : " + this.montant + " | Date d'exécution : " + this.dateRecurrente
		+ " | Bénéficiaire : " + this.beneficiaire + " | N°Compte : " + this.idNumCompte;
	}
}
