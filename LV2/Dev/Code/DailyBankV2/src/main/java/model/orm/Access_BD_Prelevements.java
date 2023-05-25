package model.orm;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.data.Prelevement;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.ManagementRuleViolation;
import model.orm.exception.Order;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
import model.orm.exception.Table;

/**
 * Classe d'accès aux prélèvements en BD Oracle.
 */
public class Access_BD_Prelevements {

	public Access_BD_Prelevements() {

	}

	/**
	 * @author KHALIL Ahmad
	 * 
	 * Récupère la liste des prélèvements pour un compte donné.
	 *
	 * @param idNumCompte L'identifiant du compte.
	 * @return Une liste de prélèvements.
	 * @throws DataAccessException         En cas d'erreur d'accès aux données.
	 * @throws DatabaseConnexionException En cas de problème de connexion à la base de données.
	 */
	public ArrayList<Prelevement> getListePrelevements(int idNumCompte)
			throws DataAccessException, DatabaseConnexionException {

		ArrayList<Prelevement> alResult = new ArrayList<>();

		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "SELECT * FROM PRELEVEMENTAUTOMATIQUE where idNumCompte = ?";
			query += " ORDER BY idprelev";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idNumCompte);

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int idprelevement = rs.getInt("idprelev");
				double montant = rs.getDouble("montant");
				int dateRecur = rs.getInt("daterecurrente");
				String beneficiaire = rs.getString("beneficiaire");
				int idNumComptePrelev = rs.getInt("idnumcompte");

				alResult.add(new Prelevement(idprelevement, montant, dateRecur, beneficiaire, idNumComptePrelev));
			}
			rs.close();
			pst.close();
			return alResult;
		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.SELECT, "Erreur accès", e);
		}
	}

	/**
	 * @author KHALIL Ahmad
	 * 
	 * Supprime un prélèvement de la base de données.
	 *
	 * @param idPrelevement L'identifiant du prélèvement à supprimer.
	 * @throws DataAccessException              En cas d'erreur d'accès aux données.
	 * @throws DatabaseConnexionException      En cas de problème de connexion à la base de données.
	 * @throws RowNotFoundOrTooManyRowsException Si aucune ligne n'est trouvée ou si plusieurs lignes sont trouvées lors de la suppression.
	 */
	public void supprimerPrelevement(int idPrelevement)
			throws DataAccessException, DatabaseConnexionException, RowNotFoundOrTooManyRowsException {
		try {
			Connection con = LogToDatabase.getConnexion();

			String query = "DELETE FROM PRELEVEMENTAUTOMATIQUE WHERE idprelev = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idPrelevement);

			int result = pst.executeUpdate();
			pst.close();
			if (result < 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.DELETE,
						"Delete impossible de la ligne : Access_BD_Prelevements.supprimerPrelevement", null, result);
			}
			con.commit();
		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.DELETE, "Erreur accès", e);
		}
	}

	/**
	 * @author KHALIL Ahmad
	 * 
	 * Exécute tous les prélèvements automatiques pour un compte donné et qui sont à cette date.
	 *
	 * @param idNumCompte L'identifiant du compte.
	 * @return Le résultat de l'exécution.
	 * @throws DatabaseConnexionException En cas de problème de connexion à la base de données.
	 * @throws ManagementRuleViolation   En cas de violation d'une règle de gestion.
	 * @throws DataAccessException       En cas d'erreur d'accès aux données.
	 */
	public String executerAll(int idNumCompte)
			throws DatabaseConnexionException, ManagementRuleViolation, DataAccessException {
		try {
			Connection con = LogToDatabase.getConnexion();
			CallableStatement call;

			String q = "{call EXECUTERPRELEVMANUELLE (?, ?)}";

			call = con.prepareCall(q);

			call.setInt(1, idNumCompte);

			call.registerOutParameter(2, java.sql.Types.VARCHAR);

			call.execute();

			String res = call.getString(2);

			return res;
		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.OTHER, "Erreur accès", e);
		}
	}

	/**
	 * @author KHALIL Ahmad
	 * 
	 * Ajoute un prélèvement à la base de données.
	 *
	 * @param prlv Le prélèvement à ajouter.
	 * @throws RowNotFoundOrTooManyRowsException En cas d'impossibilité de trouver la ligne ou si plusieurs lignes sont trouvées lors de l'ajout.
	 * @throws DataAccessException              En cas d'erreur d'accès aux données.
	 * @throws DatabaseConnexionException      En cas de problème de connexion à la base de données.
	 * @throws ManagementRuleViolation          En cas de violation d'une règle de gestion.
	 */
	public void addPrelevement(Prelevement prlv) throws RowNotFoundOrTooManyRowsException, DataAccessException,
			DatabaseConnexionException, ManagementRuleViolation {
		try {
			Connection con = LogToDatabase.getConnexion();

			String query = "INSERT INTO PRELEVEMENTAUTOMATIQUE VALUES (seq_id_prelevauto.NEXTVAL, ?, ?, ?, ?)";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setDouble(1, prlv.montant);
			pst.setInt(2, prlv.dateRecurrente);
			pst.setString(3, prlv.beneficiaire);
			pst.setInt(4, prlv.idNumCompte);

			int result = pst.executeUpdate();
			
			query = "SELECT seq_id_prelevauto.CURRVAL from DUAL";

			PreparedStatement pst2 = con.prepareStatement(query);

			ResultSet rs = pst2.executeQuery();
			rs.next();
			int idprelev = rs.getInt(1);
			prlv.idprelev = idprelev;
			
			pst.close();
			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.INSERT,
						"Update anormal (update de moins ou plus d'une ligne)", null, result);
			}
			con.commit();
		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.INSERT, "Erreur ACCES BD COMPTECOURANT ADDCOMPTE",
					e);
		}
	}

	/**
	 * @author KHALIL Ahmad
	 * 
	 * Met à jour un prélèvement dans la base de données.
	 *
	 * @param pfPrelevement Le prélèvement à mettre à jour.
	 * @throws RowNotFoundOrTooManyRowsException Si aucune ligne ou plusieurs lignes correspondantes sont trouvées lors de la mise à jour.
	 * @throws DataAccessException              En cas d'erreur d'accès aux données.
	 * @throws DatabaseConnexionException       En cas d'erreur de connexion à la base de données.
	 */
	public void updatePrelevement(Prelevement pfPrelevement)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {
			Connection con = LogToDatabase.getConnexion();

			String query = "UPDATE PRELEVEMENTAUTOMATIQUE SET montant = ?, daterecurrente = ?, beneficiaire = ? "
					+ "WHERE idprelev = ? ";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setDouble(1, pfPrelevement.montant);
			pst.setInt(2, pfPrelevement.dateRecurrente);
			pst.setString(3, pfPrelevement.beneficiaire);
			pst.setInt(4, pfPrelevement.idprelev);

			int result = pst.executeUpdate();
			pst.close();
			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.UPDATE,
						"Update anormal (update de moins ou plus d'une ligne)", null, result);
			}
			con.commit();
		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.UPDATE, "Erreur accès", e);
		}
	}
}
