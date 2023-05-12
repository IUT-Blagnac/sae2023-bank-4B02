package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ClientEditorPaneController;
import application.view.ClientsManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;

/**
 * La classe ClientEditorPane représente la fenêtre de saisie/modification d'un client.
 * Elle permet de créer ou de modifier un client en utilisant un formulaire d'édition.
 */

public class ClientEditorPane {

	private Stage primaryStage;
	private ClientEditorPaneController cepcViewController;
	private DailyBankState dailyBankState;

	/**
	 * Constructeur de la fenêtre de saisie/modification d'un client.
	 * @param _parentStage Stage parent de la fenêtre.
	 * @param _dbstate DailyBankState de l'application.
	 */
	public ClientEditorPane(Stage _parentStage, DailyBankState _dbstate) {
	    this.dailyBankState = _dbstate;
	    try {
	        FXMLLoader loader = new FXMLLoader(ClientsManagementController.class.getResource("clienteditorpane.fxml"));
	        BorderPane root = loader.load();

	        Scene scene = new Scene(root, root.getPrefWidth() + 20, root.getPrefHeight() + 10);
	        scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

	        this.primaryStage = new Stage();
	        this.primaryStage.initModality(Modality.WINDOW_MODAL);
	        this.primaryStage.initOwner(_parentStage);
	        StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
	        this.primaryStage.setScene(scene);
	        this.primaryStage.setTitle("Gestion d'un client");
	        this.primaryStage.setResizable(false);

	        this.cepcViewController = loader.getController();
	        this.cepcViewController.initContext(this.primaryStage, this.dailyBankState);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	/**
	 * Affiche la fenêtre de saisie/modification d'un client et retourne le client saisi/modifié.
	 * @param client Client à modifier ou null pour créer un nouveau client.
	 * @param em Mode d'édition : CREATION pour créer un nouveau client, MODIFICATION pour modifier un client existant.
	 * @return Le client saisi/modifié, ou null si la fenêtre a été annulée.
	 */
	public Client doClientEditorDialog(Client client, EditionMode em) {
	    return this.cepcViewController.displayDialog(client, em);
	}
}
