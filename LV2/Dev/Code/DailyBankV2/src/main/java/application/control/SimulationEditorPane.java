package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ClientsManagementController;
import application.view.SimulationEditorPaneController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class SimulationEditorPane {

	private Stage primaryStage;
	private SimulationEditorPaneController sepc;
	// Etat courant de l'application
	private DailyBankState dailyBankState;
	
	

	
	public SimulationEditorPane(Stage _parentStage, DailyBankState _dbstate) {

		try {
			FXMLLoader loader = new FXMLLoader(SimulationEditorPaneController.class.getResource("simulationeditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+20, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Simulation et assurance d'emprunt");
			this.primaryStage.setResizable(false);

			this.sepc = loader.getController();
			this.sepc.initContext(this.primaryStage,this, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Permet à l'utilisateur d'interagir avec le dialogue du controleur de la simulation et assurance emprunt
	 * @see EditionMode
	 * @see SimulerEditorPaneController
	 */
	public void doSimulerEditorDialog() {
		this.sepc.displayDialog();
	}
	
}