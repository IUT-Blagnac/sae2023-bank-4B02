package application.view;

import java.util.ArrayList;
import application.DailyBankState;
import application.control.OperationsManagement;
import application.tools.NoSelectionModel;
import application.tools.PairsOfValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;

/**
 * Controller JavaFX de la view OperationsManagement.
 *
 */

public class OperationsManagementController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à OperationsManagementController
	private OperationsManagement omDialogController;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientDuCompte;
	private CompteCourant compteConcerne;
	private ObservableList<Operation> oListOperations;
	ArrayList<Operation> listeOP;

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, OperationsManagement _om, DailyBankState _dbstate, Client client,
			CompteCourant compte) {
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.omDialogController = _om;
		this.clientDuCompte = client;
		this.compteConcerne = compte;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.oListOperations = FXCollections.observableArrayList();
		this.lvOperations.setItems(this.oListOperations);
		this.lvOperations.setSelectionModel(new NoSelectionModel<Operation>());
		this.updateInfoCompteClient();
		this.validateComponentState();
	}

	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions

	@FXML
	private Label lblInfosClient;
	@FXML
	private Label lblInfosCompte;
	@FXML
	private ListView<Operation> lvOperations;
	@FXML
	private Button btnDebit;
	@FXML
	private Button btnCredit;
	@FXML
	private Button btnVirement;

	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	@FXML
	private void doDebit() {
		Operation op = this.omDialogController.enregistrerDebit();
		if (op != null) {
			this.updateInfoCompteClient();
			this.validateComponentState();
		}
	}

	/**
	 * RAYAN SELLOU 4B Ici on créer la fonction permettant de gérer les crédits
	 * Cette méthode permet de gérer les crédits d'un compte client.
	 * 
	 * @implNote Cette méthode est appelée lorsque l'utilisateur clique sur le
	 *           bouton de crédit.
	 * @implSpec Cette méthode utilise un objet de type Operation pour enregistrer
	 *           le crédit dans le compte client.
	 * 
	 */
	@FXML
	private void doCredit() {
		Operation op = this.omDialogController.enregistrerCredit();
		if (op != null) {
			this.updateInfoCompteClient();
			this.validateComponentState();
		}
	}

	/**
	 * Effectue un virement sur le compte client.
	 *
	 * @author KHALIL Ahmad
	 * 
	 * Cette méthode est appelée lorsqu'on souhaite effectuer un virement sur le compte client. Elle utilise la méthode enregistrerVirement()
	 * de la classe omDialogController pour enregistrer le virement. Si le virement est enregistré avec succès (opération différente de null),
	 * elle met à jour les informations du compte client en appelant la méthode updateInfoCompteClient() et met à jour l'état des composants
	 * en appelant la méthode validateComponentState().
	 */
	@FXML
	private void doVirement() {
		Operation op = this.omDialogController.enregistrerVirement();
		if (op != null) {
			this.updateInfoCompteClient();
			this.validateComponentState();
		}
	}

	private void validateComponentState() {
		if (this.compteConcerne.estCloture.equals("N")) {
			this.btnCredit.setDisable(false);
			this.btnDebit.setDisable(false);
			this.btnVirement.setDisable(false);
		} else {
			this.btnCredit.setDisable(true);
			this.btnDebit.setDisable(true);
			this.btnVirement.setDisable(true);
		}
	}

	private void updateInfoCompteClient() {

		PairsOfValue<CompteCourant, ArrayList<Operation>> opesEtCompte;

		opesEtCompte = this.omDialogController.operationsEtSoldeDunCompte();

		this.compteConcerne = opesEtCompte.getLeft();
		this.listeOP = opesEtCompte.getRight();

		this.lblInfosClient.setText(
				"N°Client : " + this.clientDuCompte.idNumCli + " | N°Compte : " + this.compteConcerne.idNumCompte
						+ " | Etat : " + (this.compteConcerne.estCloture.equals("N") ? "Ouvert" : "Cloturé"));

		this.lblInfosCompte.setText("Solde : " + this.compteConcerne.solde + " | Découvert Autorisé : "
				+ Integer.toString(this.compteConcerne.debitAutorise));

		this.oListOperations.clear();
		this.oListOperations.addAll(listeOP);

		this.validateComponentState();
	}
}
