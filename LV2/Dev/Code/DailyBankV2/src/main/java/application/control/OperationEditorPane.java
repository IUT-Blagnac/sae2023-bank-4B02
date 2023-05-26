package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.CategorieOperation;
import application.tools.StageManagement;
import application.view.OperationEditorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.CompteCourant;
import model.data.Operation;

/**
 * La classe OperationEditorPane permet de créer et afficher une fenêtre de saisie pour l'enregistrement d'une opération bancaire.
 * Cette fenêtre est créée à partir d'un fichier FXML de nom "operationeditorpane.fxml".
 * Elle contient un formulaire permettant de saisir les informations de l'opération à enregistrer.
 * Lors de la validation du formulaire, une instance de la classe Operation est retournée avec les informations saisies.
 */
public class OperationEditorPane {

    private Stage primaryStage;
    private OperationEditorPaneController oepcViewController;

    /**
     * Constructeur de la classe OperationEditorPane.
     * @param _parentStage le stage parent de la fenêtre de l'application.
     * @param _dbstate l'état courant de l'application.
     */
    public OperationEditorPane(Stage _parentStage, DailyBankState _dbstate) {
        try {
            // Chargement du fichier FXML
            FXMLLoader loader = new FXMLLoader(OperationEditorPaneController.class.getResource("operationeditorpane.fxml"));
            BorderPane root = loader.load();

            // Création de la scène
            Scene scene = new Scene(root, 500 + 20, 250 + 10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            // Création de la fenêtre et initialisation de ses propriétés
            this.primaryStage = new Stage();
            this.primaryStage.initModality(Modality.WINDOW_MODAL);
            this.primaryStage.initOwner(_parentStage);
            StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
            this.primaryStage.setScene(scene);
            this.primaryStage.setTitle("Enregistrement d'une opération");
            this.primaryStage.setResizable(false);

            // Récupération du controller de la vue et initialisation de son contexte
            this.oepcViewController = loader.getController();
            this.oepcViewController.initContext(this.primaryStage, _dbstate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche la fenêtre de saisie pour l'enregistrement d'une opération bancaire.
     * @param cpte le compte courant concerné par l'opération.
     * @param cm la catégorie de l'opération.
     * @return une instance de la classe Operation avec les informations saisies dans le formulaire.
     */
    public Operation doOperationEditorDialog(CompteCourant cpte, CategorieOperation cm) {
        return this.oepcViewController.displayDialog(cpte, cm);
    }
    
    /**
     * Renvoie le type du débit de l'attribut isDebitExceptionnel situé dans le controlleur.
     */
	public boolean getTypeDebit() {
		return this.oepcViewController.isDebitExceptionnel;
	}
}
