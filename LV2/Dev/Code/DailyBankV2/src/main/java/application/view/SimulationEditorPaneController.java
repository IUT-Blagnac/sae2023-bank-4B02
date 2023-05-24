package application.view;

import java.net.URL;
import java.util.ResourceBundle;
import application.DailyBankState;
import application.control.SimulationEditorPane;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Employe;
import model.data.Prelevement;

import java.lang.Math;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;


public class SimulationEditorPaneController implements Initializable {

	// Etat application
	@SuppressWarnings("unused")
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private EditionMode editionMode;
	private SimulationEditorPane sep;

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage,SimulationEditorPane sep, DailyBankState _dbstate) {
		
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
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

	// Attributs de la scene + actions
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

	@FXML
	private void doSimul() {

		String aff = "";

		if (!montant.getText().isEmpty() && isNumber(this.montant) && !annee.getText().isEmpty() && isNumber(annee) && !TA.getText().isEmpty() && isDouble(TA)) {

			double numTA = Double.parseDouble(TA.getText());
			int numA= Integer.parseInt(annee.getText());
			double numMontant= Double.parseDouble(montant.getText());

			aff ="Année      |      Capital restant dû      |      Intérêts      |      Amortissement du capital      | Annuité\n";

			double Capital=numMontant;
			double interet=(Capital/100)*numTA;
			double amor= (Capital/numA);
			double annuite= amor+interet;


			double totI = 0;
			double totC = 0;
			double totA = 0;
			for (int i =0 ; i<numA; i++) {
				int bona =i+1;
				totI += interet;
				totC += amor;
				totA += annuite;
				
				
				aff = aff + "    "+ bona +"             |   " + Capital + "    |     " + interet + "     |               " + amor +"                  |   " + annuite + "\n";

				Capital = Capital-amor;
				interet = (Capital/100)*numTA;
				annuite = amor+interet;


			}


			aff = aff + "    "+ " Total " +"   |   " + "       " + "    |     " + totI + "     |               " + totC +"                  |   " + totA + "\n";
			txt.setText(aff);
			
		}

	}


	


	@FXML
    private void doAss() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String aff = "";

        if (!montantAss.getText().isEmpty() && isNumber(montantAss) && !TauxAnnuel.getText().isEmpty() && isDouble(TauxAnnuel) && !DureeMois.getText().isEmpty() && isNumber(DureeMois)) {

            double numTA= Double.parseDouble(TauxAnnuel.getText());
            int numA= Integer.parseInt(DureeMois.getText());
            int numMontant= Integer.parseInt(montantAss.getText());
            double Tapl = numTA/100/12;
            double tour = numA;
            numA = numA - numA - numA;

            aff ="Num mois | Capital restant dû en début de période |Intérêts | Montant des intêrets | Montant du principal  |  Montant à rembourser (Mensualité) | Capital restant dû en fin de période \n";

            double CapDeb = numMontant;
            

            double MontantArembourser = numMontant * (Tapl/ (1-(Math.pow(1+Tapl, numA))));


            

            


            for (int i =0 ; i<tour; i++) {
                int bona =i+1;
                double interet = CapDeb*Tapl;
                double princ= MontantArembourser-interet;
                double CapFin= CapDeb-princ;
                aff = aff + "    "+ bona +"             |                   " + decimalFormat.format(CapDeb) + "             |                           " + decimalFormat.format(interet) + "     |               " + decimalFormat.format(princ) +"                  |   " + decimalFormat.format(MontantArembourser) + "  |    " + decimalFormat.format(CapFin) + "\n";






                interet = CapDeb*Tapl;
                CapFin = CapDeb -princ;
                princ = MontantArembourser-interet;
                CapDeb = CapFin;


            }
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

	private boolean isFloat(TextField message) {
		try {
			Float.parseFloat(message.getText());
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