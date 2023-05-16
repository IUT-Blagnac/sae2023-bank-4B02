package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.EmployeManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Employe;
import model.orm.Access_BD_Employe;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class EmployeManagement {
	private Stage primaryStage;
    private DailyBankState dailyBankState;
    private EmployeManagementController cmcViewController;

    /**
     * Constructeur de la classe EmployeManagement.
     * 
     * @param _parentStage la fenêtre parente
     * @param _dbstate     l'état de l'application
     */
    public EmployeManagement(Stage _parentStage, DailyBankState _dbstate) {
        this.dailyBankState = _dbstate;
        try {
            // Chargement du fichier FXML pour la gestion des employes
            FXMLLoader loader = new FXMLLoader(EmployeManagementController.class.getResource("employemanagement.fxml"));
            BorderPane root = loader.load();

            // Création de la scène
            Scene scene = new Scene(root, root.getPrefWidth() + 50, root.getPrefHeight() + 10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            // Création de la fenêtre
            this.primaryStage = new Stage();
            this.primaryStage.initModality(Modality.WINDOW_MODAL);
            this.primaryStage.initOwner(_parentStage);
            StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
            this.primaryStage.setScene(scene);
            this.primaryStage.setTitle("Gestion des employés");
            this.primaryStage.setResizable(false);

            // Récupération du contrôleur de la vue pour la gestion des employes
            this.cmcViewController = loader.getController();
            this.cmcViewController.initContext(this.primaryStage, this, _dbstate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affichage de la fenêtre de gestion des employes.
     */
    public void doEmployeManagementDialog() {
        this.cmcViewController.displayDialog();
    }
    
    /**
     * Récupère une liste d'employés en fonction des critères spécifiés.
     *
     * Cette méthode effectue une recherche d'employés en fonction des critères spécifiés : numéro d'employé, début de nom et début de prénom.
     * Si un numéro d'employé est spécifié, la recherche est effectuée en fonction de ce numéro d'employé.
     * Si aucun numéro d'employé n'est spécifié et un début de nom est spécifié, la recherche est effectuée en fonction du nom et du prénom.
     * Si aucun critère de recherche n'est spécifié, tous les employés sont récupérés.
     * Les employés correspondants sont récupérés à partir de la base de données.
     * En cas d'erreur de connexion à la base de données ou d'exception de l'application, une fenêtre d'exception est affichée.
     * Si une erreur de connexion se produit, l'application est fermée.
     * La méthode retourne la liste d'employés récupérés.
     *
     * @param _numEmploye Le numéro de l'employé (utiliser -1 si non spécifié)
     * @param _debutNom Le début du nom de l'employé (utiliser une chaîne vide si non spécifié)
     * @param _debutPrenom Le début du prénom de l'employé (utiliser une chaîne vide si non spécifié)
     * @return La liste des employés correspondants aux critères spécifiés
     */
	public ArrayList<Employe> getlisteEmploye(int _numEmploye, String _debutNom, String _debutPrenom) {
	    ArrayList<Employe> listeEmpl = new ArrayList<>();
	    try {
	        // Recherche des employes en BD. cf. Accessemploye > getemployes(.)
	        // numCompte != -1 => recherche sur numCompte
	        // numCompte == -1 et debutNom non vide => recherche nom/prenom
	        // numCompte == -1 et debutNom vide => recherche tous les employes
	        Access_BD_Employe ac = new Access_BD_Employe();
	        listeEmpl = ac.getlisteEmploye(_numEmploye, _debutNom, _debutPrenom);

	    } catch (DatabaseConnexionException e) {
	        // Si une erreur de connexion à la base de données est levée, affiche une fenêtre d'exception et ferme l'application
	        ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
	        ed.doExceptionDialog();
	        this.primaryStage.close();
	        listeEmpl = new ArrayList<>();
	    } catch (ApplicationException ae) {
	        // Si une exception de l'application est levée, affiche une fenêtre d'exception
	        ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
	        ed.doExceptionDialog();
	        listeEmpl = new ArrayList<>();
	    }
	    return listeEmpl;
	}

	/**
	 * Modifie les informations d'un employé existant.
	 *
	 * @author KHALIL Ahmad
	 *
	 * Cette méthode ouvre une boîte de dialogue pour permettre à l'utilisateur de modifier les informations d'un employé existant.
	 * L'employé à modifier est passé en paramètre.
	 * Si l'utilisateur effectue les modifications et les valide, les informations de l'employé sont mises à jour dans la base de données.
	 * Si la mise à jour est effectuée avec succès, l'employé modifié est retourné.
	 * En cas d'erreur de connexion à la base de données ou d'exception de l'application, une fenêtre d'exception est affichée.
	 * Si une erreur de connexion se produit, l'application est fermée.
	 * Si l'utilisateur annule la modification ou une exception se produit, la méthode retourne null.
	 *
	 * @param c L'employé à modifier
	 * @return L'employé modifié ou null en cas d'annulation ou d'erreur
	 */
    public Employe modifierEmploye(Employe c) {
        EmployeEditorPane cep = new EmployeEditorPane(this.primaryStage, this.dailyBankState);
        Employe result = cep.doEmployeEditorDialog(c, EditionMode.MODIFICATION);
        if (result != null) {
            try {
                Access_BD_Employe ac = new Access_BD_Employe();
                ac.updateEmploye(result);
            } catch (DatabaseConnexionException e) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
                ed.doExceptionDialog();
                result = null;
                this.primaryStage.close();
            } catch (ApplicationException ae) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
                ed.doExceptionDialog();
                result = null;
            }
        }
        return result;
    }
    
    /**
     * Crée un nouvel employé en saisissant ses informations.
     *
     * @author KHALIL Ahmad
     *
     * Cette méthode ouvre une boîte de dialogue pour permettre à l'utilisateur de saisir les informations d'un nouvel employé.
     * Si l'utilisateur saisit les informations et les valide, un nouvel employé est créé avec ces informations.
     * L'employé est ensuite enregistré dans la base de données.
     * Si l'enregistrement est effectué avec succès, le nouvel employé est retourné.
     * En cas d'erreur de connexion à la base de données ou d'exception de l'application, une fenêtre d'exception est affichée.
     * Si une erreur de connexion se produit, l'application est fermée.
     * Si l'utilisateur annule la création ou une exception se produit, la méthode retourne null.
     *
     * @return Le nouvel employé créé ou null en cas d'annulation ou d'erreur
     */
	public Employe nouveauEmploye() {
		Employe employe;
        EmployeEditorPane cep = new EmployeEditorPane(this.primaryStage, this.dailyBankState);
        employe = cep.doEmployeEditorDialog(null, EditionMode.CREATION);
        if (employe != null) {
            try {
                Access_BD_Employe ac = new Access_BD_Employe();

                ac.insertEmploye(employe);
            } catch (DatabaseConnexionException e) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
                ed.doExceptionDialog();
                this.primaryStage.close();
                employe = null;
            } catch (ApplicationException ae) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
                ed.doExceptionDialog();
                employe = null;
            }
        }
        return employe;
    }
	
	
	/**
	 * Supprime un employé existant.
	 *
	 * @author KHALIL Ahmad
	 *
	 * Cette méthode supprime un employé existant en le passant en paramètre.
	 * Si l'employé à supprimer n'est pas null, il est supprimé de la base de données.
	 * Si la suppression est effectuée avec succès, l'employé supprimé est retourné.
	 * En cas d'erreur de connexion à la base de données ou d'exception de l'application, une fenêtre d'exception est affichée.
	 * Si une erreur de connexion se produit, l'application est fermée.
	 * Si une exception se produit ou si l'employé à supprimer est null, la méthode retourne null.
	 *
	 * @param pfEmploye L'employé à supprimer
	 * @return L'employé supprimé ou null en cas d'erreur ou de valeur nulle
	 */
	public Employe deleteEmploye(Employe pfEmploye) {
        if (pfEmploye != null) {
            try {
                Access_BD_Employe ac = new Access_BD_Employe();

                ac.deleteEmploye(pfEmploye);
            } catch (DatabaseConnexionException e) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
                ed.doExceptionDialog();
                this.primaryStage.close();
                pfEmploye = null;
            } catch (ApplicationException ae) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
                ed.doExceptionDialog();
                pfEmploye = null;
            }
        }
        return pfEmploye;
    }
}
