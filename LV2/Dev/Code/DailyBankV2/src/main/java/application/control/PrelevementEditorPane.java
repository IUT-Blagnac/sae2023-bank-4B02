package application.control;

import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.CompteEditorPaneController;
import application.view.PrelevementEditorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Prelevement;

/**
 * Cette classe gère la fenêtre de l'éditeur de prélèvements.
 */
public class PrelevementEditorPane {

	private Stage primaryStage;
	private PrelevementEditorPaneController cepcViewController;

	 /**
     * Constructeur de la classe PrelevementEditorPane.
     *
     * @param _parentStage Le stage parent.
     * @param _dbstate     L'état de la banque.
     */
    public PrelevementEditorPane(Stage _parentStage, DailyBankState _dbstate) {
        try {
            FXMLLoader loader = new FXMLLoader(CompteEditorPaneController.class.getResource("prelevementeditorpane.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, root.getPrefWidth() + 20, root.getPrefHeight() + 10);

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
     * Affiche la fenêtre de l'éditeur de prélèvements.
     *
     * @param prlv Le prélèvement à éditer.
     * @param em   Le mode d'édition.
     * @return Le prélèvement modifié ou null si l'édition est annulée.
     */
    public Prelevement doPrelevementEditorDialog(Prelevement prlv, EditionMode em) {
        return this.cepcViewController.displayDialog(prlv, em);
    }
}
