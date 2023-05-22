package application.view;

import java.util.ArrayList;

import application.DailyBankState;
import application.control.EmployeManagement;
import application.tools.AlertUtilities;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Employe;

/**
 * Controller JavaFX de la view EmployeManagement.
 */

public class EmployeManagementController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à EmployeManagementController
	private EmployeManagement cmDialogController;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private ObservableList<Employe> oListEmploye;

	/**
	 * Initialise le contexte de la fenêtre en spécifiant le contrôleur de dialogue,
	 * la fenêtre principale et l'état quotidien de la banque.
	 * 
	 * @param _containingStage La fenêtre principale.
	 * @param _em              Le contrôleur de gestion des employés.
	 * @param _dbstate         L'état quotidien de la banque.
	 */
	public void initContext(Stage _containingStage, EmployeManagement _em, DailyBankState _dbstate) {
		this.cmDialogController = _em;
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.configure();
	}

	/**
	 * Configure la fenêtre en définissant l'action à effectuer lors de sa
	 * fermeture, en initialisant la liste des employés et en validant l'état des
	 * composants.
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.oListEmploye = FXCollections.observableArrayList();
		this.listeEmploye.setItems(this.oListEmploye);
		this.listeEmploye.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.listeEmploye.getFocusModel().focus(-1);
		this.listeEmploye.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
		this.validateComponentState();
		this.doRechercher();
	}

	/**
	 * Affiche la fenêtre de dialogue.
	 */
	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	/**
	 * Gère l'événement de fermeture de la fenêtre.
	 * 
	 * @param e L'événement de fermeture de la fenêtre.
	 */
	private void closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
	}

	/**
	 * Gère l'action d'annulation.
	 */
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	// Attributs de la scene + actions

	@FXML
	private TextField txtNum;
	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtPrenom;
	@FXML
	private ListView<Employe> listeEmploye;
	@FXML
	private Button btnSupprimer;
	@FXML
	private Button btnMAJ;
	@FXML
	private Button btnInfos;
	@FXML
	private Button btnAjouter;

	/**
	 * Affiche les informations détaillées d'un employé sélectionné.
	 * 
	 * @author ALMASRI Marwan
	 * 
	 *         Cette méthode affiche les informations détaillées d'un employé
	 *         sélectionné dans une boîte de dialogue. Les informations affichées
	 *         comprennent le numéro de l'employé, le nom, le prénom, les droits
	 *         d'accès, le login, le mot de passe et le numéro de l'agence. Si
	 *         l'employé sélectionné a des droits d'accès "chefAgence" et
	 *         l'utilisateur actuel a également des droits d'accès "chefAgence", les
	 *         informations de login et de mot de passe sont masquées par des
	 *         étoiles (******). Sinon, les informations de login et de mot de passe
	 *         sont affichées normalement.
	 */
	@FXML
	private void checkInfo() {
		int selectedIndice = this.listeEmploye.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Employe EmplMod = this.oListEmploye.get(selectedIndice);
			String info = "";
			info += "N°Employé : " + EmplMod.idEmploye + "\nNom : " + EmplMod.nom + "\nPrénom : " + EmplMod.prenom
					+ "\nDroits d'accès : " + EmplMod.droitsAccess;
			if (this.dailyBankState.getEmployeActuel().droitsAccess.equals("chefAgence")
					&& EmplMod.droitsAccess.equals("chefAgence")) {
				info += "\nLogin : ****** " + "\nMot de passe : ******";
			} else {
				info += "\nLogin : " + EmplMod.login + "\nMot de passe : " + EmplMod.motPasse;
			}

			info += "\nN°Agence : " + EmplMod.idAg;

			AlertUtilities.showAlert(this.primaryStage, "Information d'un employé", null, info, AlertType.INFORMATION);
		}
	}

	/**
	 * Modifie les informations d'un employé existant.
	 * 
	 * @author KHALIL Ahmad
	 * 
	 *         Spécifiez ici votre nom en tant qu'auteur de cette méthode.
	 * 
	 *         Cette méthode gère la modification des informations d'un employé
	 *         existant. Si un employé est sélectionné dans la liste, une boîte de
	 *         dialogue est ouverte pour permettre à l'utilisateur de modifier les
	 *         informations de l'employé sélectionné. Si l'utilisateur effectue les
	 *         modifications et les confirme, les informations de l'employé sont
	 *         mises à jour dans la liste. Si l'utilisateur annule ou ne fait aucune
	 *         modification, les informations de l'employé restent inchangées.
	 */
	@FXML
	private void doModifierEmploye() {
		int selectedIndice = this.listeEmploye.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Employe EmplMod = this.oListEmploye.get(selectedIndice);
			Employe result = this.cmDialogController.modifierEmploye(EmplMod);
			if (result != null) {
				this.oListEmploye.set(selectedIndice, result);
			}
		}
	}

	/**
	 * Crée un nouvel employé.
	 * 
	 * @author KHALIL Ahmad
	 * 
	 *         Cette méthode gère la création d'un nouvel employé. Elle ouvre une
	 *         boîte de dialogue pour collecter les informations sur le nouvel
	 *         employé. Si l'utilisateur saisit les informations requises et
	 *         confirme, l'employé est créé et ajouté à la liste des employés
	 *         existants. Si l'utilisateur annule ou ne saisit pas toutes les
	 *         informations requises, aucun employé n'est créé.
	 */
	@FXML
	private void doNouveauEmploye() {
		Employe employe;
		employe = this.cmDialogController.nouveauEmploye();
		if (employe != null) {
			this.oListEmploye.add(employe);
		}
	}

	/**
	 * Supprime un employé de la liste des employés.
	 * 
	 * @author KHALIL Ahmad
	 * 
	 *         Cette méthode gère la suppression d'un employé de la liste. Si un
	 *         employé est sélectionné, une confirmation est demandée à
	 *         l'utilisateur avant de procéder à la suppression. Si l'utilisateur
	 *         confirme, l'employé sélectionné est supprimé de la liste des
	 *         employés, et une recherche est effectuée pour mettre à jour la vue.
	 */
	@FXML
	private void deleteEmploye() {
		int selectedIndice = this.listeEmploye.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			boolean choix = AlertUtilities.confirmYesCancel(primaryStage, "Supression d'un employé",
					"Merci de confirmer la suppression", null, AlertType.CONFIRMATION);
			if (choix) {
				Employe EmplMod = this.oListEmploye.get(selectedIndice);
				this.cmDialogController.deleteEmploye(EmplMod);
				this.doRechercher();
			}
		}
	}

	/**
	 * Effectue une recherche d'employés en fonction des critères spécifiés.
	 * 
	 * Cette méthode effectue une recherche d'employés en fonction des critères
	 * spécifiés dans les champs de recherche. Si un numéro de compte est saisi et
	 * valide, la recherche est effectuée en fonction de ce numéro de compte. Si un
	 * numéro de compte est saisi mais invalide, la recherche est ignorée. Si un nom
	 * et/ou un prénom sont saisis, la recherche est effectuée en fonction de ces
	 * critères. Si aucun critère de recherche n'est spécifié, tous les employés
	 * sont affichés. Les résultats de la recherche sont affichés dans la liste des
	 * employés. Cette méthode met également à jour l'état des composants
	 * graphiques.
	 */
	@FXML
	private void doRechercher() {
		int numCompte;
		try {
			String nc = this.txtNum.getText();
			if (nc.equals("")) {
				numCompte = -1;
			} else {
				numCompte = Integer.parseInt(nc);
				if (numCompte < 0) {
					numCompte = -1;
				}
			}
		} catch (NumberFormatException nfe) {
			numCompte = -1;
		}

		String debutNom = this.txtNom.getText();
		String debutPrenom = this.txtPrenom.getText();

		// Recherche des Employe en BD. cf. AccessEmploye > getEmploye(.)
		// numCompte != -1 => recherche sur numCompte
		// numCompte != -1 et debutNom non vide => recherche nom/prenom
		// numCompte != -1 et debutNom vide => recherche tous les employes
		ArrayList<Employe> listeEmpl;
		listeEmpl = this.cmDialogController.getlisteEmploye(numCompte, debutNom, debutPrenom);

		this.oListEmploye.clear();
		this.oListEmploye.addAll(listeEmpl);
		this.validateComponentState();
	}

	/**
	 * Valide l'état des composants graphiques en fonction de la sélection de
	 * l'employé.
	 * 
	 * Cette méthode met à jour l'état des boutons en fonction de la sélection de
	 * l'employé dans la liste. Si aucun employé n'est sélectionné, tous les boutons
	 * sont désactivés. Si un employé est sélectionné, les boutons "Informations",
	 * "Mise à jour" et "Supprimer" sont activés ou désactivés en fonction des
	 * droits d'accès de l'employé. Les employés ayant le droit d'accès "chefAgence"
	 * ne peuvent pas être mis à jour ou supprimés, donc les boutons correspondants
	 * sont désactivés.
	 */
	private void validateComponentState() {
		// Non implémenté => désactivé
		int selectedIndice = this.listeEmploye.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			this.btnInfos.setDisable(false);
			Employe EmplMod = this.oListEmploye.get(selectedIndice);
			if (EmplMod.droitsAccess.equals("chefAgence")) {
				this.btnMAJ.setDisable(true);
				this.btnSupprimer.setDisable(true);
			} else {
				this.btnMAJ.setDisable(false);
				this.btnSupprimer.setDisable(false);
			}
		} else {
			this.btnInfos.setDisable(true);
			this.btnMAJ.setDisable(true);
			this.btnSupprimer.setDisable(true);
		}
	}
}
