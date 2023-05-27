package application.view;

import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;

/**
 * Controller JavaFX de la view CompteEditorPane.
 */

public class CompteEditorPaneController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private EditionMode editionMode;
	private Client clientDuCompte;
	private CompteCourant compteEdite;
	private CompteCourant compteResultat;

	/**
	 * Initialise le contexte de la fenêtre avec le stage parent et l'état quotidien
	 * de la banque.
	 * 
	 * @param _containingStage Le stage parent de la fenêtre.
	 * @param _dbstate         L'état quotidien de la banque.
	 */
	public void initContext(Stage _containingStage, DailyBankState _dbstate) {
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.configure();
	}

	/**
	 * Configure la fenêtre en ajoutant des gestionnaires d'événements aux
	 * composants.
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.txtDecAutorise.focusedProperty().addListener((t, o, n) -> this.focusDecouvert(t, o, n));
		this.txtSolde.focusedProperty().addListener((t, o, n) -> this.focusSolde(t, o, n));
	}

	/**
	 * Affiche la fenêtre de dialogue pour la gestion des comptes courants.
	 * 
	 * @param client Le client associé au compte courant.
	 * @param cpte   Le compte courant à afficher (null pour un nouveau compte).
	 * @param mode   Le mode d'édition (CREATION, MODIFICATION, SUPPRESSION).
	 * @return Le compte courant résultant de l'opération (null en cas d'annulation
	 *         ou d'erreur).
	 */
	public CompteCourant displayDialog(Client client, CompteCourant cpte, EditionMode mode) {
		this.clientDuCompte = client;
		this.editionMode = mode;
		if (cpte == null) {
			this.compteEdite = new CompteCourant(0, -200, 0, "N", this.clientDuCompte.idNumCli);
		} else {
			this.compteEdite = new CompteCourant(cpte);
		}
		this.compteResultat = null;
		this.txtIdclient.setDisable(true);
		this.txtIdAgence.setDisable(true);
		this.txtIdNumCompte.setDisable(true);
		switch (mode) {
		case CREATION:
			this.txtDecAutorise.setDisable(false);
			this.txtSolde.setDisable(false);
			this.lblMessage.setText("Informations sur le nouveau compte");
			this.lblSolde.setText("Solde (premier dépôt)");
			this.btnOk.setText("Ajouter");
			this.btnCancel.setText("Annuler");
			break;
		case MODIFICATION:
			this.txtSolde.setDisable(true);
			this.txtIdclient.setText("" + client.idNumCli);
			this.txtIdAgence.setText("" + client.idAg);
			this.txtIdNumCompte.setText("" + cpte.idNumCompte);
			this.txtSolde.setText("" + cpte.solde);
			this.txtDecAutorise.setText("" + cpte.debitAutorise);
			this.lblMessage.setText("Modification d'un compte");
			this.lblSolde.setText("Solde (premier dépôt)");
			this.btnOk.setText("Modifier");
			this.btnCancel.setText("Annuler");
			break;
		case SUPPRESSION:
			AlertUtilities.showAlert(this.primaryStage, "Non implémenté", "Suppression de compte n'est pas implémenté",
					null, AlertType.ERROR);
			return null;
		// break;
		}

		// Paramétrages spécifiques pour les chefs d'agences
		if (ConstantesIHM.isAdmin(this.dailyBankState.getEmployeActuel())) {
			// rien pour l'instant
		}

		// initialisation du contenu des champs
		this.txtIdclient.setText("" + this.compteEdite.idNumCli);
		this.txtIdNumCompte.setText("" + this.compteEdite.idNumCompte);
		this.txtIdAgence.setText("" + this.dailyBankState.getEmployeActuel().idAg);
		this.txtDecAutorise.setText("" + this.compteEdite.debitAutorise);
		this.txtSolde.setText("" + this.compteEdite.solde);

		this.compteResultat = null;

		this.primaryStage.showAndWait();
		return this.compteResultat;
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
	 * Gère l'événement de mise au focus du champ de texte "txtDecAutorise".
	 * 
	 * @param txtField         L'observable qui détecte le changement de focus du
	 *                         champ de texte.
	 * @param oldPropertyValue La valeur précédente de la propriété focus.
	 * @param newPropertyValue La nouvelle valeur de la propriété focus.
	 * @return null
	 */
	private Object focusDecouvert(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
			boolean newPropertyValue) {
		if (oldPropertyValue) {
			try {
				int val;
				val = Integer.parseInt(this.txtDecAutorise.getText().trim());
				if (val > 0) {
					throw new NumberFormatException();
				}
				this.compteEdite.debitAutorise = val;
			} catch (NumberFormatException nfe) {
			}
		}
		return null;
	}

	/**
	 * Gère l'événement de mise au focus du champ de texte "txtSolde".
	 * 
	 * @param txtField         L'observable qui détecte le changement de focus du
	 *                         champ de texte.
	 * @param oldPropertyValue La valeur précédente de la propriété focus.
	 * @param newPropertyValue La nouvelle valeur de la propriété focus.
	 * @return null
	 */
	private Object focusSolde(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
			boolean newPropertyValue) {
		if (oldPropertyValue) {
			try {
				double val;
				val = Double.parseDouble(this.txtSolde.getText().trim());
				if (val < 0) {
					throw new NumberFormatException();
				}
				this.compteEdite.solde = val;
			} catch (NumberFormatException nfe) {
			}
		}
		return null;
	}

	// Attributs de la scene + actions
	@FXML
	private Label lblMessage;
	@FXML
	private Label lblSolde;
	@FXML
	private TextField txtIdclient;
	@FXML
	private TextField txtIdAgence;
	@FXML
	private TextField txtIdNumCompte;
	@FXML
	private TextField txtDecAutorise;
	@FXML
	private TextField txtSolde;
	@FXML
	private Button btnOk;
	@FXML
	private Button btnCancel;

	/**
	 * Annule et ferme la fenêtre d'édition des comptes.
	 */
	@FXML
	private void doCancel() {
		this.compteResultat = null;
		this.primaryStage.close();
	}

	/**
	 * Gère l'événement du bouton "Ajouter".
	 */
	@FXML
	private void doAjouter() {
		switch (this.editionMode) {
		case CREATION:
			if (this.isSaisieValide()) {
				this.compteResultat = this.compteEdite;
				this.primaryStage.close();
			}
			break;
		case MODIFICATION:
			if (this.isSaisieValide()) {
				this.compteResultat = this.compteEdite;
				this.primaryStage.close();
			}
			break;
		case SUPPRESSION:
			this.compteResultat = this.compteEdite;
			this.primaryStage.close();
			break;
		}
	}

	/**
	 * Vérifie si la saisie des champs est valide.
	 * 
	 * @return true si la saisie est valide, false sinon.
	 */
	private boolean isSaisieValide() {
		try {
			int decouvert = Integer.parseInt(this.txtDecAutorise.getText().trim());
			double solde = Double.parseDouble(this.txtSolde.getText());
			String info = "";
			if (decouvert > 0) {
				info += "Le découvert ne peut pas être supérieur à 0 !\n";
			}
			if (this.editionMode == editionMode.CREATION) {
				if (solde < 0) {
					info += "Le premier dépôt doit être > à 0 !";
				}
			}
			if (decouvert < -9999) {
				info += "Le découvert maximale est de -9999 !\n";
			}
			if (info.equals("")) {
				return true;
			} else {
				AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, info, AlertType.WARNING);
				return false;
			}
		} catch (NumberFormatException e) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", "Saisie invalide ! ",
					"Merci d'entrer des chiffres dans les cases 'Découvert autorisé' et 'Solde'", AlertType.WARNING);
			return false;
		}
	}
}
