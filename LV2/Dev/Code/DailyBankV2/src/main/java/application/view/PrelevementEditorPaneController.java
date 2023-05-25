package application.view;

import application.DailyBankState;
import application.view.PrelevementEditorPaneController;
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
import model.data.Prelevement;

/**
 * Controller JavaFX de la vue PrelevementEditorPane.
 */

public class PrelevementEditorPaneController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private EditionMode editionMode;
	private Prelevement prelevementEdite;
	private Prelevement prelevementResultat;

	/**
	 * Initialise le contexte du contrôleur.
	 *
	 * @param _containingStage la fenêtre physique contenant la scène contrôlée par
	 *                         ce contrôleur
	 * @param _dbstate         l'état courant de l'application DailyBankState
	 */
	public void initContext(Stage _containingStage, DailyBankState _dbstate) {
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.configure();

		this.labDateRecur.setText("Jour du prélèvement" + "\n	(entre 1 et 28)");
	}

	/**
	 * Configure le contrôleur en ajoutant des écouteurs d'événements et en
	 * définissant des comportements pour les éléments de la fenêtre.
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.txtMontant.focusedProperty().addListener((t, o, n) -> this.focusMontant(t, o, n));
		this.txtDateRecur.focusedProperty().addListener((t, o, n) -> this.focusDateRecu(t, o, n));
		this.txtBeneficiaire.focusedProperty().addListener((t, o, n) -> this.focusBeneficiaire(t, o, n));
	}

	/**
	 * Affiche une fenêtre de dialogue pour la création ou la modification d'un
	 * prélèvement. Initialise les valeurs des champs en fonction du prélèvement
	 * fourni (s'il existe) et du mode d'édition. Retourne le prélèvement résultant
	 * de la saisie utilisateur ou null si l'action est annulée.
	 *
	 * @param prlv Le prélèvement à modifier (null pour une création)
	 * @param mode Le mode d'édition (CREATION ou MODIFICATION)
	 * @return Le prélèvement résultat ou null si l'action est annulée
	 */
	public Prelevement displayDialog(Prelevement prlv, EditionMode mode) {
		this.editionMode = mode;
		if (prlv == null) {
			this.prelevementEdite = new Prelevement();
		} else {
			this.prelevementEdite = prlv;
		}
		this.prelevementResultat = null;

		switch (mode) {
		case CREATION:
			this.lblMessage.setText("Création d'un nouveau prélèvement");
			break;

		case MODIFICATION:
			this.lblMessage.setText("Modification d'un prélèvement");
			this.txtNumPrelev.setText("" + this.prelevementEdite.idprelev);
			this.txtNumCompte.setText("" + this.prelevementEdite.idNumCompte);
			this.txtDateRecur.setText("" + this.prelevementEdite.dateRecurrente);
			this.txtMontant.setText("" + this.prelevementEdite.montant);
			this.txtBeneficiaire.setText(this.prelevementEdite.beneficiaire);
			this.btnOk.setText("Modifier");
			break;

		default:
			break;
		}

		// Paramétrages spécifiques pour les chefs d'agences
		if (ConstantesIHM.isAdmin(this.dailyBankState.getEmployeActuel())) {
			// rien pour l'instant
		}

		this.prelevementResultat = null;

		this.primaryStage.showAndWait();
		return this.prelevementResultat;
	}

	/**
	 * Gère l'événement de fermeture de la fenêtre.
	 *
	 * @param e l'événement de fermeture de la fenêtre
	 */
	private void closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
	}

	/**
	 * Réagit à l'événement de focus sur le champ de saisie de la date de
	 * prélèvement récurrent.
	 *
	 * @param txtField         la propriété observée du champ de saisie
	 * @param oldPropertyValue la valeur précédente de la propriété
	 * @param newPropertyValue la nouvelle valeur de la propriété
	 * @return null
	 */
	private Object focusDateRecu(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
			boolean newPropertyValue) {
		if (oldPropertyValue) {
			try {
				int val;
				val = Integer.parseInt(this.txtDateRecur.getText().trim());
				this.prelevementEdite.dateRecurrente = val;
			} catch (NumberFormatException nfe) {
			}
		}
		return null;
	}

	/**
	 * Réagit à l'événement de focus sur le champ de saisie du bénéficiaire.
	 *
	 * @param txtField         la propriété observée du champ de saisie
	 * @param oldPropertyValue la valeur précédente de la propriété
	 * @param newPropertyValue la nouvelle valeur de la propriété
	 * @return null
	 */
	private Object focusBeneficiaire(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
			boolean newPropertyValue) {
		if (oldPropertyValue) {
			try {
				String val;
				val = this.txtBeneficiaire.getText().trim();
				this.prelevementEdite.beneficiaire = val;
			} catch (NumberFormatException nfe) {
			}
		}
		return null;
	}

	/**
	 * Réagit à l'événement de focus sur le champ de saisie du montant.
	 *
	 * @param txtField         la propriété observée du champ de saisie
	 * @param oldPropertyValue la valeur précédente de la propriété
	 * @param newPropertyValue la nouvelle valeur de la propriété
	 * @return null
	 */
	private Object focusMontant(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
			boolean newPropertyValue) {
		if (oldPropertyValue) {
			try {
				double val;
				val = Double.parseDouble(this.txtMontant.getText().trim());
				if (val < 0) {
					throw new NumberFormatException();
				}
				this.prelevementEdite.montant = val;
			} catch (NumberFormatException nfe) {
			}
		}
		return null;
	}

	// Attributs de la scene + actions
	@FXML
	private Label lblMessage;
	@FXML
	private TextField txtNumPrelev;
	@FXML
	private TextField txtNumCompte;
	@FXML
	private TextField txtDateRecur;
	@FXML
	private TextField txtBeneficiaire;
	@FXML
	private TextField txtMontant;
	@FXML
	private Label labDateRecur;
	@FXML
	private Button btnOk;
	@FXML
	private Button btnCancel;

	/**
	 * Annule l'action en cours en réinitialisant la variable prelevementResultat à
	 * null et en fermant la fenêtre.
	 */
	@FXML
	private void doCancel() {
		this.prelevementResultat = null;
		this.primaryStage.close();
	}

	/**
	 * Gère l'action de l'ajout/modification d'un prélèvement. Vérifie la validité
	 * des saisies et assigne les valeurs appropriées à prelevementResultat. Ferme
	 * ensuite la fenêtre.
	 */
	@FXML
	private void doAjouter() {
		switch (this.editionMode) {
		case CREATION:
			if (this.isSaisieValide()) {
				this.prelevementResultat = this.prelevementEdite;
				this.primaryStage.close();
			}
			break;
		case MODIFICATION:
			if (this.isSaisieValide()) {
				this.prelevementResultat = this.prelevementEdite;
				this.primaryStage.close();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * Vérifie la validité des saisies de la fenêtre de dialogue. Vérifie que la
	 * date du prélèvement est comprise entre 1 et 28, que le nom du bénéficiaire
	 * n'est pas vide, que le montant est supérieur à 0 et inférieur ou égal à
	 * 999999. Affiche une alerte en cas de saisie invalide.
	 *
	 * @return true si la saisie est valide, false sinon
	 */
	private boolean isSaisieValide() {
		try {
			int dateRecur = Integer.parseInt(this.txtDateRecur.getText().trim());
			String beneficiaire = this.txtBeneficiaire.getText();
			double montant = Double.parseDouble(this.txtMontant.getText());

			String info = "";
			if (dateRecur < 0 || dateRecur > 28) {
				info += "La date du prélèvement doit être comprise entre 1 et 28 !\n";
			}
			if (beneficiaire.equals("")) {
				info += "Merci de saisir le nom du bénéficiaire !\n";
			}
			if (montant <= 0) {
				info += "Le montant doit être > à 0 !";
			}
			if (montant > 999999) {
				info += "Le montant maximale est de 999 999 !\n";
			}
			if (info.equals("")) {
				this.prelevementResultat = new Prelevement(montant, dateRecur, beneficiaire);
				return true;
			} else {
				AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, info, AlertType.WARNING);
				return false;
			}
		} catch (NumberFormatException e) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", "Saisie invalide ! ",
					"Merci d'entrer des chiffres dans les cases 'Jour de récurrence' et 'Montant'", AlertType.WARNING);
			return false;
		}
	}
}
