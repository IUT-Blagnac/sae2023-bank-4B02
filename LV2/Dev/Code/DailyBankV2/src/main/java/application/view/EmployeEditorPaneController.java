package application.view;

import application.DailyBankState;
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
import model.data.Employe;
import model.orm.Access_BD_Employe;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.RowNotFoundOrTooManyRowsException;

/**
 * Controller JavaFX de la view EmployeEditorPane.
 *
 */
public class EmployeEditorPaneController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre

	private Employe employeEdite;
	private EditionMode editionMode;
	private Employe employeResultat;

	// Manipulation de la fenêtre

	public void initContext(Stage _containingStage, DailyBankState _dbstate) {
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.configure();
	}
	
	/**
	 * Affiche la boîte de dialogue de modification d'un employé.
	 *
	 * @param pfEmploye L'employé à modifier
	 * @param mode Le mode d'édition (CREATION, MODIFICATION ou SUPPRESSION)
	 * @return L'employé résultat après modification ou suppression
	 *
	 * Cette méthode affiche une boîte de dialogue permettant de modifier un employé existant.
	 * Le paramètre pfEmploye spécifie l'employé à modifier.
	 * Le paramètre mode indique le mode d'édition de la boîte de dialogue.
	 * La méthode retourne l'employé résultat après modification ou suppression.
	 */
	public Employe displayDialog(Employe pfEmploye, EditionMode mode) {

		this.editionMode = mode;
		if (pfEmploye == null) {
			this.employeEdite = new Employe();
		} else {
			this.employeEdite = new Employe(pfEmploye);
		}
		this.employeResultat = null;
		ToggleGroup choixAcces = new ToggleGroup();
		this.btnChefAg.setToggleGroup(choixAcces);
		this.btnGuichetier.setToggleGroup(choixAcces);
		choixAcces.selectedToggleProperty().addListener((observableValue, oldToggle, newToggle) -> {
			if (newToggle == null) {
				oldToggle.setSelected(true);
			}
		});
		switch (mode) {
		case CREATION:
			this.txtIdempl.setDisable(true);
			this.txtNom.setDisable(false);
			this.txtPrenom.setDisable(false);
			this.txtLogin.setDisable(false);
			this.txtMdp.setDisable(false);
			this.btnGuichetier.setSelected(true);
			this.txtIdAg.setDisable(false);
			this.lblMessage.setText("Entrer les informations du nouveau employé");
			// initialisation du contenu des champs
			this.txtNom.setText("");
			this.txtPrenom.setText("");
			this.txtLogin.setText("");
			this.txtMdp.setText("");
			this.txtIdAg.setText("");
			break;
		case MODIFICATION:
			this.txtIdempl.setDisable(true);
			this.txtNom.setDisable(false);
			this.txtPrenom.setDisable(false);
			this.txtLogin.setDisable(false);
			this.txtMdp.setDisable(false);
			if(this.employeEdite.droitsAccess.equals("guichetier")) {
			this.btnGuichetier.setSelected(true);
			this.btnChefAg.setSelected(false);
			}
			else {
				this.btnChefAg.setSelected(true);
				this.btnGuichetier.setSelected(false);
			}
			this.txtIdAg.setDisable(true);
			this.lblMessage.setText("Mettez à jour les informations de l'employé");
			this.butOk.setText("Modifier");
			this.txtIdempl.setText("" + this.employeEdite.idEmploye);
			this.txtNom.setText(this.employeEdite.nom);
			this.txtPrenom.setText(this.employeEdite.prenom);
			this.txtLogin.setText(this.employeEdite.login);
			this.txtMdp.setText(this.employeEdite.motPasse);
			this.txtIdAg.setText(String.valueOf(this.employeEdite.idAg));
			break;
		case SUPPRESSION:
			break;
		}
		// Paramétrages spécifiques pour les chefs d'agences
		if (ConstantesIHM.isAdmin(this.dailyBankState.getEmployeActuel())) {
			// rien pour l'instant
		}

		this.employeResultat = null;

		this.primaryStage.showAndWait();
		return this.employeResultat;
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	@FXML
	private void doCancel() {
		this.employeResultat = null;
		this.primaryStage.close();
	}

	@FXML
	private Label lblMessage;
	@FXML
	private TextField txtIdempl;
	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtPrenom;
	@FXML
	private RadioButton btnGuichetier;
	@FXML
	private RadioButton btnChefAg;
	@FXML
	private TextField txtLogin;
	@FXML
	private TextField txtMdp;
	@FXML
	private TextField txtIdAg;
	@FXML
	private Button butOk;
	@FXML
	private Button butCancel;
	
	/**
	 * Effectue l'action d'ajout d'un employé.
	 *
	 * Cette méthode est appelée lorsque l'utilisateur clique sur le bouton d'ajout.
	 * Elle gère l'ajout d'un nouvel employé en fonction du mode d'édition en cours.
	 * Si le mode est CREATION ou MODIFICATION, elle vérifie d'abord la validité de la saisie.
	 * Si la saisie est valide, elle affecte l'employé en cours d'édition à l'employé résultat et ferme la fenêtre.
	 * Si le mode est SUPPRESSION, elle affecte l'employé en cours d'édition à l'employé résultat et ferme la fenêtre.
	 */
	@FXML
	private void doAjouter() {
		switch (this.editionMode) {
		case CREATION:
			if (this.isSaisieValide()) {
				this.employeResultat = this.employeEdite;
				this.primaryStage.close();
			}
			break;
		case MODIFICATION:
			if (this.isSaisieValide()) {
				this.employeResultat = this.employeEdite;
				this.primaryStage.close();
			}
			break;
		case SUPPRESSION:
			this.employeResultat = this.employeEdite;
			this.primaryStage.close();
			break;
		}
	}
	
	/**
	 * Vérifie si la saisie des informations de l'employé est valide.
	 *
	 * Cette méthode vérifie si les informations saisies pour l'employé sont valides.
	 * Elle récupère les valeurs saisies dans les champs de texte et les assigne à l'employé en cours d'édition.
	 * Elle effectue différentes vérifications, telles que la présence de valeurs obligatoires et la validité du numéro d'agence.
	 * Si toutes les vérifications passent avec succès, la méthode retourne true.
	 * Sinon, elle affiche une fenêtre d'alerte avec les informations sur les erreurs de saisie et retourne false.
	 *
	 * @return true si la saisie est valide, false sinon
	 */
	private boolean isSaisieValide() {
		this.employeEdite.nom = this.txtNom.getText().trim();
		this.employeEdite.prenom = this.txtPrenom.getText().trim();
		this.employeEdite.login = this.txtLogin.getText();
		this.employeEdite.motPasse = this.txtMdp.getText();
		try {
			if (!this.txtIdAg.getText().toString().isEmpty()) {
				this.employeEdite.idAg = Integer.valueOf(this.txtIdAg.getText());
			}
		} catch (java.lang.NumberFormatException e) {
			System.out.println("Exception NumberFormatException : EmployeEditorPaneController.isSaisieValide");
		}
		if (this.btnChefAg.isSelected())
			this.employeEdite.droitsAccess = "chefAgence";
		else
			this.employeEdite.droitsAccess = "guichetier";
		String info = "";
		if (this.employeEdite.nom.isEmpty()) {
			info += "Le nom ne doit pas être vide !\n";
			this.txtNom.requestFocus();
		}
		if (this.employeEdite.prenom.isEmpty()) {
			info += "Le prénom ne doit pas être vide !\n";
			this.txtPrenom.requestFocus();
		}
		if (this.employeEdite.login.isEmpty()) {
			info += "Le login ne doit pas être vide !\n";
			this.txtLogin.requestFocus();
		}
		if (this.employeEdite.motPasse.isEmpty()) {
			info += "Le mot de passe ne doit pas être vide !\n";
			this.txtMdp.requestFocus();
		}
		Access_BD_Employe ac = new Access_BD_Employe();
		try {
			if (!ac.checkIdAgence(this.employeEdite.idAg)) {
				info += "Le numéro d'agence est invalide !\n";
				this.txtIdAg.requestFocus();
			}
		} catch (RowNotFoundOrTooManyRowsException e) {
			System.out.println(
					"Exception RowNotFoundOrTooManyRowsException : EmployeEditorPaneController.isSaisieValide");
		} catch (DataAccessException e) {
			System.out.println("Exception DataAccessException : EmployeEditorPaneController.isSaisieValide");
		} catch (DatabaseConnexionException e) {
			System.out.println("Exception DatabaseConnexionException : EmployeEditorPaneController.isSaisieValide");
		}
		if (info.equals("")) {
			return true;
		} else {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, info, AlertType.WARNING);
			return false;
		}
	}
}