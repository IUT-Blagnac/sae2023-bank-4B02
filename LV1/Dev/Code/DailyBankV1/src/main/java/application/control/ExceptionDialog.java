package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.ExceptionDialogController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.orm.exception.ApplicationException;

/**
 * La classe ExceptionDialog permet de créer une boîte de dialogue affichant une exception.
 * Elle utilise une vue créée à partir d'un fichier FXML et un contrôleur associé.
 */
public class ExceptionDialog {

    private Stage primaryStage;
    private ExceptionDialogController edcViewController;

    /**
     * Constructeur de la classe ExceptionDialog.
     *
     * @param _parentStage la fenêtre principale où la boîte de dialogue sera affichée.
     * @param _dbstate     l'état de la banque quotidienne.
     * @param ae           l'exception à afficher dans la boîte de dialogue.
     */
    public ExceptionDialog(Stage _parentStage, DailyBankState _dbstate, ApplicationException ae) {
        try {
            // Charge la vue depuis le fichier FXML et crée son élément racine.
            FXMLLoader loader = new FXMLLoader(ExceptionDialogController.class.getResource("exceptiondialog.fxml"));
            BorderPane root = loader.load();

            // Crée une scène pour la vue.
            Scene scene = new Scene(root, 700 + 20, 400 + 10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            // Crée une nouvelle fenêtre modale et la configure.
            this.primaryStage = new Stage();
            this.primaryStage.initModality(Modality.WINDOW_MODAL);
            this.primaryStage.initOwner(_parentStage);
            StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
            this.primaryStage.setScene(scene);
            this.primaryStage.setTitle("Opération impossible");
            this.primaryStage.setResizable(false);

            // Initialise le contrôleur avec la fenêtre principale, l'état de la banque quotidienne et l'exception.
            this.edcViewController = loader.getController();
            this.edcViewController.initContext(this.primaryStage, _dbstate, ae);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche la boîte de dialogue.
     */
    public void doExceptionDialog() {
        this.edcViewController.displayDialog();
    }

    /*
     * Test :
     * ApplicationException ae = new ApplicationException(Table.NONE, Order.OTHER, "M", null );
     * ExceptionDialog ed = new ExceptionDialog(primaryStage, dbs, ae);
     * ed.doExceptionDialog();
     */
}
