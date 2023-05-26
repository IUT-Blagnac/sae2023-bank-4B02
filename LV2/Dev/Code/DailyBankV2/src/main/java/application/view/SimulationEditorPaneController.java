package application.view;

import java.net.URL;
import java.util.ResourceBundle;
import application.DailyBankState;
import application.control.SimulationEditorPane;
import application.tools.EditionMode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.lang.Math;
import java.text.DecimalFormat;


public class SimulationEditorPaneController implements Initializable {

	// Etat application
	@SuppressWarnings("unused")
	private DailyBankState dailyBankState;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private EditionMode editionMode;
	private SimulationEditorPane sep;

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage,SimulationEditorPane sep, DailyBankState _dbstate) {
		
		this.primaryStage = _primaryStage;
		this.dailyBankState = _dbstate;
		this.sep = sep;
		this.configure();
		
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
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

	// éléments de la scène
	@FXML
	private Label lblMessage;
	
	@FXML
	private TextField montant;

	@FXML
	private TextField annee;

	@FXML
	private TextField TA;

	@FXML
	private TextArea txt;

	@FXML
	private TextField montantAss;

	@FXML
	private TextField TauxAnnuel;

	@FXML
	private TextField DureeMois;
	
	@FXML
	private Button butAssurance;
	
	@FXML
	private Button butEmprunt;

	@FXML
	private Button btnCancel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	/** @autor RAYAN SELLOU
	 * Effectue une simulation d'emprunt en générant un tableau d'amortissement
	 * basé sur les valeurs saisies dans les champs de texte.
	 * Affiche le résultat dans le TextArea.
	 */
	@FXML
	private void doEmprunt() {

	    String aff = "";

	    // Vérifie que les champs de texte ne sont pas vides et contiennent des valeurs numériques valides
	    if (!montant.getText().isEmpty() && isNumber(this.montant) && !annee.getText().isEmpty() && isNumber(annee) && !TA.getText().isEmpty() && isDouble(TA)) {

	        double numTauxAnnuel = Double.parseDouble(TA.getText());
	        int numAnnee= Integer.parseInt(annee.getText());
	        double numMontant= Double.parseDouble(montant.getText());

	        // Initialise la chaîne de caractères pour afficher le tableau 
	        aff ="Année      |      Capital restant dû      |      Intérêts      |      Amortissement du capital      | Annuité\n";
	        
	        //calculs
	        double Capital=numMontant;
	        double interet=(Capital/100)*numTauxAnnuel;
	        double amortissement= (Capital/numAnnee);
	        double annuite= amortissement+interet;


	        double totaI = 0;
	        double totalC = 0;
	        double totA = 0;

	        // Calcule les valeurs pour chaque année et les ajoute à la chaîne d'affichage
	        for (int i =0 ; i<numAnnee; i++) {
	            int bona =i+1;
	            totaI += interet;
	            totalC += amortissement;
	            totA += annuite;


	            aff = aff + "    "+ bona +"          |          " + Capital + "          |          " + interet + "          |          " + amortissement +"          |          " + annuite + "\n";

	            Capital = Capital-amortissement;
	            interet = (Capital/100)*numTauxAnnuel;
	            annuite = amortissement+interet;

	        }

	        // Ajoute la ligne du total à la chaîne d'affichage
	        aff = aff + "    "+ " Total " +"   |   " + "       " + "    |     " + totaI + "     |               " + totalC +"                  |   " + totA + "\n";

	        // Affiche le résultat 
	        txt.setText(aff);
	    }
	}


	/** @autor RAYAN SELLOU 4B
	 * Effectue une simulation d'assurance en générant un tableau d'amortissement
	 * basé sur les valeurs saisies dans les champs de texte.
	 * Affiche le résultat dans le TextArea.
	 */
	@FXML
	private void doAssurance() {
	    DecimalFormat decimalFormat = new DecimalFormat("#.##");
	    String aff = "";

	    // Vérifie que les champs de texte ne sont pas vides et contiennent des valeurs numériques valides
	    if (!montantAss.getText().isEmpty() && isNumber(montantAss) && !TauxAnnuel.getText().isEmpty() && isDouble(TauxAnnuel) && !DureeMois.getText().isEmpty() && isNumber(DureeMois)) {

	        double numTauxAnnuel= Double.parseDouble(TauxAnnuel.getText());
	        int numAnnee= Integer.parseInt(DureeMois.getText());
	        int numMontant= Integer.parseInt(montantAss.getText());
	        double Tapli = numTauxAnnuel/100/12;
	        double tour = numAnnee;
	        numAnnee = numAnnee - numAnnee - numAnnee;

	        // Initialise la chaîne de caractères pour afficher le tableau d'amortissement
	        aff ="Num mois          |          Capital restant dû en début de période          |          Intérêts          |          Montant des intêrets          |          Montant du principal           |          Montant à rembourser (Mensualité)          |          Capital restant dû en fin de période \n";

	        double CapitalDebut = numMontant;

	        // Calcul du montant à rembourser (mensualité) selon la formule de calcul de l'assurance
	        double MontantArembourser = numMontant * (Tapli/ (1-(Math.pow(1+Tapli, numAnnee))));

	        // Calcule les valeurs pour chaque mois et les ajoute à la chaîne d'affichage
	        for (int i =0 ; i<tour; i++) {
	            int bona =i+1;
	            double interet = CapitalDebut*Tapli;
	            double princ= MontantArembourser-interet;
	            double CapitalFin= CapitalDebut-princ;
	            aff = aff + "    "+ bona +"          |          " + decimalFormat.format(CapitalDebut) + "          |          " + decimalFormat.format(interet) + "          |          " + decimalFormat.format(princ) +"          |          " + decimalFormat.format(MontantArembourser) + "          |" + decimalFormat.format(CapitalFin) + "\n";

	            // Met à jour les valeurs pour le prochain mois
	            interet = CapitalDebut*Tapli;
	            CapitalFin = CapitalDebut -princ;
	            princ = MontantArembourser-interet;
	            CapitalDebut = CapitalFin;
	        }

	        // Affiche le résultat dans le TextArea
	        txt.setText(aff);
	    }
	}

	
	private boolean isNumber(TextField message) {
		try {
			Integer.parseInt(message.getText());
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}
	
		
	
	private boolean isDouble(TextField message) {
		try {
			Double.parseDouble(message.getText());
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}




}