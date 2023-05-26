package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.SimulationEditorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class SimulationEditorPane {

	private Stage primaryStage;
	private SimulationEditorPaneController sepc;
	// Etat courant de l'application
	private DailyBankState dailyBankState;
	
	

	
	/** @autor RAYAN SELLOU 4B
	 * Constructeur de la classe SimulationEditorPane.
	 * Crée une nouvelle fenêtre de simulation et d'assurance d'emprunt.
	 *
	 * @param _parentStage le stage parent à associer à la nouvelle fenêtre
	 * @param _dbstate l'état quotidien de la banque utilisé dans la simulation
	 */
	public SimulationEditorPane(Stage _parentStage, DailyBankState _dbstate) {

	    try {
	        // Chargement du fichier FXML
	        FXMLLoader loader = new FXMLLoader(SimulationEditorPaneController.class.getResource("simulationeditorpane.fxml"));
	        BorderPane root = loader.load();

	        // Création de la scène avec la taille préférée du root
	        Scene scene = new Scene(root, root.getPrefWidth()+20, root.getPrefHeight()+10);
	        scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

	        // Création d'une nouvelle fenêtre
	        this.primaryStage = new Stage();
	        this.primaryStage.initModality(Modality.WINDOW_MODAL);
	        this.primaryStage.initOwner(_parentStage);

	        // Gestion du centrage de la fenêtre par rapport au stage parent
	        StageManagement.manageCenteringStage(_parentStage, this.primaryStage);

	        // Configuration de la scène et de la fenêtre
	        this.primaryStage.setScene(scene);
	        this.primaryStage.setTitle("Simulation et assurance d'emprunt");
	        this.primaryStage.setResizable(false);

	        // Récupération du contrôleur du fichier FXML et initialisation de son contexte
	        this.sepc = loader.getController();
	        this.sepc.initContext(this.primaryStage, this, _dbstate);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	/** @autor RAYAN SELLOU 4B
	 * Affiche la boîte de dialogue de simulation.
	 * Cette méthode appelle la méthode displayDialog() du contrôleur de la fenêtre de simulation.
	 */
	public void doSimulerEditorDialog() {
		this.sepc.displayDialog();
	}
	
}