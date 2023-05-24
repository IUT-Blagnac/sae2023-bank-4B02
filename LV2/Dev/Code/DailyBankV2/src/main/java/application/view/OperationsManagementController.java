package application.view;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.*;

import application.DailyBankState;
import application.control.OperationsManagement;
import application.tools.AlertUtilities;
import application.tools.NoSelectionModel;
import application.tools.PairsOfValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
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

	/**
	 * Initialise le contexte de la fenêtre. Définit les différents attributs
	 * utilisés par la fenêtre avec les valeurs passées en paramètres. Appelle la
	 * méthode configure() pour effectuer la configuration initiale de la fenêtre.
	 * 
	 * @param _containingStage la fenêtre principale qui contient cette fenêtre de
	 *                         dialogue
	 * @param _om              le controller OperationsManagement utilisé pour
	 *                         effectuer les opérations sur les comptes
	 * @param _dbstate         l'état bancaire quotidien utilisé pour les
	 *                         informations de compte
	 * @param client           le client associé au compte
	 * @param compte           le compte courant concerné
	 */
	public void initContext(Stage _containingStage, OperationsManagement _om, DailyBankState _dbstate, Client client,
			CompteCourant compte) {
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.omDialogController = _om;
		this.clientDuCompte = client;
		this.compteConcerne = compte;
		this.configure();
	}

	/**
	 * Effectue la configuration initiale de la fenêtre. Configure la fermeture de
	 * la fenêtre principale lors de la demande de fermeture. Initialise la liste
	 * observable des opérations et la lie à la liste affichée dans la fenêtre. Met
	 * à jour les informations du compte client. Valide l'état des composants en
	 * fonction du compte.
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.oListOperations = FXCollections.observableArrayList();
		this.lvOperations.setItems(this.oListOperations);
		this.lvOperations.setSelectionModel(new NoSelectionModel<Operation>());
		this.updateInfoCompteClient();
		this.validateComponentState();
	}

	/**
	 * Affiche la fenêtre de dialogue et attend jusqu'à ce qu'elle soit fermée.
	 */
	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	/**
	 * Gère l'événement de fermeture de la fenêtre. Appelle la méthode doCancel()
	 * pour effectuer les actions d'annulation. Consomme l'événement pour empêcher
	 * la fermeture de la fenêtre.
	 * 
	 * @param e l'événement de fermeture de la fenêtre
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
	private ListView<Operation> lvOperations;
	@FXML
	private Button btnDebit;
	@FXML
	private Button btnCredit;
	@FXML
	private Button btnVirement;

	/**
	 * Action exécutée lors de l'appui sur le bouton Annuler. Ferme la fenêtre
	 * principale (primaryStage).
	 */
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	/**
	 * Action exécutée lors de l'appui sur le bouton Débit. Enregistre une opération
	 * de débit en utilisant le controller omDialogController. Si l'opération est
	 * enregistrée avec succès, met à jour les informations du compte client et
	 * remet l'état des composants à leur état initiale.
	 */
	@FXML
	private void doDebit() {
		Operation op = this.omDialogController.enregistrerDebit();
		if (op != null) {
			this.updateInfoCompteClient();
			this.validateComponentState();
		}
	}

	/**
	 * @autor RAYAN SELLOU 4B 
	 * Ici on créer la fonction permettant de gérer les crédits
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
	 *         Cette méthode est appelée lorsqu'on souhaite effectuer un virement
	 *         sur le compte client. Elle utilise la méthode enregistrerVirement()
	 *         de la classe omDialogController pour enregistrer le virement. Si le
	 *         virement est enregistré avec succès (opération différente de null),
	 *         elle met à jour les informations du compte client en appelant la
	 *         méthode updateInfoCompteClient() et met à jour l'état des composants
	 *         en appelant la méthode validateComponentState().
	 */
	@FXML
	private void doVirement() {
		Operation op = this.omDialogController.enregistrerVirement();
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
	 *         Cette méthode est appelée lorsque le bouton de gestion des
	 *         prélèvements est appuyé par l'utilisateur.
	 */
	@FXML
	private void doPrelevement() {
		this.omDialogController.afficherPrelevements();
		this.updateInfoCompteClient();
		this.validateComponentState();
	}

	/**
	 * Valide l'état des composants en fonction de l'état du compte concerné. Si le
	 * compte est clôturé, les boutons de crédit, débit et virement sont désactivés.
	 * Si le compte est ouvert, les boutons de crédit, débit et virement sont
	 * activés.
	 */
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

	/**
	 * Met à jour les informations du compte client. Récupère les opérations et le
	 * solde du compte à partir du controller omDialogController. Met à jour les
	 * attributs compteConcerne et listeOP. Met à jour les labels lblInfosClient et
	 * lblInfosCompte avec les informations du compte. Efface la liste
	 * oListOperations et ajoute les nouvelles opérations. Appelle la méthode
	 * validateComponentState() pour valider l'état des composants en fonction du
	 * compte.
	 */
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
	
	@FXML
	private Button btnPDF;
	
	
	/** @autor RAYAN SELLOU 4B
	 * Génère un relevé PDF pour le compte courant et l'ouvre avec l'application par défaut.
	 * Le relevé est enregistré dans le dossier "ReleveComptes" avec le nom "RelevéCompteN°[numéroCompte].pdf".
	 * Affiche également une boîte de dialogue pour informer l'utilisateur du succès de la création du relevé.
	 */
	@FXML
	private void doRelevePDF() {
	    Document document = new Document();

	    try {
	        // Définition du nom de fichier pour le relevé
	        String nom = "ReleveComptes/RelevéCompteN°" + this.compteConcerne.idNumCompte + ".pdf";

	        // Création de l'instance PdfWriter avec le document et le fichier de sortie
	        PdfWriter.getInstance(document, new FileOutputStream(nom));
	        document.open();
	        
	        // Ajout des informations sur le client et le compte dans le relevé
	        Paragraph infoClient = new Paragraph("N°Client : " + this.compteConcerne.idNumCli + "  | Relevé du compte " + this.compteConcerne.toString() + "\n\n");
	        document.add(infoClient);
	        
	        // Ajout de la liste des opérations dans le relevé
	        document.add(new Paragraph("Liste des opérations :"));
	        for (int i = 0; i < listeOP.size(); i++) {
	            document.add(new Paragraph(listeOP.get(i).toString()));
	        }
	        
	        document.close();
	        
	        // Ouvre le fichier PDF avec l'application par défaut
	        try {
	            Desktop.getDesktop().open(new File(nom));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
	        // Affiche une boîte de dialogue d'information sur la réussite de la création du relevé
	        AlertUtilities.showAlert(this.primaryStage, "Création du relevé réussie", "Le relevé du compte n°" + this.compteConcerne.idNumCompte + " a bien été créé\n dans le dossier ReleveComptes !", null, AlertType.INFORMATION);
	    } catch (DocumentException e) {
	        e.printStackTrace();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
	}
}
