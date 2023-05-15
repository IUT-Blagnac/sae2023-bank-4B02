package model.data;

/*
 * Attributs mis en public car cette classe ne fait que "véhiculer" des données.
 * 
 * La classe CompteCourant représente un compte courant bancaire.
 */
public class CompteCourant {

    //L'identifiant unique du compte courant
    public int idNumCompte;

    // Le montant autorisé de découvert pour ce compte courant
    public int debitAutorise;

    // Le solde actuel du compte courant
    public double solde;

    /**
     * Indique si le compte courant est clôturé ou non.
     * "O" signifie "oui", c'est-à-dire qu'il est clôturé.
     * "N" signifie "non", c'est-à-dire qu'il est ouvert.
     */
    public String estCloture;

    /** L'identifiant du client propriétaire de ce compte courant. */
    public int idNumCli;

    /**
     * Constructeur de la classe CompteCourant.
     * @param idNumCompte l'identifiant unique du compte courant
     * @param debitAutorise le montant autorisé de découvert pour ce compte courant
     * @param solde le solde actuel du compte courant
     * @param estCloture indique si le compte courant est clôturé ou non
     * @param idNumCli l'identifiant du client propriétaire de ce compte courant
     */
    public CompteCourant(int idNumCompte, int debitAutorise, double solde, String estCloture, int idNumCli) {
        this.idNumCompte = idNumCompte;
        this.debitAutorise = debitAutorise;
        this.solde = solde;
        this.estCloture = estCloture;
        this.idNumCli = idNumCli;
    }

    /**
     * Constructeur par copie de la classe CompteCourant.
     * @param cc le CompteCourant à copier
     */
    public CompteCourant(CompteCourant cc) {
        this(cc.idNumCompte, cc.debitAutorise, cc.solde, cc.estCloture, cc.idNumCli);
    }

    /**
     * Constructeur sans paramètres de la classe CompteCourant.
     * Initialise les attributs à des valeurs par défaut.
     */
    public CompteCourant() {
        this(0, 0, 0, "N", -1000);
    }

    /**
     * Retourne une chaîne de caractères représentant le compte courant.
     * @return une chaîne de caractères représentant le compte courant
     */
    @Override
    public String toString() {
        String s = "" + String.format("%05d", this.idNumCompte) + " : Solde=" + String.format("%12.02f", this.solde)
                + "  ,  Découvert Autorise=" + String.format("%8d", this.debitAutorise);
        if (this.estCloture == null) {
            s = s + " (Cloture)";
        } else {
            s = s + (this.estCloture.equals("N") ? " (Ouvert)" : " (Cloture)");
        }
        return s;
    }

}
