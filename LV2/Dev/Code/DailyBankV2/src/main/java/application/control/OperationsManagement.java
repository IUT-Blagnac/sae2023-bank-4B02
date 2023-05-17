package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.CategorieOperation;
import application.tools.PairsOfValue;
import application.tools.StageManagement;
import application.view.OperationsManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.Access_BD_CompteCourant;
import model.orm.Access_BD_Operation;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

/**
 * Cette classe est utilisée pour gérer les opérations d'un compte bancaire pour
 * un client spécifique. Elle affiche une interface utilisateur pour ajouter,
 * supprimer ou modifier des opérations.
 */
public class OperationsManagement {

	/**
	 * La fenêtre principale de l'application.
	 */
	private Stage primaryStage;

	/**
	 * L'état actuel de l'application.
	 */
	private DailyBankState dailyBankState;

	/**
	 * Le contrôleur associé à cette instance de la classe.
	 */
	private OperationsManagementController omcViewController;

	/**
	 * Le client dont les opérations sont en cours de gestion.
	 */
	private Client clientDuCompte;

	/**
	 * Le compte bancaire en cours de gestion.
	 */
	private CompteCourant compteConcerne;

	/**
	 * Constructeur de la classe OperationsManagement.
	 * 
	 * @param _parentStage la fenêtre parente de l'application
	 * @param _dbstate     l'état actuel de l'application
	 * @param client       le client associé au compte bancaire
	 * @param compte       le compte bancaire en cours de gestion
	 */
	public OperationsManagement(Stage _parentStage, DailyBankState _dbstate, Client client, CompteCourant compte) {

		this.clientDuCompte = client;
		this.compteConcerne = compte;
		this.dailyBankState = _dbstate;

		try {
			FXMLLoader loader = new FXMLLoader(
					OperationsManagementController.class.getResource("operationsmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, 900 + 20, 350 + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des opérations");
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
	public void doOperationsManagementDialog() {
		this.omcViewController.displayDialog();
	}

	/**
	 * Permet d'afficher la fenêtre d'édition pour enregistrer une opération de type
	 * débit sur le compte courant associé à l'instance de cette classe.
	 * 
	 * @return L'opération créée si l'utilisateur a validé l'enregistrement, sinon
	 *         null.
	 */
	public Operation enregistrerDebit() {

		OperationEditorPane oep = new OperationEditorPane(this.primaryStage, this.dailyBankState);
		Operation op = oep.doOperationEditorDialog(this.compteConcerne, CategorieOperation.DEBIT);
		if (op != null) {
			try {
				Access_BD_Operation ao = new Access_BD_Operation();

				ao.insertDebit(this.compteConcerne.idNumCompte, op.montant, op.idTypeOp);

			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				op = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				op = null;
			}
		}
		return op;
	}

	/**
	 * RAYAN SELLOU 4B Enregistre une opération de crédit.
	 *
	 * @return L'opération enregistrée, ou null si l'enregistrement a échoué ou a
	 *         été annulé.
	 */
	public Operation enregistrerCredit() {
		// Ouvre et affiche une fenêtre d'édition d'opération
		// et récupère l'opération saisie
		OperationEditorPane oep = new OperationEditorPane(this.primaryStage, this.dailyBankState);
		Operation op = oep.doOperationEditorDialog(this.compteConcerne, CategorieOperation.CREDIT);
		// Vérifie si une opération a été saisie
		if (op != null) {
			try {
				Access_BD_Operation ao = new Access_BD_Operation();
				ao.insertCredit(this.compteConcerne.idNumCompte, op.montant, op.idTypeOp);

			} catch (DatabaseConnexionException e) {
				// gestion des exceptions de connexion à la BDD
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				op = null;
			} catch (ApplicationException ae) {
				// gestion des exceptions d'application
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				op = null;
			}
		}
		return op;
	}

	/**
	 * KHALIL Ahmad 4B
	 * 
	 * Enregistre une opération de virement.
	 *
	 * @return L'opération enregistrée, ou null si l'enregistrement a échoué ou a
	 *         été annulé.
	 */
	public Operation enregistrerVirement() {
		// Ouvre et affiche une fenêtre d'édition d'opération
		// et récupère l'opération saisie
		OperationEditorPane oep = new OperationEditorPane(this.primaryStage, this.dailyBankState);
		Operation op = oep.doOperationEditorDialog(this.compteConcerne, CategorieOperation.VIREMENT);
		// Vérifie si une opération a été saisie
		if (op != null) {
			try {
				Access_BD_Operation ao = new Access_BD_Operation();
				ao.insertVirement(this.compteConcerne.idNumCompte, op.idNumCompte, op.montant);
			} catch (DatabaseConnexionException e) {
				// gestion des exceptions de connexion à la BDD
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				op = null;
			} catch (ApplicationException ae) {
				// gestion des exceptions d'application
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				op = null;
			}
		}
		return op;
	}

	/**
	 * Récupère la liste des opérations enregistrées sur le compte courant associé à
	 * l'instance de cette classe, ainsi que le solde actuel de ce compte.
	 * 
	 * @return Un objet PairsOfValue contenant une paire (CompteCourant,
	 *         ArrayList<Operation>) représentant respectivement le compte courant
	 *         associé et la liste des opérations enregistrées sur ce compte.
	 */
	public PairsOfValue<CompteCourant, ArrayList<Operation>> operationsEtSoldeDunCompte() {
		ArrayList<Operation> listeOP = new ArrayList<>();

		try {
			// Relecture BD du solde du compte
			Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
			this.compteConcerne = acc.getCompteCourant(this.compteConcerne.idNumCompte);

			// lecture BD de la liste des opérations du compte de l'utilisateur
			Access_BD_Operation ao = new Access_BD_Operation();
			listeOP = ao.getOperations(this.compteConcerne.idNumCompte);

		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeOP = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
			listeOP = new ArrayList<>();
		}
		return new PairsOfValue<>(this.compteConcerne, listeOP);
	}
}
