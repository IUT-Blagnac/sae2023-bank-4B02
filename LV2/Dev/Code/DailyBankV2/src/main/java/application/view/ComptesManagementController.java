package application.view;

import java.util.ArrayList;

import application.DailyBankState;
import application.control.ComptesManagement;
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
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;

/**
 * Controller JavaFX de la view ComptesManagement.
 */

public class ComptesManagementController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à ComptesManagementController
	private ComptesManagement cmDialogController;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientDesComptes;
	private ObservableList<CompteCourant> oListCompteCourant;

	/**
	 * Initialise le contexte de la fenêtre.
	 * 
	 * @param _containingStage La fenêtre parente.
	 * @param _cm              Le contrôleur de gestion des comptes.
	 * @param _dbstate         L'état quotidien de la banque.
	 * @param client           Le client dont les comptes sont affichés.
	 */
	public void initContext(Stage _containingStage, ComptesManagement _cm, DailyBankState _dbstate, Client client) {
		this.cmDialogController = _cm;
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.clientDesComptes = client;
		this.configure();
	}

	/**
	 * Configure la fenêtre.
	 */
	private void configure() {

		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.oListCompteCourant = FXCollections.observableArrayList();
		this.lvComptes.setItems(this.oListCompteCourant);
		this.lvComptes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvComptes.getFocusModel().focus(-1);
		this.lvComptes.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());

		this.lblInfosClient.setText("Infos client : " + this.clientDesComptes.toString());

		this.loadList();
		this.validateComponentState();
	}

	/**
	 * Affiche la fenêtre.
	 */
	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	/**
	 * Fait appel a annuler() et ferme la fenêtre.
	 */
	private void closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
	}

	// Attributs de la scene + actions

	@FXML
	private Label lblInfosClient;
	@FXML
	private ListView<CompteCourant> lvComptes;
	@FXML
	private Button btnVoirOpes;
	@FXML
	private Button btnModifierCompte;
	@FXML
	private Button btnClotureCompte;
	@FXML
	private Button btnSimulation;

	/**
	 * Gère l'événement du bouton "Annuler".
	 */
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	/**
	 * Gère l'événement du bouton "Voir Opérations".
	 */
	@FXML
	private void doVoirOperations() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant cpt = this.oListCompteCourant.get(selectedIndice);
			this.cmDialogController.gererOperationsDUnCompte(cpt);
		}
		this.loadList();
		this.validateComponentState();
	}

	/**
	 * Gère l'événement du bouton "Modifier Compte".
	 */
	@FXML
	private void doModifierCompte() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant cpt = this.oListCompteCourant.get(selectedIndice);
			this.cmDialogController.modifierCompte(cpt);
		}
		this.loadList();
		this.validateComponentState();
	}

	/**
	 * @author ALMASRI MARWAN
	 * 
	 *         Cette méthode est appelée lorsqu'un utilisateur souhaite clôturer le
	 *         compte courant sélectionné dans la liste. Elle récupère l'indice de
	 *         l'élément sélectionné dans la liste, et clôture le compte courant
	 *         correspondant en appelant la méthode cloturerCompte() du contrôleur
	 *         de dialogue. Ensuite, elle recharge la liste de comptes courants et
	 *         valide l'état des composants de l'interface utilisateur.
	 * @throws DatabaseConnexionException
	 * @throws DataAccessException
	 */
	@FXML
	private void doCloturerCompte() throws DataAccessException, DatabaseConnexionException {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant cpt = this.oListCompteCourant.get(selectedIndice);
			Access_BD_Prelevements ap = new Access_BD_Prelevements();
			ArrayList<Prelevement> listprelevs = ap.getListePrelevements(cpt.idNumCompte);
			if (cpt.solde == 0) {
				if (listprelevs.size() > 0) {
					AlertUtilities.showAlert(this.primaryStage, "Action impossible",
							"Impossible de cloturer le compte car il y a encore " + listprelevs.size()
									+ " prélèvements en cours !",
							"", AlertType.INFORMATION);
				} else {
					boolean rep = AlertUtilities.confirmYesCancel(this.primaryStage, "Action impossible",
							"Voulez vous vraiment cloturer le compte ? ", null, AlertType.CONFIRMATION);
					if (rep) {
						this.cmDialogController.cloturerCompte(cpt);
					}
				}
			} else {
				AlertUtilities.showAlert(this.primaryStage, "Action impossible",
						"Impossible de cloturer le compte (solde non nul)", "", AlertType.INFORMATION);
			}
		}
		this.loadList();
		this.validateComponentState();
	}

	/**
	 * Gère l'événement du bouton "Nouveau Compte".
	 */
	@FXML
	private void doNouveauCompte() {
		this.cmDialogController.creerNouveauCompte();
		this.loadList();
	}

	/**
	 * Charge la liste des comptes courants.
	 */
	private void loadList() {
		ArrayList<CompteCourant> listeCpt;
		listeCpt = this.cmDialogController.getComptesDunClient();
		this.oListCompteCourant.clear();
		this.oListCompteCourant.addAll(listeCpt);
	}

	/**
	 * Valide l'état des composants de l'interface utilisateur.
	 */
	private void validateComponentState() {
		this.btnModifierCompte.setDisable(true);
		this.btnClotureCompte.setDisable(true);
		this.btnVoirOpes.setDisable(true);
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			if (this.oListCompteCourant.get(selectedIndice).estCloture.equals("N")) {
				this.btnModifierCompte.setDisable(false);
				this.btnClotureCompte.setDisable(false);
			} else {
				this.btnModifierCompte.setDisable(true);
				this.btnClotureCompte.setDisable(true);
			}
			this.btnVoirOpes.setDisable(false);
		} else {
			this.btnModifierCompte.setDisable(true);
			this.btnClotureCompte.setDisable(true);
			this.btnVoirOpes.setDisable(true);
		}
	}

	/**
	 * @author SELLOU Rayan
	 * 
	 * Gère l'événement du bouton "Simuler Emprunt/Assu".
	 */
	@FXML
	private void doSimulerEmprunt() {
		this.cmDialogController.SimulationEditor();
	}
}
