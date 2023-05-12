package model.data;

/*
 * Attributs mis en public car cette classe ne fait que "véhiculer" des données.
 * 
 * La classe TypeOperation représente un type d'opération bancaire.
 */
public class TypeOperation {

    /**
     * L'identifiant du type d'opération.
     */
    public String idTypeOp;

    /**
     * Constructeur de la classe TypeOperation prenant en paramètre un identifiant de type d'opération.
     * @param idTypeOp l'identifiant du type d'opération.
     */
    public TypeOperation(String idTypeOp) {
        super();
        this.idTypeOp = idTypeOp;
    }

    /**
     * Constructeur de copie de la classe TypeOperation.
     * @param to l'instance à copier.
     */
    public TypeOperation(TypeOperation to) {
        this(to.idTypeOp);
    }

    /**
     * Constructeur sans paramètre de la classe TypeOperation.
     */
    public TypeOperation() {
        this((String) null);
    }

    /**
     * Renvoie une représentation sous forme de chaîne de caractères de l'instance TypeOperation.
     * @return une chaîne de caractères représentant l'instance TypeOperation.
     */
    @Override
    public String toString() {
        return "TypeOperation [idTypeOp=" + this.idTypeOp + "]";
    }
}

