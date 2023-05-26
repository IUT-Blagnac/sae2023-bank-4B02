package application.view;

import java.util.ArrayList;

import application.DailyBankState;
import application.control.ExceptionDialog;
import application.control.PrelevementsManagement;
import application.tools.AlertUtilities;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Prelevement;
import model.orm.Access_BD_Prelevements;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

/**
 * Controller JavaFX de la view PrelevementsManagement.
 */

public class PrelevementsManagementController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à OperationsManagementController
	private PrelevementsManagement omDialogController;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientDuCompte;
	private CompteCourant compteConcerne;
	private ObservableList<Prelevement> oListPrelevements;
	ArrayList<Prelevement> listePrelevs = new ArrayList<Prelevement>();

	/**
	 * Initialise le contexte de la fenêtre de gestion des prélèvements.
	 *
	 * @param _containingStage Le stage contenant la fenêtre
	 * @param _om              L'instance du gestionnaire de prélèvements
	 * @param _dbstate         L'état courant de la banque
	 * @param client           Le client associé au compte
	 * @param compte           Le compte courant concerné
	 */
	public void initContext(Stage _containingStage, PrelevementsManagement _om, DailyBankState _dbstate, Client client,
			CompteCourant compte) {
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.omDialogController = _om;
		this.clientDuCompte = client;
		this.compteConcerne = compte;
		this.configure();

		this.lblInfosClient.setText(
				"N°Client : " + this.clientDuCompte.idNumCli + " | N°Compte : " + this.compteConcerne.idNumCompte
						+ " | Etat : " + (this.compteConcerne.estCloture.equals("N") ? "Ouvert" : "Cloturé"));
	}

	/**
	 * Configure les paramètres et le comportement de la fenêtre de gestion des
	 * prélèvements.
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.oListPrelevements = FXCollections.observableArrayList();
		this.updateInfoPrelevements();
		this.lvPrelevements.setItems(this.oListPrelevements);
		this.lvPrelevements.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvPrelevements.getFocusModel().focus(-1);
		this.lvPrelevements.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
		this.validateComponentState();
	}

	/**
	 * Affiche la fenêtre de gestion des prélèvements.
	 */
	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	/**
	 * Gère l'événement de fermeture de la fenêtre.
	 *
	 * @param e L'événement de fermeture de la fenêtre
	 */
	private void closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
	}

	// Attributs de la scene + actions

	@FXML
	private Label lblInfosClient;
	@FXML
	private Label lblInfosCompte;
	@FXML
	private ListView<Prelevement> lvPrelevements;
	@FXML
	private Button btnSupprimer;
	@FXML
	private Button btnMAJ;
	@FXML
	private Button btnCreer;
	@FXML
	private Button btnToutPrelever;

	/**
	 * Ferme la fenêtre de gestion des prélèvements.
	 */
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	/**
	 * @author KHALIL Ahmad
	 * 
	 *         Édite le prélèvement sélectionné.
	 */
	@FXML
	private void doEditer() {

		int selectedIndice = this.lvPrelevements.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Prelevement prelev = this.oListPrelevements.get(selectedIndice);
			if (prelev != null) {
				this.omDialogController.modifierPrelevement(prelev);
			} else {
				AlertUtilities.showAlert(this.primaryStage, "Action impossible", "Prelevement choisi null", "",
						AlertType.INFORMATION);
			}
		}
		updateInfoPrelevements();
	}

	/**
	 * @author KHALIL Ahmad
	 * 
	 *         Crée un nouveau prélèvement.
	 */
	@FXML
	private void doCreer() {
		this.omDialogController.nouveauPrelevement();
		updateInfoPrelevements();
	}

	/**
	 * @author KHALIL Ahmad
	 * 
	 *         Exécute tous les prélèvements du compte courant.
	 */
	@FXML
	private void doExecuterAll() {
		if (this.dailyBankState.getEmployeActuel().droitsAccess.equals("chefAgence")) {
			String res = this.omDialogController.executerAll(this.compteConcerne.idNumCompte);
			if (res == null) {
				res = "Aucun prélèvement à exécuter ou à cette date.";
			}
			AlertUtilities.showAlert(primaryStage, "Réponse du serveur", "Exécution des prélèvements", res,
					AlertType.INFORMATION);
		} else {
			AlertUtilities.showAlert(primaryStage, "Action impossible",
					"Seul un chef d'agence peut exécuter les prélèvements", null, AlertType.INFORMATION);
		}
		this.updateInfoPrelevements();
		this.validateComponentState();
	}

	/**
	 * @author Al-Masri Marwan
	 * 
	 *         Supprime le prélèvement sélectionné.
	 */
	@FXML
	private void doSupprimer() {
		int selectedIndice = this.lvPrelevements.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Prelevement prlv = this.oListPrelevements.get(selectedIndice);
			boolean res = AlertUtilities.confirmYesCancel(primaryStage, "Confirmation",
					"Voulez vous vraiment supprimer ce prélèvement ?", null, AlertType.CONFIRMATION);
			if (res) {
				this.omDialogController.supprimer(prlv);
			}
		}
		this.updateInfoPrelevements();
		this.validateComponentState();
	}

	/**
	 * Met à jour les informations sur les prélèvements.
	 */
	public void updateInfoPrelevements() {
		try {
			Access_BD_Prelevements acc = new Access_BD_Prelevements();
			this.listePrelevs = acc.getListePrelevements(this.compteConcerne.idNumCompte);
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listePrelevs = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
			this.listePrelevs = new ArrayList<>();
		}

		this.lblInfosCompte.setText("Nombre de prélèvements en cours : " + listePrelevs.size());

		this.oListPrelevements.clear();
		this.oListPrelevements.addAll(this.listePrelevs);
	}

	/**
	 * Valide l'état des composants de la fenêtre.
	 */
	private void validateComponentState() {
		this.btnCreer.setDisable(false);
		this.btnMAJ.setDisable(true);
		this.btnSupprimer.setDisable(true);
		int selectedIndice = this.lvPrelevements.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			this.btnMAJ.setDisable(false);
			this.btnSupprimer.setDisable(false);
		}
	}
}
