package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ComptesManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
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
 * La classe ComptesManagement permet de gérer l'interface graphique pour la gestion des comptes d'un client.
 * Elle est utilisée pour afficher la liste des comptes d'un client, ajouter, supprimer ou modifier un compte, et gérer les opérations associées à un compte.
 */

public class ComptesManagement {

	private Stage primaryStage;
	private ComptesManagementController cmcViewController;
	private DailyBankState dailyBankState;
	private Client clientDesComptes;

	/**
     * Constructeur de la classe ComptesManagement.
     * @param _parentStage la fenêtre parente
     * @param _dbstate l'état de la banque
     * @param client le client dont les comptes seront gérés
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
     * @param cpt le compte à gérer
     */
    public void gererOperationsDUnCompte(CompteCourant cpt) {
        OperationsManagement om = new OperationsManagement(this.primaryStage, this.dailyBankState, this.clientDesComptes, cpt);
        om.doOperationsManagementDialog();
    }

    /**
     * Crée un nouveau compte courant pour le client actuel en ouvrant une boîte de dialogue d'édition de compte.
     * Enregistre temporairement le compte en mémoire jusqu'à ce qu'une implémentation de la base de données soit ajoutée.
     * 
     * @return le nouveau compte courant créé ou null si l'utilisateur a annulé la création
     */
    public CompteCourant creerNouveauCompte() {
        CompteCourant compte;
        CompteEditorPane cep = new CompteEditorPane(this.primaryStage, this.dailyBankState);
        compte = cep.doCompteEditorDialog(this.clientDesComptes, null, EditionMode.CREATION);
        if (compte != null) {
            try {
                // Temporaire jusqu'à implémentation
                compte = null;
                AlertUtilities.showAlert(this.primaryStage, "En cours de développement", "Non implémenté",
                        "Enregistrement réel en BDD du compte non effectué\nEn cours de développement", AlertType.ERROR);

                // TODO : enregistrement du nouveau compte en BDD (la BDD donne de nouvel id dans "compte")

                // if JAMAIS vrai
                // existe pour compiler les catchs dessous
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
        return compte;
    }

    /**
     * Récupère une liste de tous les comptes courants du client actuel à partir de la base de données.
     * 
     * @return la liste des comptes courants du client actuel ou une liste vide si une erreur s'est produite
     */
    public ArrayList<CompteCourant> getComptesDunClient() {
        ArrayList<CompteCourant> listeCpt = new ArrayList<>();

        try {
            Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
            listeCpt = acc.getCompteCourants(this.clientDesComptes.idNumCli);
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
}
