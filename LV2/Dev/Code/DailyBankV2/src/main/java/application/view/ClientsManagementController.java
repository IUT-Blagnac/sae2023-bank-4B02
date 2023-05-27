package application.view;

import java.util.ArrayList;

import application.DailyBankState;
import application.control.ClientsManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;

/**
 * Controller JavaFX de la view ClientsManagement.
 */

public class ClientsManagementController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à ClientsManagementController
	private ClientsManagement cmDialogController;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private ObservableList<Client> oListClients;

	/**
	 * Initialise le contexte de la fenêtre.
	 * Définit les différents attributs utilisés par la fenêtre avec les valeurs passées en paramètres.
	 * Appelle la méthode configure() pour effectuer la configuration initiale de la fenêtre.
	 * 
	 * @param _containingStage la fenêtre principale qui contient cette fenêtre de dialogue
	 * @param _cm le controller ClientsManagement utilisé pour gérer les clients
	 * @param _dbstate l'état bancaire quotidien utilisé pour les informations de compte
	 */
	public void initContext(Stage _containingStage, ClientsManagement _cm, DailyBankState _dbstate) {
		this.cmDialogController = _cm;
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.configure();
	}

	/**
	 * Effectue la configuration initiale de la fenêtre.
	 * Configure la fermeture de la fenêtre principale lors de la demande de fermeture.
	 * Initialise la liste observable des clients et la lie à la liste affichée dans la fenêtre.
	 * Configure la sélection unique des clients dans la liste.
	 * Configure le gestionnaire d'événements pour mettre à jour l'état des composants lors de la sélection d'un client.
	 * Valide l'état des composants.
	 * Appelle la méthode doRechercher() pour effectuer une recherche initiale.
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.oListClients = FXCollections.observableArrayList();
		this.lvClients.setItems(this.oListClients);
		this.lvClients.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvClients.getFocusModel().focus(-1);
		this.lvClients.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
		this.validateComponentState();
		this.doRechercher();
	}

	/**
	 * Affiche la fenêtre de dialogue et attend jusqu'à ce qu'elle soit fermée.
	 */
	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	/**
	 * Gère l'événement de fermeture de la fenêtre.
	 * Appelle la méthode doCancel() pour effectuer les actions d'annulation.
	 * Consomme l'événement pour empêcher la fermeture de la fenêtre.
	 * 
	 * @param e l'événement de fermeture de la fenêtre
	 */
	private void closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
	}

	// Attributs de la scene + actions

	@FXML
	private TextField txtNum;
	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtPrenom;
	@FXML
	private ListView<Client> lvClients;
	@FXML
	private Button btnDesactClient;
	@FXML
	private Button btnModifClient;
	@FXML
	private Button btnComptesClient;
	

	/**
	 * Ferme la fenêtre principale.
	 */
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	/**
	 * Effectue une recherche de clients en fonction des critères saisis.
	 * Les critères comprennent le numéro de compte, le début du nom et le début du prénom.
	 * Les résultats de la recherche sont affichés dans la liste des clients et actualisé à chaque nouvelle touche entrée.
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

		// Recherche des clients en BD. cf. AccessClient > getClients(.)
		// numCompte != -1 => recherche sur numCompte
		// numCompte != -1 et debutNom non vide => recherche nom/prenom
		// numCompte != -1 et debutNom vide => recherche tous les clients
		ArrayList<Client> listeCli;
		listeCli = this.cmDialogController.getlisteComptes(numCompte, debutNom, debutPrenom);

		this.oListClients.clear();
		this.oListClients.addAll(listeCli);
		this.validateComponentState();
	}

	/**
	 * Affiche la fenêtre de gestion des comptes du client sélectionné dans une nouvelle fenêtre.
	 */
	@FXML
	private void doComptesClient() {
		int selectedIndice = this.lvClients.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Client client = this.oListClients.get(selectedIndice);
			this.cmDialogController.gererComptesClient(client);
		}
	}

	/**
	 * Affiche la fenêtre de modification de client avec les informations du client sélectionné.
	 * Si la modification est réussie, met à jour les informations du client dans la liste des clients.
	 */
	@FXML
	private void doModifierClient() {
		int selectedIndice = this.lvClients.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Client cliMod = this.oListClients.get(selectedIndice);
			Client result = this.cmDialogController.modifierClient(cliMod);
			if (result != null) {
				this.oListClients.set(selectedIndice, result);
			}
		}
	}

	/**
	 * Désactive le client sélectionné.
	 * (Méthode non implémentée)
	 */
	@FXML
	private void doDesactiverClient() {
	}

	/**
	 * Ajoute un nouveau client.
	 * Si l'ajout est réussi, ajoute le nouveau client à la liste des clients.
	 */
	@FXML
	private void doNouveauClient() {
		Client client;
		client = this.cmDialogController.nouveauClient();
		if (client != null) {
			this.oListClients.add(client);
		}
	}

	/**
	 * Valide l'état des composants de la fenêtre en fonction de la sélection des clients.
	 * Désactive le bouton de désactivation du client (non implémenté).
	 * Active ou désactive les boutons de modification et de gestion des comptes en fonction de la sélection d'un client.
	 */
	private void validateComponentState() {
		this.btnDesactClient.setDisable(true);
		int selectedIndice = this.lvClients.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			this.btnModifClient.setDisable(false);
			this.btnComptesClient.setDisable(false);
		} else {
			this.btnModifClient.setDisable(true);
			this.btnComptesClient.setDisable(true);
		}
	}
}
