package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ComptesManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.orm.Access_BD_CompteCourant;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.Table;

/**
 * La classe ComptesManagement permet de gérer l'interface graphique pour la
 * gestion des comptes d'un client. Elle est utilisée pour afficher la liste des
 * comptes d'un client, ajouter, supprimer ou modifier un compte, et gérer les
 * opérations associées à un compte.
 */

public class ComptesManagement {

	private Stage primaryStage;
	private ComptesManagementController cmcViewController;
	private DailyBankState dailyBankState;
	private Client clientDesComptes;

	/**
	 * Constructeur de la classe ComptesManagement.
	 * 
	 * @param _parentStage la fenêtre parente
	 * @param _dbstate     l'état de la banque
	 * @param client       le client dont les comptes seront gérés
	 */
	public ComptesManagement(Stage _parentStage, DailyBankState _dbstate, Client client) {
		this.clientDesComptes = client;
		this.dailyBankState = _dbstate;

		try {
			FXMLLoader loader = new FXMLLoader(ComptesManagementController.class.getResource("comptesmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth() + 50, root.getPrefHeight() + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des comptes");
			this.primaryStage.setResizable(false);

			this.cmcViewController = loader.getController();
			this.cmcViewController.initContext(this.primaryStage, this, _dbstate, client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Affiche la fenêtre de gestion des comptes.
	 */
	public void doComptesManagementDialog() {
		this.cmcViewController.displayDialog();
	}

	/**
	 * Ouvre la fenêtre pour gérer les opérations d'un compte.
	 * 
	 * @param cpt le compte à gérer
	 */
	public void gererOperationsDUnCompte(CompteCourant cpt) {
		OperationsManagement om = new OperationsManagement(this.primaryStage, this.dailyBankState,
				this.clientDesComptes, cpt);
		om.doOperationsManagementDialog();
	}

	/**
	 * RAYAN SELLOU 4B
	 * 
	 * Crée un nouveau compte courant pour le client actuel en ouvrant une boîte de
	 * dialogue d'édition de compte. Enregistre temporairement le compte en mémoire
	 * jusqu'à ce qu'une implémentation de la base de données soit ajoutée.
	 * 
	 * @return le nouveau compte courant créé ou null si l'utilisateur a annulé la
	 *         création
	 */
	public void creerNouveauCompte() {
		CompteCourant compte;
		CompteEditorPane cep = new CompteEditorPane(this.primaryStage, this.dailyBankState);
		compte = cep.doCompteEditorDialog(this.clientDesComptes, null, EditionMode.CREATION);

		// Vérifie si un compte a été créé.
		if (compte != null) {
			try {
				// accède à la BDD et ajoute le compte courant à cette dernière
				Access_BD_CompteCourant ac = new Access_BD_CompteCourant();
				ac.addCompte(compte);

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
	 * Récupère une liste de tous les comptes courants du client actuel à partir de
	 * la base de données.
	 * 
	 * @return la liste des comptes courants du client actuel ou une liste vide si
	 *         une erreur s'est produite
	 */
	public ArrayList<CompteCourant> getComptesDunClient() {
		ArrayList<CompteCourant> listeCpt = new ArrayList<>();

		try {
			Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
			listeCpt = acc.getCompteCourants(this.clientDesComptes.idNumCli, false, -1);
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeCpt = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
			listeCpt = new ArrayList<>();
		}
		return listeCpt;
	}

	/**
	 * @author ALMASRI MARWAN
	 * 
	 *         Cette méthode permet de clôturer le compte courant spécifié en
	 *         appelant la méthode cloturerCompte() de la classe
	 *         Access_BD_CompteCourant. Si un objet CompteCourant valide est fourni
	 *         en paramètre, la méthode procède à la clôture du compte courant et
	 *         peut lever une ApplicationException aléatoirement (à des fins de
	 *         test). Si une erreur de connexion à la base de données se produit, la
	 *         méthode affiche une boîte de dialogue d'erreur et ferme la fenêtre
	 *         principale. Si une ApplicationException est levée, la méthode affiche
	 *         une boîte de dialogue d'erreur sans fermer la fenêtre principale.
	 *
	 * @param cpt Le compte courant à clôturer.
	 */
	public void cloturerCompte(CompteCourant cpt) {
		if (cpt != null) {
			try {

				Access_BD_CompteCourant ac = new Access_BD_CompteCourant();
				ac.cloturerCompte(cpt);

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
	 * Lance l'éditeur de simulation.
	 * Crée une instance de SimulationEditorPane et affiche la boîte de dialogue d'édition de simulation.
	 */
	public void SimulationEditor() {
		SimulationEditorPane cep = new SimulationEditorPane(this.primaryStage, this.dailyBankState);
		cep.doSimulerEditorDialog();
	}

	/**
	 * Modifie un compte courant existant.
	 * Crée une instance de CompteEditorPane et affiche la boîte de dialogue d'édition de compte.
	 * Met à jour le compte courant dans la base de données.
	 * Gère les exceptions liées à la connexion à la base de données et aux erreurs d'application.
	 *
	 * @param cpt Le compte courant à modifier.
	 */
	public void modifierCompte(CompteCourant cpt) {
		CompteCourant compte;
		CompteEditorPane cep = new CompteEditorPane(this.primaryStage, this.dailyBankState);
		compte = cep.doCompteEditorDialog(this.clientDesComptes, cpt, EditionMode.MODIFICATION);

		// Vérifie si un compte a été créé.
		if (compte != null) {
			try {
				// accède à la BDD et ajoute le compte courant à cette dernière
				Access_BD_CompteCourant ac = new Access_BD_CompteCourant();
				ac.updateCompteCourant(compte);

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
}
