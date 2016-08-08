package RE.impl.ex3;

import java.util.LinkedList;
import java.util.List;

import RE.api.Configuration;
import RE.api.Rule;
import RE.rep.SentenceWithDependenciesAndNER;

/**
 * Ex3 is best configuration of rules.
 * @author hazoomm 201337904
 *
 */
public class Ex3BestConfiguration implements Configuration<SentenceWithDependenciesAndNER> {
	
	private LinkedList<Rule<SentenceWithDependenciesAndNER>> configuration;
	
	public List<Rule<SentenceWithDependenciesAndNER>> getConfiguration() {
		return configuration;
	}	
	
	public void generateConfiguration() {
		configuration = new LinkedList<Rule<SentenceWithDependenciesAndNER>>();

		// Kill
		configuration.add(new Ex3FullConfiguration.KillXKillingOfYPrepIn());
		configuration.add(new Ex3FullConfiguration.KillXFireAtYPrepAt());
		configuration.add(new Ex3FullConfiguration.KillXWasAssasinatedByYPrepBy());
		configuration.add(new Ex3FullConfiguration.KillXForTheAssassinationOfYPrepFor());
		configuration.add(new Ex3FullConfiguration.KillXConvincedForAssasinatingYPrepcOf());
		configuration.add(new Ex3FullConfiguration.KillXKilledYDopj());
		configuration.add(new Ex3FullConfiguration.KillXSAssassinYAppos());
		configuration.add(new Ex3FullConfiguration.KillXWasShotByYAgent());
		configuration.add(new Ex3FullConfiguration.KillXTheAssassinOfYAppos());
		configuration.add(new Ex3FullConfiguration.KillXSAssassinationYPoss());
		configuration.add(new Ex3FullConfiguration.KillXSlayingYNn());
		
		// Work_For
		configuration.add(new Ex3FullConfiguration.WorkForXForYPrep_for());
		configuration.add(new Ex3FullConfiguration.WorkForXOfYPrep_of());
		configuration.add(new Ex3FullConfiguration.WorkForXYProfessorAppos());
		configuration.add(new Ex3FullConfiguration.WorkForXOfficialYAppos());
		configuration.add(new Ex3FullConfiguration.WorkForXFoundedByYAgent());
		configuration.add(new Ex3FullConfiguration.WorkForXSYPoss());
		configuration.add(new Ex3FullConfiguration.WorkForXSpokesmanYNn());
		configuration.add(new Ex3FullConfiguration.WorkForXOfficialYDep());
		configuration.add(new Ex3FullConfiguration.WorkForXFirstYFirstEmployee());
		configuration.add(new Ex3FullConfiguration.WorkForXServedInYPrep_In());
		configuration.add(new Ex3FullConfiguration.WorkForXRepublicanOfYNn());
		configuration.add(new Ex3FullConfiguration.WorkForXDirectorOfY());
		configuration.add(new Ex3FullConfiguration.WorkForXDirectsY());
		configuration.add(new Ex3FullConfiguration.WorkForAXProfessorYDep());
		configuration.add(new Ex3FullConfiguration.WorkForXOfTheYPrep_of());
		
		// OrgBased_In
		configuration.add(new Ex3FullConfiguration.OrgBasedInXInY());
		configuration.add(new Ex3FullConfiguration.OrgBasedInXInYPrep_in());
		configuration.add(new Ex3FullConfiguration.OrgBasedYFirstAP());
		configuration.add(new Ex3FullConfiguration.OrgBasedYLastAP());
		
		// Live_In
		configuration.add(new Ex3FullConfiguration.LiveInNamePlace());
		configuration.add(new Ex3FullConfiguration.LiveInPlaceName());
		
		// Located_In
		configuration.add(new Ex3FullConfiguration.LocatedInXPlaceCommaYPlace());
		configuration.add(new Ex3FullConfiguration.LocatedInXPlaceInYPlace());
	}
}
