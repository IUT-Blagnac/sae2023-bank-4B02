package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.OperationsManagementController;
import application.view.PrelevementsManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Prelevement;
import model.orm.Access_BD_Prelevements;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.Table;

/**
 * Cette classe est utilisée pour gérer les prélèvements d'un compte bancaire
 * pour un client spécifique. Elle affiche une interface utilisateur pour
 * ajouter, supprimer ou modifier des prélèvements.
 */
public class PrelevementsManagement {

	// La fenêtre principale de l'application.
	private Stage primaryStage;

	// L'état actuel de l'application.
	private DailyBankState dailyBankState;

	// Le contrôleur associé à cette instance de la classe.
	private PrelevementsManagementController omcViewController;

	// Le client dont les opérations sont en cours de gestion.
	private Client clientDuCompte;

	// Le compte bancaire en cours de gestion.
	private CompteCourant compteConcerne;

	/**
	 * Constructeur de la classe OperationsManagement.
	 * 
	 * @param _parentStage la fenêtre parente de l'application
	 * @param _dbstate     l'état actuel de l'application
	 * @param client       le client associé au compte bancaire
	 * @param compte       le compte bancaire en cours de gestion
	 */
	public PrelevementsManagement(Stage _parentStage, DailyBankState _dbstate, Client client, CompteCourant compte) {

		this.clientDuCompte = client;
		this.compteConcerne = compte;
		this.dailyBankState = _dbstate;

		try {
			FXMLLoader loader = new FXMLLoader(
					OperationsManagementController.class.getResource("prelevementsmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, 900 + 20, 350 + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des prélèvements");
			this.primaryStage.setResizable(false);

			this.omcViewController = loader.getController();
			this.omcViewController.initContext(this.primaryStage, this, _dbstate, client, this.compteConcerne);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Affiche la fenêtre de gestion des opérations.
	 */
	public void doPrelevementsManagementDialog() {
		this.omcViewController.displayDialog();
	}

	/**
	 * @author KHALIL Ahmad
	 * 
	 *         Supprime un prélèvement de la base de données.
	 *
	 * @param pfPrelevement Le prélèvement à supprimer.
	 */
	public void supprimer(Prelevement pfPrelevement) {
		if (pfPrelevement != null) {
			try {

				Access_BD_Prelevements ac = new Access_BD_Prelevements();
				ac.supprimerPrelevement(pfPrelevement.idprelev);

				if (Math.random() < -1) {
					throw new ApplicationException(Table.CompteCourant, Order.INSERT, "todo : test exceptions", null);
				}
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
			}
		}
	}

	/**
	 * @author KHALIL Ahmad
	 * 
	 *         Exécute tous les prélèvements pour un compte donné.
	 *
	 * @param idNumCompte L'identifiant du compte.
	 * @return Le résultat de l'exécution des prélèvements.
	 */
	public String executerAll(int idNumCompte) {
		try {

			Access_BD_Prelevements ac = new Access_BD_Prelevements();
			String res = ac.executerAll(idNumCompte);

			return res;
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
		}
		return null;
	}

	/**
	 * @author KHALIL Ahmad
	 * 
	 *         Crée un nouveau prélèvement et l'ajoute à la base de données.
	 */
	public void nouveauPrelevement() {
		Prelevement prlv;
		PrelevementEditorPane pep = new PrelevementEditorPane(primaryStage, dailyBankState);
		prlv = pep.doPrelevementEditorDialog(null, EditionMode.CREATION);
		if (prlv != null) {
			prlv.idNumCompte = this.compteConcerne.idNumCompte;
			try {
				Access_BD_Prelevements ac = new Access_BD_Prelevements();
				ac.addPrelevement(prlv);

			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
			}
		}
	}

	/**
	 * @author KHALIL Ahmad
	 * 
	 *         Modifie un prélèvement existant dans la base de données.
	 *
	 * @param pfPrelev Le prélèvement à modifier.
	 */
	public void modifierPrelevement(Prelevement pfPrelev) {
		Prelevement prlv;
		PrelevementEditorPane pep = new PrelevementEditorPane(primaryStage, dailyBankState);
		prlv = pep.doPrelevementEditorDialog(pfPrelev, EditionMode.MODIFICATION);
		if (prlv != null) {
			try {
				Access_BD_Prelevements ac = new Access_BD_Prelevements();
				ac.updatePrelevement(pfPrelev);

			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
			}
		}
	}
}
