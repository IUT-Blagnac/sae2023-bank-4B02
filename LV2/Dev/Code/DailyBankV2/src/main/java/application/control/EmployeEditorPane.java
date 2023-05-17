package application.control;

import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.EmployeEditorPaneController;
import application.view.EmployeManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Employe;

/**
 * La classe EmployeEditorPane représente la fenêtre de saisie/modification d'un employé.
 * Elle permet de créer ou de modifier un employé en utilisant un formulaire d'édition.
 */

public class EmployeEditorPane {

	private Stage primaryStage;
	private EmployeEditorPaneController cepcViewController;
	private DailyBankState dailyBankState;

	public EmployeEditorPane(Stage _parentStage, DailyBankState _dbstate) {
	    this.dailyBankState = _dbstate;
	    try {
	        FXMLLoader loader = new FXMLLoader(EmployeManagementController.class.getResource("employeeditpane.fxml"));
	        BorderPane root = loader.load();

	        Scene scene = new Scene(root, root.getPrefWidth() + 20, root.getPrefHeight() + 10);

	        this.primaryStage = new Stage();
	        this.primaryStage.initModality(Modality.WINDOW_MODAL);
	        this.primaryStage.initOwner(_parentStage);
	        StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
	        this.primaryStage.setScene(scene);
	        this.primaryStage.setTitle("Gestion des employés");
	        this.primaryStage.setResizable(false);

	        this.cepcViewController = loader.getController();
	        this.cepcViewController.initContext(this.primaryStage, this.dailyBankState);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * @author KHALIL Ahmad
	 * 
	 * Lancer la gestion des employés.
	 */
	public Employe doEmployeEditorDialog(Employe pfEmploye, EditionMode em) {
	    return this.cepcViewController.displayDialog(pfEmploye, em);
	}
}