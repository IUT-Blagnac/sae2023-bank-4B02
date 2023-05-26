package model.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Attributs mis en public car cette classe ne fait que "véhiculer" des données.
 * 
 * Classe représentant une opération bancaire.
 * Les attributs sont publics car cette classe ne fait que "véhiculer" des données.
 */
public class Operation {

    /** Identifiant de l'opération */
    public int idOperation;

    /** Montant de l'opération */
    public double montant;

    /** Date de l'opération */
    public Date dateOp;

    /** Date de valeur de l'opération */
    public Date dateValeur;

    /** Identifiant du numéro de compte associé à l'opération */
    public int idNumCompte;

    /** Identifiant du type d'opération */
    public String idTypeOp;

    /**
     * Constructeur de la classe Operation.
     * @param idOperation Identifiant de l'opération.
     * @param montant Montant de l'opération.
     * @param dateOp Date de l'opération.
     * @param dateValeur Date de valeur de l'opération.
     * @param idNumCompte Identifiant du numéro de compte associé à l'opération.
     * @param idTypeOp Identifiant du type d'opération.
     */
    public Operation(int idOperation, double montant, Date dateOp, Date dateValeur, int idNumCompte, String idTypeOp) {
        this.idOperation = idOperation;
        this.montant = montant;
        this.dateOp = dateOp;
        this.dateValeur = dateValeur;
        this.idNumCompte = idNumCompte;
        this.idTypeOp = idTypeOp;
    }

    /**
     * Constructeur de copie de la classe Operation.
     * @param o Objet Operation à copier.
     */
    public Operation(Operation o) {
        this(o.idOperation, o.montant, o.dateOp, o.dateValeur, o.idNumCompte, o.idTypeOp);
    }

    /**
     * Constructeur par défaut de la classe Operation.
     * L'identifiant de l'opération est initialisé à -1000.
     * Le montant est initialisé à 0.
     * Les dates sont initialisées à null.
     * L'identifiant du numéro de compte est initialisé à -1000.
     * L'identifiant du type d'opération est initialisé à null.
     */
    public Operation() {
        this(-1000, 0, null, null, -1000, null);
    }

    /**
     * Renvoie une chaîne de caractères représentant l'opération.
     * @return Une chaîne de caractères représentant l'opération.
     */
    @Override
    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return "Type : " + this.idTypeOp + "  |  Montant : " + this.montant + "  |  Date : " + dateFormat.format(this.dateOp);
    }
}
