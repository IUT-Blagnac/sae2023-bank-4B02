package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ClientsManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.orm.Access_BD_Client;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

/**
 * La classe ClientsManagement représente la fenêtre de gestion des clients. Elle permet de visualiser,
 * ajouter, éditer et supprimer des clients.
 */

public class ClientsManagement {

    private Stage primaryStage;
    private DailyBankState dailyBankState;
    private ClientsManagementController cmcViewController;

    /**
     * Constructeur de la classe ClientsManagement.
     * 
     * @param _parentStage la fenêtre parente
     * @param _dbstate     l'état de l'application
     */
    public ClientsManagement(Stage _parentStage, DailyBankState _dbstate) {
        this.dailyBankState = _dbstate;
        try {
            // Chargement du fichier FXML pour la gestion des clients
            FXMLLoader loader = new FXMLLoader(ClientsManagementController.class.getResource("clientsmanagement.fxml"));
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
            this.primaryStage.setTitle("Gestion des clients");
            this.primaryStage.setResizable(false);

            // Récupération du contrôleur de la vue pour la gestion des clients
            this.cmcViewController = loader.getController();
            this.cmcViewController.initContext(this.primaryStage, this, _dbstate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affichage de la fenêtre de gestion des clients.
     */
    public void doClientManagementDialog() {
        this.cmcViewController.displayDialog();
    }

    /**
     * Modification d'un client existant.
     * 
     * @param c le client à modifier
     * @return le client modifié
     */
    public Client modifierClient(Client c) {
        ClientEditorPane cep = new ClientEditorPane(this.primaryStage, this.dailyBankState);
        Client result = cep.doClientEditorDialog(c, EditionMode.MODIFICATION);
        if (result != null) {
            try {
                Access_BD_Client ac = new Access_BD_Client();
                ac.updateClient(result);
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
     * Crée un nouveau client en affichant un formulaire d'édition de client. Si le client est créé avec succès, 
     * il est ajouté à la base de données. Si une exception est levée lors de l'ajout du client, une boîte de dialogue 
     * d'exception est affichée et le client n'est pas ajouté à la base de données. Si l'opération est annulée par l'utilisateur, 
     * aucun client n'est créé ni ajouté à la base de données.
     *
     * @return le client créé ou null si l'opération a été annulée par l'utilisateur ou si une exception a été levée lors de l'ajout du client.
     */
    public Client nouveauClient() {
        Client client;
        ClientEditorPane cep = new ClientEditorPane(this.primaryStage, this.dailyBankState);
        client = cep.doClientEditorDialog(null, EditionMode.CREATION);
        if (client != null) {
            try {
                Access_BD_Client ac = new Access_BD_Client();

                ac.insertClient(client);
            } catch (DatabaseConnexionException e) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
                ed.doExceptionDialog();
                this.primaryStage.close();
                client = null;
            } catch (ApplicationException ae) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
                ed.doExceptionDialog();
                client = null;
            }
        }
        return client;
    }

    /**
     * Ouvre la fenêtre de gestion des comptes pour le client spécifié.
     *
     * @param c le client pour lequel gérer les comptes.
     */
    public void gererComptesClient(Client c) {
        ComptesManagement cm = new ComptesManagement(this.primaryStage, this.dailyBankState, c);
        cm.doComptesManagementDialog();
    }

	/**
	 * Récupère une liste de clients selon des critères de recherche.
	 * Si le numéro de compte est spécifié, renvoie le client associé à ce compte.
	 * Si le nom et prénom sont spécifiés, renvoie les clients dont le nom et prénom commencent par ces valeurs.
	 * Si aucun critère n'est spécifié, renvoie tous les clients.
	 * @param _numCompte le numéro de compte pour lequel chercher un client
	 * @param _debutNom la première lettre du nom des clients à chercher
	 * @param _debutPrenom la première lettre du prénom des clients à chercher
	 * @return une liste de clients correspondant aux critères de recherche
	 */
	public ArrayList<Client> getlisteComptes(int _numCompte, String _debutNom, String _debutPrenom) {
	    ArrayList<Client> listeCli = new ArrayList<>();
	    try {
	        // Recherche des clients en BD. cf. AccessClient > getClients(.)
	        // numCompte != -1 => recherche sur numCompte
	        // numCompte == -1 et debutNom non vide => recherche nom/prenom
	        // numCompte == -1 et debutNom vide => recherche tous les clients
	        Access_BD_Client ac = new Access_BD_Client();
	        listeCli = ac.getClients(this.dailyBankState.getEmployeActuel().idAg, _numCompte, _debutNom, _debutPrenom);

	    } catch (DatabaseConnexionException e) {
	        // Si une erreur de connexion à la base de données est levée, affiche une fenêtre d'exception et ferme l'application
	        ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
	        ed.doExceptionDialog();
	        this.primaryStage.close();
	        listeCli = new ArrayList<>();
	    } catch (ApplicationException ae) {
	        // Si une exception de l'application est levée, affiche une fenêtre d'exception
	        ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
	        ed.doExceptionDialog();
	        listeCli = new ArrayList<>();
	    }
	    return listeCli;
	}
	
	public void SimulationEditor() {
		SimulationEditorPane cep = new SimulationEditorPane(this.primaryStage, this.dailyBankState);
		cep.doSimulerEditorDialog();
	}
}
