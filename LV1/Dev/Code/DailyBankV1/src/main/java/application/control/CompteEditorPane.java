package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.CompteEditorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;

/**
 * La classe CompteEditorPane représente la boîte de dialogue pour l'édition d'un compte.
 * Elle permet d'afficher un formulaire pour modifier les informations d'un compte et de sauvegarder les modifications.
 */

public class CompteEditorPane {

	private Stage primaryStage;
	private CompteEditorPaneController cepcViewController;

	/**
     * Constructeur de la classe CompteEditorPane.
     * 
     * @param _parentStage La fenêtre parente de l'éditeur de compte.
     * @param _dbstate     L'état de la banque.
     */
    public CompteEditorPane(Stage _parentStage, DailyBankState _dbstate) {
        try {
            FXMLLoader loader = new FXMLLoader(CompteEditorPaneController.class.getResource("compteeditorpane.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, root.getPrefWidth() + 20, root.getPrefHeight() + 10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            this.primaryStage = new Stage();
            this.primaryStage.initModality(Modality.WINDOW_MODAL);
            this.primaryStage.initOwner(_parentStage);
            StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
            this.primaryStage.setScene(scene);
            this.primaryStage.setTitle("Gestion d'un compte");
            this.primaryStage.setResizable(false);

            this.cepcViewController = loader.getController();
            this.cepcViewController.initContext(this.primaryStage, _dbstate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche la boîte de dialogue pour éditer un compte.
     * 
     * @param client Le client associé au compte.
     * @param cpte   Le compte à éditer.
     * @param em     Le mode d'édition.
     * @return Le compte édité ou null si l'édition a été annulée.
     */
    public CompteCourant doCompteEditorDialog(Client client, CompteCourant cpte, EditionMode em) {
        return this.cepcViewController.displayDialog(client, cpte, em);
    }
}
