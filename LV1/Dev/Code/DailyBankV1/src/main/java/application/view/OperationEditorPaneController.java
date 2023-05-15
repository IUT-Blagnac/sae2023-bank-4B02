package application.view;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

import application.DailyBankState;
import application.tools.CategorieOperation;
import application.tools.ConstantesIHM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.CompteCourant;
import model.data.Operation;
import model.data.TypeOperation;
import model.orm.Access_BD_CompteCourant;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;

/**
 * Controller JavaFX de la view OperationEditorPane.
 *
 */

public class OperationEditorPaneController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private CategorieOperation categorieOperation;
	private CompteCourant compteEdite;
	private Operation operationResultat;

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, DailyBankState _dbstate) {
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	public Operation displayDialog(CompteCourant cpte, CategorieOperation mode) {
		this.categorieOperation = mode;
		this.compteEdite = cpte;

		switch (mode) {
		case DEBIT:
			this.primaryStage.setTitle("Enregistrer un débit");
			String info = "Compte n°" + this.compteEdite.idNumCompte + "  Solde : " + this.compteEdite.solde
					+ "  Découvert Autorisé : " + Integer.toString(this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);

			this.btnOk.setText("Effectuer débit");
			this.btnCancel.setText("Annuler");
			ObservableList<String> listTypesOpesPossibles = FXCollections.observableArrayList();
			listTypesOpesPossibles.addAll(ConstantesIHM.OPERATIONS_DEBIT_GUICHET);

			this.cbTypeOpe.setItems(listTypesOpesPossibles);
			this.cbTypeOpe.getSelectionModel().select(0);
			break;
		case CREDIT:
			this.primaryStage.setTitle("Enregistrer un crédit");
			info = "Compte n°" + this.compteEdite.idNumCompte + "  Solde : " + this.compteEdite.solde
					+ "  Découvert Autorisé : " + Integer.toString(this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);

			this.btnOk.setText("Effectuer crédit");
			this.btnCancel.setText("Annuler");

			listTypesOpesPossibles = FXCollections.observableArrayList();
			listTypesOpesPossibles.addAll(ConstantesIHM.OPERATIONS_CREDIT_GUICHET);

			this.cbTypeOpe.setItems(listTypesOpesPossibles);
			this.cbTypeOpe.getSelectionModel().select(0);
			break;
		case VIREMENT:
			this.primaryStage.setTitle("Enregistrer un virement");
			info = "Compte n°" + this.compteEdite.idNumCompte + "  Solde : " + this.compteEdite.solde
					+ "  Découvert Autorisé : " + Integer.toString(this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);
			this.lblNomOp.setText("Compte destinataire");
			this.btnOk.setText("Effectuer virement");
			this.btnCancel.setText("Annuler");

			Access_BD_CompteCourant ac = new Access_BD_CompteCourant();
			ObservableList<String> listTypesComptesPossibles = FXCollections.observableArrayList();
			ArrayList<CompteCourant> listCompte = new ArrayList<CompteCourant>();

			try {
				listCompte = ac.getCompteCourants(this.compteEdite.idNumCli, true, this.compteEdite.idNumCompte);
			} catch (DataAccessException e) {
			} catch (DatabaseConnexionException e) {
			}
			for (int i = 0; i < listCompte.size(); i++) {
				if (listCompte.get(i) != null) {
					if (listCompte.get(i).estCloture.equals("N")) {
						listTypesComptesPossibles.add("Numéro du Compte = " + listCompte.get(i).idNumCompte
								+ "   Solde : " + listCompte.get(i).solde);
					}
				}
			}

			this.cbTypeOpe.setItems(listTypesComptesPossibles);
			this.cbTypeOpe.getSelectionModel().select(0);
			break;
		}

		// Paramétrages spécifiques pour les chefs d'agences
		if (ConstantesIHM.isAdmin(this.dailyBankState.getEmployeActuel())) {
			// rien pour l'instant
		}

		this.operationResultat = null;
		this.cbTypeOpe.requestFocus();

		this.primaryStage.showAndWait();
		return this.operationResultat;
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions

	@FXML
	private Label lblMessage;
	@FXML
	private Label lblMontant;
	@FXML
	private ComboBox<String> cbTypeOpe;
	@FXML
	private TextField txtMontant;
	@FXML
	private Button btnOk;
	@FXML
	private Button btnCancel;
	@FXML
	private Label lblNomOp;

	@FXML
	private void doCancel() {
		this.operationResultat = null;
		this.primaryStage.close();
	}

	@FXML
	private void doAjouter() {
		switch (this.categorieOperation) {
		case DEBIT:
			// règles de validation d'un débit :
			// - le montant doit être un nombre valide
			// - et si l'utilisateur n'est pas chef d'agence,
			// - le débit ne doit pas amener le compte en dessous de son découvert autorisé
			double montant;

			this.txtMontant.getStyleClass().remove("borderred");
			this.lblMontant.getStyleClass().remove("borderred");
			this.lblMessage.getStyleClass().remove("borderred");
			String info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);

			try {
				montant = Double.parseDouble(this.txtMontant.getText().trim());
				if (montant <= 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
			}
			if (this.compteEdite.solde - montant < this.compteEdite.debitAutorise) {
				info = "Dépassement du découvert ! - Cpt. : " + this.compteEdite.idNumCompte + "  "
						+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
						+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
				this.lblMessage.setText(info);
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.lblMessage.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
			}
			String typeOp = this.cbTypeOpe.getValue();
			this.operationResultat = new Operation(-1, montant, null, null, this.compteEdite.idNumCli, typeOp);
			this.primaryStage.close();
			break;
		case CREDIT:
			// règles de validation d'un débit :
			// - le montant doit être un nombre valide
			// - et si l'utilisateur n'est pas chef d'agence,
			// - le débit ne doit pas amener le compte en dessous de son découvert autorisé
			double montant1;

			this.txtMontant.getStyleClass().remove("borderred");
			this.lblMontant.getStyleClass().remove("borderred");
			this.lblMessage.getStyleClass().remove("borderred");
			info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);

			try {
				montant1 = Double.parseDouble(this.txtMontant.getText().trim());
				if (montant1 <= 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
			}
			typeOp = this.cbTypeOpe.getValue();
			this.operationResultat = new Operation(-1, montant1, null, null, this.compteEdite.idNumCli, typeOp);
			this.primaryStage.close();
			break;
		case VIREMENT:
			// règles de validation d'un débit :
			// - le montant doit être un nombre valide
			// - et si l'utilisateur n'est pas chef d'agence,
			// - le débit ne doit pas amener le compte en dessous de son découvert autorisé
			double montant2;

			this.txtMontant.getStyleClass().remove("borderred");
			this.lblMontant.getStyleClass().remove("borderred");
			this.lblMessage.getStyleClass().remove("borderred");
			info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);

			try {
				montant2 = Double.parseDouble(this.txtMontant.getText().trim());
				if (montant2 <= 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
			}
			String cptChoisi = this.cbTypeOpe.getValue();
			int idNumCpt = -1;
			for (int i = 0; i < cptChoisi.length(); i++) {
				char c = cptChoisi.charAt(i);
				if (Character.isDigit(c)) { // Vérifie si le caractère est un chiffre
					idNumCpt = Character.getNumericValue(c); // Convertit le caractère en chiffre et l'assigne à la
																// variable
					if (i + 1 < cptChoisi.length() && Character.isDigit(cptChoisi.charAt(i + 1))) {
						idNumCpt = idNumCpt * 10 + Character.getNumericValue(cptChoisi.charAt(i + 1));
					}
					break; // Sort de la boucle dès que le premier chiffre est trouvé
				}
			}
			System.out.println("Id " + idNumCpt);
			System.out.println("Id " + idNumCpt);
			System.out.println("Id " + idNumCpt);
			if (idNumCpt != -1) {
				this.operationResultat = new Operation(-1, montant2, null, null, idNumCpt, "Virement Compte à Compte");
				this.primaryStage.close();
				break;
			}
		}
	}
}
