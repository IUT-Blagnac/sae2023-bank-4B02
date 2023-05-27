package application.batch;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import model.orm.Access_BD_Prelevements;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.ManagementRuleViolation;

/*
 * Classe qui gère le batch du prélèvement automatique.
 * 
 */
public class PrelevementAuto implements org.quartz.Job {
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			
			Access_BD_Prelevements ac = new Access_BD_Prelevements();
			
			ac.executerPrelevementAuto();
			
		} catch (DataAccessException | DatabaseConnexionException e) {
			e.printStackTrace();
		} catch (ManagementRuleViolation e) {
			e.printStackTrace();
		}
	}
}
