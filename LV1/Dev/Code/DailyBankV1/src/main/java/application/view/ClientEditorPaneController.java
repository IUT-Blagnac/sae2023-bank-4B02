package application.view;

import java.util.regex.Pattern;

import application.DailyBankState;
import application.control.ExceptionDialog;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.orm.exception.ApplicationException;
import model.orm.exception.Order;
import model.orm.exception.Table;

/**
 * Controller JavaFX de la view ClientEditorPane.
 *
 */
public class ClientEditorPaneController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre

	private Client clientEdite;
	private EditionMode editionMode;
	private Client clientResultat;

	// Manipulation de la fenêtre

	/**
	 * Initialise le contexte en assignant la scène principale et l'état quotidien de la banque.
	 * Configure l'écouteur d'événement de fermeture de la scène principale.
	 * @param _containingStage la scène principale
	 * @param _dbstate l'état quotidien de la banque
	 */
	public void initContext(Stage _containingStage, DailyBankState _dbstate) {
	    this.primaryStage = _containingStage;
	    this.dailyBankState = _dbstate;
	    this.configure();
	}

	/**
	 * Configure l'écouteur d'événement de fermeture de la scène principale.
	 * @param e l'événement de fermeture
	 */
	private void configure() {
	    this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	/**
	 * Affiche une boîte de dialogue pour l'édition d'un client.
	 * @param client le client à éditer (null pour un nouveau client)
	 * @param mode le mode d'édition
	 * @return le client résultant après l'édition
	 */
	public Client displayDialog(Client client, EditionMode mode) {
	    this.editionMode = mode;

	    if (client == null) {
	        this.clientEdite = new Client(0, "", "", "", "", "", "N", this.dailyBankState.getEmployeActuel().idAg);
	    } else {
	        this.clientEdite = new Client(client);
	    }

	    this.clientResultat = null;

	    switch (mode) {
	        case CREATION:
	            // Configuration spécifique pour le mode CREATION
	            // ...
	            break;
	        case MODIFICATION:
	            // Configuration spécifique pour le mode MODIFICATION
	            // ...
	            break;
	        case SUPPRESSION:
	            // Mode SUPPRESSION non utilisé pour les clients
	            // Affiche une exception et un dialogue d'exception
	            // ...
	            break;
	    }

	    // Paramétrages spécifiques pour les chefs d'agences
	    if (ConstantesIHM.isAdmin(this.dailyBankState.getEmployeActuel())) {
	        // Configuration spécifique pour les chefs d'agences
	        // ...
	    }

	    // Initialisation des champs avec les valeurs du client édité
	    // ...

	    // Affiche la fenêtre de dialogue et attend la fermeture
	    this.primaryStage.showAndWait();

	    return this.clientResultat;
	}

	/**
	 * Gestion de l'événement de fermeture de la fenêtre.
	 * Appelle la méthode doCancel() et consomme l'événement.
	 * @param e l'événement de fermeture
	 * @return null
	 */
	private Object closeWindow(WindowEvent e) {
	    this.doCancel();
	    e.consume();
	    return null;
	}

	// Attributs de la scene + actions

	@FXML
	private Label lblMessage;
	@FXML
	private TextField txtIdcli;
	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtPrenom;
	@FXML
	private TextField txtAdr;
	@FXML
	private TextField txtTel;
	@FXML
	private TextField txtMail;
	@FXML
	private RadioButton rbActif;
	@FXML
	private RadioButton rbInactif;
	@FXML
	private ToggleGroup actifInactif;
	@FXML
	private Button butOk;
	@FXML
	private Button butCancel;

	/**
	 * Méthode appelée lors de l'action d'annulation.
	 * Réinitialise le résultat du client et ferme la fenêtre principale.
	 */
	@FXML
	private void doCancel() {
	    this.clientResultat = null;
	    this.primaryStage.close();
	}

	/**
	 * Méthode appelée lors de l'action d'ajout.
	 * Effectue différentes actions en fonction du mode d'édition :
	 * - Mode CREATION : vérifie la saisie, assigne le client édité au résultat et ferme la fenêtre principale.
	 * - Mode MODIFICATION : vérifie la saisie, assigne le client édité au résultat et ferme la fenêtre principale.
	 * - Mode SUPPRESSION : assigne le client édité au résultat et ferme la fenêtre principale.
	 */
	@FXML
	private void doAjouter() {
	    switch (this.editionMode) {
	        case CREATION:
	            if (this.isSaisieValide()) {
	                this.clientResultat = this.clientEdite;
	                this.primaryStage.close();
	            }
	            break;
	        case MODIFICATION:
	            if (this.isSaisieValide()) {
	                this.clientResultat = this.clientEdite;
	                this.primaryStage.close();
	            }
	            break;
	        case SUPPRESSION:
	            this.clientResultat = this.clientEdite;
	            this.primaryStage.close();
	            break;
	    }
	}

	/**
	 * Vérifie si la saisie du client est valide.
	 * Met à jour les propriétés du client édité en fonction des champs de saisie.
	 * Affiche des messages d'erreur si la saisie est invalide.
	 * @return true si la saisie est valide, false sinon.
	 */
	private boolean isSaisieValide() {
	    this.clientEdite.nom = this.txtNom.getText().trim();
	    this.clientEdite.prenom = this.txtPrenom.getText().trim();
	    this.clientEdite.adressePostale = this.txtAdr.getText().trim();
	    this.clientEdite.telephone = this.txtTel.getText().trim();
	    this.clientEdite.email = this.txtMail.getText().trim();
	    if (this.rbActif.isSelected()) {
	        this.clientEdite.estInactif = ConstantesIHM.CLIENT_ACTIF;
	    } else {
	        this.clientEdite.estInactif = ConstantesIHM.CLIENT_INACTIF;
	    }

	    if (this.clientEdite.nom.isEmpty()) {
	        AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le nom ne doit pas être vide",
	                AlertType.WARNING);
	        this.txtNom.requestFocus();
	        return false;
	    }
	    if (this.clientEdite.prenom.isEmpty()) {
	        AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le prénom ne doit pas être vide",
	                AlertType.WARNING);
	        this.txtPrenom.requestFocus();
	        return false;
	    }

		String regex = "(0)[1-9][0-9]{8}";
		if (!Pattern.matches(regex, this.clientEdite.telephone) || this.clientEdite.telephone.length() > 10) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le téléphone n'est pas valable",
					AlertType.WARNING);
			this.txtTel.requestFocus();
			return false;
		}
		regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
				+ "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
		if (!Pattern.matches(regex, this.clientEdite.email) || this.clientEdite.email.length() > 20) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le mail n'est pas valable",
					AlertType.WARNING);
			this.txtMail.requestFocus();
			return false;
		}

		return true;
	}
}
