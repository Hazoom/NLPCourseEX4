package RE.impl.ex3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import RE.api.Configuration;
import RE.api.Rule;
import RE.rep.POS;
import RE.rep.Relation;
import RE.rep.Sentence;
import RE.rep.SentenceWithDependenciesAndNER;
import RE.rep.TokenWithNER;
import RE.rep.TokenWithNER.NamedEntityType;

/**
 * Ex3 is full configuration of rules.
 * @author hazoomm 201337904
 *
 */
public class Ex3FullConfiguration implements Configuration<SentenceWithDependenciesAndNER> {
	private LinkedList<Rule<SentenceWithDependenciesAndNER>> configuration;
	
	@Override
	public List<Rule<SentenceWithDependenciesAndNER>> getConfiguration() {
		return configuration;
	}
	
	@Override
	public void generateConfiguration() {
		configuration = new LinkedList<Rule<SentenceWithDependenciesAndNER>>();
		
		// Kill
		configuration.add(new KillXKillingOfYPrepIn());
		configuration.add(new KillXFireAtYPrepAt());
		configuration.add(new KillXWasAssasinatedByYPrepBy());
		configuration.add(new KillXForTheAssassinationOfYPrepFor());
		configuration.add(new KillXConvincedForAssasinatingYPrepcOf());
		configuration.add(new KillXKilledYDopj());
		configuration.add(new KillXSAssassinYAppos());
		configuration.add(new KillXWasShotByYAgent());
		configuration.add(new KillXTheAssassinOfYAppos());
		configuration.add(new KillXSAssassinationYPoss());
		configuration.add(new KillXSlayingYNn());
		
		// Work_For
		configuration.add(new WorkForXForYPrep_for());
		configuration.add(new WorkForXOfYPrep_of());
		configuration.add(new WorkForXYProfessorAppos());
		configuration.add(new WorkForXOfficialYAppos());
		configuration.add(new WorkForXFoundedByYAgent());
		configuration.add(new WorkForXSYPoss());
		configuration.add(new WorkForXSpokesmanYNn());
		configuration.add(new WorkForXOfficialYDep());
		configuration.add(new WorkForXFirstYFirstEmployee());
		configuration.add(new WorkForXServedInYPrep_In());
		configuration.add(new WorkForXRepublicanOfYNn());
		configuration.add(new WorkForXDirectorOfY());
		configuration.add(new WorkForXDirectsY());
		configuration.add(new WorkForAXProfessorYDep());
		configuration.add(new WorkForXOfTheYPrep_of());
		
		// OrgBased_In
		configuration.add(new OrgBasedInXInY());
		configuration.add(new OrgBasedInXInYPrep_in());
		configuration.add(new OrgBasedYFirstAP());
		configuration.add(new OrgBasedYLastAP());
		
		// Live_In
		configuration.add(new LiveInNamePlace());
		configuration.add(new LiveInPlaceName());
		
		// Located_In
		configuration.add(new LocatedInXPlaceCommaYPlace());
		configuration.add(new LocatedInXPlaceInYPlace());
		configuration.add(new LocatedInXPlaceLastInYPlace());
	}

	/*
	 * --------------------------------------------------------------------------------
	 * 
	 *                                    Kill part
	 * 
	 * --------------------------------------------------------------------------------
	 */
	
	/**
	 * Here we taking cases of "X... killing of Y" with the dependency prep_in.
	 * @author hazoomm 201337904
	 *
	 */
	public static class KillXKillingOfYPrepIn implements Rule<SentenceWithDependenciesAndNER> {

		// Kill nouns
		private ArrayList<String> lstKillNouns = new ArrayList<String>();
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			lstKillNouns.add("killing");
			lstKillNouns.add("murdering");
			lstKillNouns.add("shooting");
			lstKillNouns.add("assassination");
			lstKillNouns.add("slaying");
			
			LinkedList<Relation> KillXKillYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (KillXKillYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundKillNoun = false;
			
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				
				if (sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.prep_of)) {

					// One of the kill nouns
					if (lstKillNouns.contains(governon.getLemma()) && 
							(depentent.getNamedEntityType().equals(NamedEntityType.PERSON) ||
							 depentent.getPos().equals(POS.NNP))) {
						strSentenceID = governon.getSentenceId();
						bFoundKillNoun = true;

						// Save Y index
						nY = depentent.getTokenId();
					}
				} else if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nsubj) ||
						sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nsubjpass)) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.PERSON) ||
						 depentent.getPos().equals(POS.NNP))) {
					
					// Save X index
					nX = depentent.getTokenId();
				}
			}
			
			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundKillNoun && !strX.equals("") && !strY.equals("")) {
				KillXKillYSet.add(new Relation(Relation.RelationName.Kill, strX, strY, strSentenceID));
			}
			
			return (KillXKillYSet);
		}
		
	}
	
	/**
	 * Here we taking cases of "X fired at Y" with the dependency prep_at.
	 * @author hazoomm 201337904
	 *
	 */
	public static class KillXFireAtYPrepAt implements Rule<SentenceWithDependenciesAndNER> {

		// Kill verbs
		private ArrayList<String> lstKillVerbs = new ArrayList<String>();
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			lstKillVerbs.add("fire");
			
			LinkedList<Relation> KillXKillYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (KillXKillYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundKillVerb = false;
			
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				
				// prep_at
				if (sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.prep_at)) {

					// One of the kill verbs
					if (lstKillVerbs.contains(governon.getLemma()) && 
							(depentent.getNamedEntityType().equals(NamedEntityType.PERSON) ||
							 (depentent.getPos().equals(POS.NNP)))) {
						strSentenceID = governon.getSentenceId();
						bFoundKillVerb = true;

						// Save Y index
						nY = depentent.getTokenId();
					}
				// nsubj || nsubjpass
				} else if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nsubj) ||
						sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nsubjpass)) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.PERSON) ||
						 (depentent.getPos().equals(POS.NNP)))) {
					
					// Save X index
					nX = depentent.getTokenId();
				}
			}
			
			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundKillVerb && !strX.equals("") && !strY.equals("")) {
				KillXKillYSet.add(new Relation(Relation.RelationName.Kill, strX, strY, strSentenceID));
			}
			
			return (KillXKillYSet);
		}
		
	}
	
	/**
	 * Here we taking cases of "X... assassination by Y" with the dependency prep_by.
	 * @author hazoomm 201337904
	 *
	 */
	public static class KillXWasAssasinatedByYPrepBy implements Rule<SentenceWithDependenciesAndNER> {

		// Kill nouns
		private ArrayList<String> lstKillNouns = new ArrayList<String>();
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			lstKillNouns.add("killing");
			lstKillNouns.add("murdering");
			lstKillNouns.add("shooting");
			lstKillNouns.add("assassination");
			lstKillNouns.add("slaying");
			
			LinkedList<Relation> KillXKillYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (KillXKillYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundKillNoun = false;
			
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				
				if (sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.prep_by)) {

					// One of the kill nouns
					if (lstKillNouns.contains(governon.getLemma()) && 
							(depentent.getNamedEntityType().equals(NamedEntityType.PERSON) ||
							 depentent.getPos().equals(POS.NNP))) {
						strSentenceID = governon.getSentenceId();
						bFoundKillNoun = true;

						// Save X index
						nX = depentent.getTokenId();
					}
				} else if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nn)) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.PERSON) ||
						 depentent.getPos().equals(POS.NNP)) &&
						 lstKillNouns.contains(governon.getLemma())) {
					
					// Save Y index
					nY = depentent.getTokenId();
				}
			}
			
			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundKillNoun && !strX.equals("") && !strY.equals("")) {
				KillXKillYSet.add(new Relation(Relation.RelationName.Kill, strX, strY, strSentenceID));
			}
			
			return (KillXKillYSet);
		}
		
	}
	
	/**
	 * Here we taking cases of "X... for the assassination of Y" with the dependency prep_for.
	 * @author hazoomm 201337904
	 *
	 */
	public static class KillXForTheAssassinationOfYPrepFor implements Rule<SentenceWithDependenciesAndNER> {

		// Kill nouns
		private ArrayList<String> lstKillNouns = new ArrayList<String>();
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			lstKillNouns.add("killing");
			lstKillNouns.add("murdering");
			lstKillNouns.add("shooting");
			lstKillNouns.add("assassination");
			lstKillNouns.add("slaying");
			
			LinkedList<Relation> KillXKillYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (KillXKillYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundKillNoun = false;
			
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				
				if (sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.prep_for)) {

					// One of the kill nouns
					if (lstKillNouns.contains(depentent.getLemma())) {
						strSentenceID = governon.getSentenceId();
						bFoundKillNoun = true;
					}
				} else if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nsubjpass)||
						(sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nsubj))) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.PERSON) ||
						 depentent.getPos().equals(POS.NNP))) {
					
					// Save X index
					nX = depentent.getTokenId();
				} else if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.prep_of)) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.PERSON) ||
						 depentent.getPos().equals(POS.NNP)) &&
						 lstKillNouns.contains(governon.getLemma())) {
					
					// Save Y index
					nY = depentent.getTokenId();
				}
			}
			
			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundKillNoun && !strX.equals("") && !strY.equals("")) {
				KillXKillYSet.add(new Relation(Relation.RelationName.Kill, strX, strY, strSentenceID));
			}
			
			return (KillXKillYSet);
		}
		
	}
	
	/**
	 * Here we taking cases of "X... convinced for assassination Y" with the dependency prepc_of.
	 * @author hazoomm 201337904
	 *
	 */
	public static class KillXConvincedForAssasinatingYPrepcOf implements Rule<SentenceWithDependenciesAndNER> {

		// Kill nouns
		private ArrayList<String> lstKillNouns = new ArrayList<String>();
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			lstKillNouns.add("kill");
			lstKillNouns.add("murder");
			lstKillNouns.add("shoot");
			lstKillNouns.add("assassinate");
			lstKillNouns.add("slay");
			
			LinkedList<Relation> KillXKillYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (KillXKillYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundKillNoun = false;
			
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				
				if (sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.prepc_of)) {

					// One of the kill nouns
					if (lstKillNouns.contains(depentent.getLemma())) {
						strSentenceID = governon.getSentenceId();
						bFoundKillNoun = true;
					}
				} else if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nsubjpass) ||
						sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nsubj)) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.PERSON) ||
						 (depentent.getPos().equals(POS.NNP)))) {
					
					// Save X index
					nX = depentent.getTokenId();
				} else if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.dobj)) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.PERSON) ||
						 depentent.getPos().equals(POS.NNP)) &&
						 lstKillNouns.contains(governon.getLemma())) {
					
					// Save Y index
					nY = depentent.getTokenId();
				}
			}
			
			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundKillNoun && !strX.equals("") && !strY.equals("")) {
				KillXKillYSet.add(new Relation(Relation.RelationName.Kill, strX, strY, strSentenceID));
			}
			
			return (KillXKillYSet);
		}
		
	}
	
	/**
	 * Here we taking cases of "X... killed Y" with the dependency dobj.
	 * @author hazoomm 201337904
	 *
	 */
	public static class KillXKilledYDopj implements Rule<SentenceWithDependenciesAndNER> {

		// Kill nouns
		private ArrayList<String> lstKillNouns = new ArrayList<String>();
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			lstKillNouns.add("kill");
			lstKillNouns.add("murder");
			lstKillNouns.add("shoot");
			lstKillNouns.add("assassin");
			lstKillNouns.add("slay");
			lstKillNouns.add("gun");
			
			LinkedList<Relation> KillXKillYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (KillXKillYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundKillNoun = false;
			
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				
				if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nsubjpass)||
						(sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nsubj))) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.PERSON) ||
						 depentent.getPos().equals(POS.NNP))) {
					
					strSentenceID = governon.getSentenceId();
					bFoundKillNoun = true;
					
					// Save X index
					nX = depentent.getTokenId();
				} else if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.dobj)) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.PERSON) ||
						 depentent.getPos().equals(POS.NNP)) &&
						 lstKillNouns.contains(governon.getLemma())) {
					
					// Save Y index
					nY = depentent.getTokenId();
				}
			}
			
			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundKillNoun && !strX.equals("") && !strY.equals("")) {
				KillXKillYSet.add(new Relation(Relation.RelationName.Kill, strX, strY, strSentenceID));
			}
			
			return (KillXKillYSet);
		}
		
	}
	
	/**
	 * Here we taking cases of "X's assassin Y" with the dependency appos.
	 * @author hazoomm 201337904
	 *
	 */
	public static class KillXSAssassinYAppos implements Rule<SentenceWithDependenciesAndNER> {

		// Kill nouns
		private ArrayList<String> lstKillNouns = new ArrayList<String>();
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			lstKillNouns.add("kill");
			lstKillNouns.add("murder");
			lstKillNouns.add("shoot");
			lstKillNouns.add("assassin");
			lstKillNouns.add("slay");
			
			LinkedList<Relation> KillXKillYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (KillXKillYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundKillNoun = false;
			
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				
				if (sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.poss) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.PERSON) ||
						 depentent.getPos().equals(POS.NNP)) &&
						 lstKillNouns.contains(governon.getLemma())) {
					
					strSentenceID = governon.getSentenceId();
					bFoundKillNoun = true;
					
					// Save X index
					nX = depentent.getTokenId();
				} else if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.appos)) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.PERSON) ||
						 depentent.getPos().equals(POS.NNP)) &&
						 lstKillNouns.contains(governon.getLemma())) {
					
					// Save Y index
					nY = depentent.getTokenId();
				}
			}
			
			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundKillNoun && !strX.equals("") && !strY.equals("")) {
				KillXKillYSet.add(new Relation(Relation.RelationName.Kill, strY, strX, strSentenceID));
			}
			
			return (KillXKillYSet);
		}
		
	}
	
	/**
	 * Here we taking cases of "X was shot by Y" with the dependency agent.
	 * @author hazoomm 201337904
	 *
	 */
	public static class KillXWasShotByYAgent implements Rule<SentenceWithDependenciesAndNER> {

		// Kill verbs
		private ArrayList<String> lstKillVerbs = new ArrayList<String>();
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			lstKillVerbs.add("shoot");
			lstKillVerbs.add("kill");
			lstKillVerbs.add("die");
			lstKillVerbs.add("assassinate");
			lstKillVerbs.add("murder");
			
			LinkedList<Relation> KillXKillYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (KillXKillYSet);

			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundKillVerb = false;
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				
				// agent
				if (sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.agent)) {

					// One of the kill verbs
					if (lstKillVerbs.contains(governon.getLemma()) && 
							(depentent.getNamedEntityType().equals(NamedEntityType.PERSON) ||
							 (depentent.getPos().equals(POS.NNP)))) {
						strSentenceID = governon.getSentenceId();
						bFoundKillVerb = true;

						// Save X index
						nX = depentent.getTokenId();
					}
				// nsubj || nsubjpass
				} else if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nsubj) ||
						sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nsubjpass)) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.PERSON) ||
						 (depentent.getPos().equals(POS.NNP)))) {
					
					// Save Y index
					nY = depentent.getTokenId();
				}
			}
			
			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundKillVerb && !strX.equals("") && !strY.equals("")) {
				KillXKillYSet.add(new Relation(Relation.RelationName.Kill, strX, strY, strSentenceID));
			}
			
			return (KillXKillYSet);
		}
		
	}
	
	/**
	 * Here we taking cases of "X the assassin of Y" with the dependency appos.
	 * @author hazoomm 201337904
	 *
	 */
	public static class KillXTheAssassinOfYAppos implements Rule<SentenceWithDependenciesAndNER> {

		// Kill verbs
		private ArrayList<String> lstKillVerbs = new ArrayList<String>();
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			lstKillVerbs.add("shoot");
			lstKillVerbs.add("kill");
			lstKillVerbs.add("assassin");
			lstKillVerbs.add("murder");
			
			LinkedList<Relation> KillXKillYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (KillXKillYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundKillVerb = false;
			
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				
				// appos
				if (sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.appos)) {

					// One of the kill verbs
					if (lstKillVerbs.contains(depentent.getLemma()) && 
							(governon.getNamedEntityType().equals(NamedEntityType.PERSON) ||
							 (governon.getPos().equals(POS.NNP)))) {
						strSentenceID = depentent.getSentenceId();
						bFoundKillVerb = true;

						// Save X index
						nX = governon.getTokenId();
					}
				// nsubj || nsubjpass
				} else if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.prep_of)) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.PERSON) ||
						 (depentent.getPos().equals(POS.NNP)))) {
					
					// Save Y index
					nY = depentent.getTokenId();
				}
			}
			
			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundKillVerb && !strX.equals("") && !strY.equals("")) {
				KillXKillYSet.add(new Relation(Relation.RelationName.Kill, strX, strY, strSentenceID));
			}
			
			return (KillXKillYSet);
		}
		
	}
	
	/**
	 * Here we taking cases of "X's assassination Y" with the dependency poss.
	 * @author hazoomm 201337904
	 *
	 */
	public static class KillXSAssassinationYPoss implements Rule<SentenceWithDependenciesAndNER> {

		// Kill nouns
		private ArrayList<String> lstKillNouns = new ArrayList<String>();
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			lstKillNouns.add("kill");
			lstKillNouns.add("murder");
			lstKillNouns.add("shoot");
			lstKillNouns.add("assassination");
			lstKillNouns.add("slay");
			
			LinkedList<Relation> KillXKillYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (KillXKillYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundKillNoun = false;
			
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				
				if (sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.poss) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.PERSON) ||
						 depentent.getPos().equals(POS.NNP)) &&
						 lstKillNouns.contains(governon.getLemma())) {
					
					strSentenceID = governon.getSentenceId();
					bFoundKillNoun = true;
					
					// Save X index
					nX = depentent.getTokenId();
				} else if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.dep)) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.PERSON) ||
						 depentent.getPos().equals(POS.NNP))) {
					
					// Save Y index
					nY = depentent.getTokenId();
				}
			}
			
			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundKillNoun && !strX.equals("") && !strY.equals("")) {
				KillXKillYSet.add(new Relation(Relation.RelationName.Kill, strY, strX, strSentenceID));
			}
			
			return (KillXKillYSet);
		}
		
	}
	
	/**
	 * Here we taking cases of "X slaying Y" with the dependency nn.
	 * @author hazoomm 201337904
	 *
	 */
	public static class KillXSlayingYNn implements Rule<SentenceWithDependenciesAndNER> {

		// Kill nouns
		private ArrayList<String> lstKillNouns = new ArrayList<String>();
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			lstKillNouns.add("killing");
			lstKillNouns.add("murdering");
			lstKillNouns.add("shooting");
			lstKillNouns.add("assassinating");
			lstKillNouns.add("slaying");
			
			LinkedList<Relation> KillXKillYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (KillXKillYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundKillNoun = false;
			
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				
				if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nsubj) ||
					sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nsubjpass)) &&
					(depentent.getNamedEntityType().equals(NamedEntityType.PERSON) ||
					depentent.getPos().equals(POS.NNP))) {
					
					strSentenceID = governon.getSentenceId();
					bFoundKillNoun = true;
					
					// Save Y index
					nY = depentent.getTokenId();
				} else if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nn)) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.PERSON) ||
						 depentent.getPos().equals(POS.NNP)) &&
						 lstKillNouns.contains(governon.getLemma())) {
					
					// Save X index
					nX = depentent.getTokenId();
				}
			}
			
			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundKillNoun && !strX.equals("") && !strY.equals("")) {
				KillXKillYSet.add(new Relation(Relation.RelationName.Kill, strY, strX, strSentenceID));
			}
			
			return (KillXKillYSet);
		}
		
	}

	
	/*
	 * --------------------------------------------------------------------------------
	 * 
	 *                                    Work_For part
	 * 
	 * --------------------------------------------------------------------------------
	 */
	
	/**
	 * Here we taking cases of "X for Y" with the dependency prep_for.
	 * @author hazoomm 201337904
	 *
	 */
	public static class WorkForXForYPrep_for implements Rule<SentenceWithDependenciesAndNER> {
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			LinkedList<Relation> WorkXWorkYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (WorkXWorkYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundWorkNoun = false;
			
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				
				if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nsubj) ||
					sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nsubjpass)) &&
					(depentent.getNamedEntityType().equals(NamedEntityType.PERSON))) {
					
					// Save X index
					nX = depentent.getTokenId();
				} else if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.prep_for)) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.ORGANIZATION))) {
					
					strSentenceID = depentent.getSentenceId();
					bFoundWorkNoun = true;
					
					// Save Y index
					nY = depentent.getTokenId();
				}
			}
			
			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.ORGANIZATION)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundWorkNoun && !strX.equals("") && !strY.equals("")) {
				WorkXWorkYSet.add(new Relation(Relation.RelationName.Work_For, strX, strY, strSentenceID));
			}
			
			return (WorkXWorkYSet);
		}
		
	}
	
	/**
	 * Here we taking cases of "X of Y" with the dependency prep_of.
	 * @author hazoomm 201337904
	 *
	 */
	public static class WorkForXOfYPrep_of implements Rule<SentenceWithDependenciesAndNER> {
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			LinkedList<Relation> WorkXWorkYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (WorkXWorkYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundWorkNoun = false;
			
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
				
				if (sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.prep_of) &&
						depentent.getNamedEntityType().equals(NamedEntityType.ORGANIZATION) &&
						governon.getNamedEntityType().equals(NamedEntityType.PERSON)) {
					
					strSentenceID = depentent.getSentenceId();
					bFoundWorkNoun = true;
					
					// Save X index
					nX = governon.getTokenId();
					
					// Save Y index
					nY = depentent.getTokenId();
				}
			}
			
			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.ORGANIZATION)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundWorkNoun && !strX.equals("") && !strY.equals("")) {
				WorkXWorkYSet.add(new Relation(Relation.RelationName.Work_For, strX, strY, strSentenceID));
			}
			
			return (WorkXWorkYSet);
		}
		
	}
	
	/**
	 * Here we taking cases of "X Y professor" with the dependency appos.
	 * @author hazoomm 201337904
	 *
	 */
	public static class WorkForXYProfessorAppos implements Rule<SentenceWithDependenciesAndNER> {
		
		private ArrayList<String> lstWorkForNouns = new ArrayList<String>();
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			lstWorkForNouns.add("professor");
			
			LinkedList<Relation> WorkXWorkYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (WorkXWorkYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundWorkNoun = false;
			
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
				
				if (sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.appos) &&
						lstWorkForNouns.contains(depentent.getLemma()) &&
						governon.getNamedEntityType().equals(NamedEntityType.PERSON)) {
					
					strSentenceID = depentent.getSentenceId();
					bFoundWorkNoun = true;
					
					// Save X index
					nX = governon.getTokenId();
				} else if (sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.dep) &&
						lstWorkForNouns.contains(governon.getLemma()) &&
						depentent.getNamedEntityType().equals(NamedEntityType.ORGANIZATION)) {
					
					// Save Y index
					nY = depentent.getTokenId();
				}
			}
			
			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.ORGANIZATION)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundWorkNoun && !strX.equals("") && !strY.equals("")) {
				WorkXWorkYSet.add(new Relation(Relation.RelationName.Work_For, strX, strY, strSentenceID));
			}
			
			return (WorkXWorkYSet);
		}
		
	}
	
	/**
	 * Here we taking cases of "X official Y" with the dependency appos.
	 * @author hazoomm 201337904
	 *
	 */
	public static class WorkForXOfficialYAppos implements Rule<SentenceWithDependenciesAndNER> {
		
		private ArrayList<String> lstWorkForNouns = new ArrayList<String>();
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			lstWorkForNouns.add("spokesman");
			lstWorkForNouns.add("president");
			lstWorkForNouns.add("official");
			lstWorkForNouns.add("chairman");
			lstWorkForNouns.add("republican");
			lstWorkForNouns.add("publisher");
			lstWorkForNouns.add("member");
			lstWorkForNouns.add("administrator");
			lstWorkForNouns.add("Secretary-General");
			lstWorkForNouns.add("manager");
			
			LinkedList<Relation> WorkXWorkYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (WorkXWorkYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundWorkNoun = false;
			
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
				
				if (sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.appos) &&
						lstWorkForNouns.contains(depentent.getLemma()) &&
						governon.getNamedEntityType().equals(NamedEntityType.PERSON)) {
					
					// Save X index
					nX = governon.getTokenId();
				} else if (sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.prep_of) &&
						lstWorkForNouns.contains(governon.getLemma()) &&
						depentent.getNamedEntityType().equals(NamedEntityType.ORGANIZATION)) {
					
					strSentenceID = depentent.getSentenceId();
					bFoundWorkNoun = true;
					
					// Save Y index
					nY = depentent.getTokenId();
				}
			}
			
			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.ORGANIZATION) ||
						sentence.getTokens().get(i).getPos().equals(POS.NN)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundWorkNoun && !strX.equals("") && !strY.equals("")) {
				WorkXWorkYSet.add(new Relation(Relation.RelationName.Work_For, strX, strY, strSentenceID));
			}
			
			return (WorkXWorkYSet);
		}
		
	}
	
	/**
	 * Here we taking cases of "X was founded by Y" with the dependency agent.
	 * @author hazoomm 201337904
	 *
	 */
	public static class WorkForXFoundedByYAgent implements Rule<SentenceWithDependenciesAndNER> {
		
		private ArrayList<String> lstWorkForNouns = new ArrayList<String>();
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			lstWorkForNouns.add("found");
			
			LinkedList<Relation> WorkXWorkYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (WorkXWorkYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundWorkNoun = false;
			
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
				
				if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nsubjpass) ||
						sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nsubj)) &&
						lstWorkForNouns.contains(governon.getLemma()) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.ORGANIZATION) ||
								depentent.getNamedEntityType().equals(NamedEntityType.LOCATION) ||
								depentent.getPos().equals(POS.NNP))) {

					// Save Y index
					nY = depentent.getTokenId();
				} else if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.agent)) &&
						lstWorkForNouns.contains(governon.getLemma()) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.PERSON) ||
								depentent.getPos().equals(POS.NNP))) {
					
					strSentenceID = depentent.getSentenceId();
					bFoundWorkNoun = true;
					
					// Save X index
					nX = depentent.getTokenId();
				}
			}
			
			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.ORGANIZATION) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP) ||
						sentence.getTokens().get(i).getWord().equals("The")) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundWorkNoun && !strX.equals("") && !strY.equals("")) {
				WorkXWorkYSet.add(new Relation(Relation.RelationName.Work_For, strX, strY, strSentenceID));
			}
			
			return (WorkXWorkYSet);
		}
		
	}
	
	/**
	 * Here we taking cases of "X's Y" with the dependency poss.
	 * @author hazoomm 201337904
	 *
	 */
	public static class WorkForXSYPoss implements Rule<SentenceWithDependenciesAndNER> {
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			LinkedList<Relation> WorkXWorkYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (WorkXWorkYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundWorkNoun = false;
			
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
				
				if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.poss) ||
						sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nn)) &&
						(governon.getNamedEntityType().equals(NamedEntityType.PERSON)) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.ORGANIZATION))) {
					
					strSentenceID = depentent.getSentenceId();
					bFoundWorkNoun = true;
					
					// Save X index
					nX = governon.getTokenId();
					
					// Save Y index
					nY = depentent.getTokenId();
				}
			}
			
			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.ORGANIZATION)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundWorkNoun && !strX.equals("") && !strY.equals("")) {
				WorkXWorkYSet.add(new Relation(Relation.RelationName.Work_For, strX, strY, strSentenceID));
			}
			
			return (WorkXWorkYSet);
		}
		
	}
	
	/**
	 * Here we taking cases of "X spokesman Y" with the dependency nn.
	 * @author hazoomm 201337904
	 *
	 */
	public static class WorkForXSpokesmanYNn implements Rule<SentenceWithDependenciesAndNER> {
		
		private ArrayList<String> lstWorkForNouns = new ArrayList<String>();
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			lstWorkForNouns.add("spokesman");
			lstWorkForNouns.add("president");
			lstWorkForNouns.add("chairman");
			lstWorkForNouns.add("publisher");
			lstWorkForNouns.add("manager");
			
			LinkedList<Relation> WorkXWorkYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (WorkXWorkYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundWorkNoun = false;
			
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
				
				if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nn)) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.ORGANIZATION)) &&
						lstWorkForNouns.contains(governon.getLemma())) {
					
					strSentenceID = depentent.getSentenceId();
					bFoundWorkNoun = true;
					
					// Save Y index
					nY = depentent.getTokenId();
				} else if (sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nsubj) &&
						depentent.getNamedEntityType().equals(NamedEntityType.PERSON)) {
					
					// Save X index
					nX = depentent.getTokenId();
				}
			}
			
			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.ORGANIZATION) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP) ||
						sentence.getTokens().get(i).getWord().equals("The")) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundWorkNoun && !strX.equals("") && !strY.equals("")) {
				WorkXWorkYSet.add(new Relation(Relation.RelationName.Work_For, strX, strY, strSentenceID));
			}
			
			return (WorkXWorkYSet);
		}
		
	}
	
	/**
	 * Here we taking cases of "X official Y" with the dependency dep.
	 * @author hazoomm 201337904
	 *
	 */
	public static class WorkForXOfficialYDep implements Rule<SentenceWithDependenciesAndNER> {
		
		private ArrayList<String> lstWorkForNouns = new ArrayList<String>();
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			lstWorkForNouns.add("spokesman");
			lstWorkForNouns.add("president");
			lstWorkForNouns.add("chairman");
			lstWorkForNouns.add("official");
			lstWorkForNouns.add("publisher");
			lstWorkForNouns.add("manager");
			lstWorkForNouns.add("head");
			
			LinkedList<Relation> WorkXWorkYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (WorkXWorkYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundWorkNoun = false;
			
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
				
				if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nn)) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.ORGANIZATION)) &&
						lstWorkForNouns.contains(governon.getLemma())) {
					
					strSentenceID = depentent.getSentenceId();
					bFoundWorkNoun = true;
					
					// Save Y index
					nY = depentent.getTokenId();
				} else if (sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.dep) &&
						depentent.getNamedEntityType().equals(NamedEntityType.PERSON) &&
						lstWorkForNouns.contains(governon.getLemma())) {
					
					// Save X index
					nX = depentent.getTokenId();
				}
			}
			
			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.ORGANIZATION) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP) ||
						sentence.getTokens().get(i).getWord().equals("The")) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundWorkNoun && !strX.equals("") && !strY.equals("")) {
				WorkXWorkYSet.add(new Relation(Relation.RelationName.Work_For, strX, strY, strSentenceID));
			}
			
			return (WorkXWorkYSet);
		}
		
	}
	
	/**
	 * Here we taking cases of "X served in Y" with the dependency prep_in.
	 * @author hazoomm 201337904
	 *
	 */
	public static class WorkForXServedInYPrep_In implements Rule<SentenceWithDependenciesAndNER> {
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			LinkedList<Relation> WorkXWorkYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (WorkXWorkYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundWorkNoun = false;
			
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
				
				if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.prep_in)) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.ORGANIZATION))) {
					
					strSentenceID = depentent.getSentenceId();
					bFoundWorkNoun = true;
					
					// Save Y index
					nY = depentent.getTokenId();
				} else if (sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.rcmod) &&
						governon.getNamedEntityType().equals(NamedEntityType.PERSON)) {
					
					// Save X index
					nX = governon.getTokenId();
				}
			}
			
			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.ORGANIZATION) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP) ||
						sentence.getTokens().get(i).getWord().equals("The")) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundWorkNoun && !strX.equals("") && !strY.equals("")) {
				WorkXWorkYSet.add(new Relation(Relation.RelationName.Work_For, strX, strY, strSentenceID));
			}
			
			return (WorkXWorkYSet);
		}
	}
	
	/**
	 * Here we taking cases of "X republican on Y".
	 * @author hazoomm 201337904
	 *
	 */
	public static class WorkForXRepublicanOfYNn implements Rule<SentenceWithDependenciesAndNER> {
		
		private ArrayList<String> lstWorkForNouns = new ArrayList<String>();
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			lstWorkForNouns.add("spokesman");
			lstWorkForNouns.add("president");
			lstWorkForNouns.add("chairman");
			lstWorkForNouns.add("official");
			lstWorkForNouns.add("manager");
			lstWorkForNouns.add("publisher");
			lstWorkForNouns.add("republican");
			lstWorkForNouns.add("member");
			lstWorkForNouns.add("head");
			
			LinkedList<Relation> WorkXWorkYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (WorkXWorkYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundWorkNoun = false;
			
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
				
				if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nn)) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.ORGANIZATION))) {
					
					strSentenceID = depentent.getSentenceId();
					bFoundWorkNoun = true;
					
					// Save Y index
					nY = depentent.getTokenId();
				} else if (sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nsubj) &&
						depentent.getNamedEntityType().equals(NamedEntityType.PERSON) &&
						lstWorkForNouns.contains(governon.getLemma().toLowerCase())) {
					
					// Save X index
					nX = depentent.getTokenId();
				}
			}
			
			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.ORGANIZATION) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP) ||
						sentence.getTokens().get(i).getPos().equals(POS.NN) ||
						sentence.getTokens().get(i).getWord().equals("The")) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundWorkNoun && !strX.equals("") && !strY.equals("")) {
				WorkXWorkYSet.add(new Relation(Relation.RelationName.Work_For, strX, strY, strSentenceID));
			}
			
			return (WorkXWorkYSet);
		}
	}
	
	/**
	 * Here we taking cases of "X director of Y".
	 * @author hazoomm 201337904
	 *
	 */
	public static class WorkForXDirectorOfY implements Rule<SentenceWithDependenciesAndNER> {
		
		private ArrayList<String> lstWorkForNouns = new ArrayList<String>();
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			lstWorkForNouns.add("spokesman");
			lstWorkForNouns.add("president");
			lstWorkForNouns.add("chairman");
			lstWorkForNouns.add("manager");
			lstWorkForNouns.add("official");
			lstWorkForNouns.add("republican");
			lstWorkForNouns.add("executive");
			lstWorkForNouns.add("member");
			lstWorkForNouns.add("director");
			lstWorkForNouns.add("publisher");
			lstWorkForNouns.add("head");
			
			LinkedList<Relation> WorkXWorkYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (WorkXWorkYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			boolean bFoundY = false; // Indication we found Y
			boolean bFoundX = false; // Indication we found X
			boolean bFoundWorkNoun = false;
			String strSentenceID = new String();
			
			int i = 0;
			int nSize = sentence.getTokens().size();
			
			// Find x
			while (i < nSize && !bFoundX) {
				if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON)) {
					bFoundX = true;
					nX = sentence.getTokens().get(i).getTokenId();
				}
				i++;
			}
			
			// Find work noun
			while (i < nSize && !bFoundWorkNoun) {
				if (lstWorkForNouns.contains(sentence.getTokens().get(i).getLemma())) {
					bFoundWorkNoun = true;
					strSentenceID = sentence.getTokens().get(i).getSentenceId();
				}
				i++;
			}
			
			// Find Y
			while (i < nSize && !bFoundY) {
				if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.ORGANIZATION)) {
					bFoundY = true;
					nY = sentence.getTokens().get(i).getTokenId();
				}
				i++;
			}

			// Take X as more than 1 word
			if (bFoundX) {	
				i = 0;
				while (i < nSize) {
					boolean bInX = false;
					
					String tmpX = "";
					while (i < nSize &&
							((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON)) {
						if (sentence.getTokens().get(i).getTokenId().equals(nX))
							bInX = true;
						tmpX += sentence.getTokens().get(i).getWord() + " ";
						i++;
					}
					
					// Removing the last space
					if (tmpX.length() > 1)
						tmpX = tmpX.substring(0, tmpX.length() - 1);
					
					i++;
					if (bInX) {
						strX = tmpX;
						i = nSize; // Stop the loop
					}
				}
			}
			
			// Take Y as more than 1 word
			if (bFoundY) {
				i = 0;
				while (i < nSize) {
					boolean bInY = false;
					
					String tmpY = "";
					while (i < nSize &&
							((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.ORGANIZATION)) {
						if (sentence.getTokens().get(i).getTokenId().equals(nY))
							bInY = true;
						tmpY += sentence.getTokens().get(i).getWord() + " ";
						i++;
					}
					
					// Removing the last space
					if (tmpY.length() > 1)
						tmpY = tmpY.substring(0, tmpY.length() - 1);
					
					i++;
					if (bInY) {
						strY = tmpY;
						i = nSize; // Stop the loop
					}
				}
			}
			
			// Add the relation to the list
			if (bFoundWorkNoun && !strX.equals("") && !strY.equals("")) {
				WorkXWorkYSet.add(new Relation(Relation.RelationName.Work_For, strX, strY, strSentenceID));
			}
			
			return (WorkXWorkYSet);
		}
	}
	
	/**
	 * Here we taking cases of "X directs Y".
	 * @author hazoomm 201337904
	 *
	 */
	public static class WorkForXDirectsY implements Rule<SentenceWithDependenciesAndNER> {
		
		private ArrayList<String> lstWorkForNouns = new ArrayList<String>();
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			lstWorkForNouns.add("head");
			lstWorkForNouns.add("direct");
			lstWorkForNouns.add("manage");
			
			LinkedList<Relation> WorkXWorkYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (WorkXWorkYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundWorkNoun = false;
			
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
				
				if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nn)) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.ORGANIZATION))) {
					
					strSentenceID = depentent.getSentenceId();
					bFoundWorkNoun = true;
					
					// Save Y index
					nY = depentent.getTokenId();
				} else if (sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nsubj) &&
						depentent.getNamedEntityType().equals(NamedEntityType.PERSON) &&
						lstWorkForNouns.contains(governon.getLemma().toLowerCase())) {
					
					// Save X index
					nX = depentent.getTokenId();
				} else if (sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.rcmod) &&
						governon.getNamedEntityType().equals(NamedEntityType.PERSON) &&
						lstWorkForNouns.contains(depentent.getLemma().toLowerCase())) {
					
					// Save X index
					nX = governon.getTokenId();
				}
			}
			
			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.ORGANIZATION) ||
						sentence.getTokens().get(i).getPos().equals(POS.NNP) ||
						sentence.getTokens().get(i).getPos().equals(POS.NN) ||
						sentence.getTokens().get(i).getWord().equals("The")) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundWorkNoun && !strX.equals("") && !strY.equals("")) {
				WorkXWorkYSet.add(new Relation(Relation.RelationName.Work_For, strX, strY, strSentenceID));
			}
			
			return (WorkXWorkYSet);
		}
	}
	
	/**
	 * Here we taking cases of "a X professor Y".
	 * @author hazoomm 201337904
	 *
	 */
	public static class WorkForAXProfessorYDep implements Rule<SentenceWithDependenciesAndNER> {
		
		private ArrayList<String> lstWorkForNouns = new ArrayList<String>();
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			lstWorkForNouns.add("professor");
			lstWorkForNouns.add("player");
			lstWorkForNouns.add("actor");
			
			LinkedList<Relation> WorkXWorkYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (WorkXWorkYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundWorkNoun = false;
			
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
				
				if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.dep)) &&
						(depentent.getNamedEntityType().equals(NamedEntityType.ORGANIZATION)) &&
						lstWorkForNouns.contains(governon.getLemma().toLowerCase())) {
					
					strSentenceID = depentent.getSentenceId();
					bFoundWorkNoun = true;
					
					// Save Y index
					nY = depentent.getTokenId();
				} else if (sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nsubj) &&
						depentent.getNamedEntityType().equals(NamedEntityType.PERSON)) {
					
					// Save X index
					nX = depentent.getTokenId();
				}
			}

			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.ORGANIZATION)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundWorkNoun && !strX.equals("") && !strY.equals("")) {
				WorkXWorkYSet.add(new Relation(Relation.RelationName.Work_For, strX, strY, strSentenceID));
			}
			
			return (WorkXWorkYSet);
		}
	}
	
	/**
	 * Here we taking cases of "X of the Y" by the dependency prep_of".
	 * @author hazoomm 201337904
	 *
	 */
	public static class WorkForXOfTheYPrep_of implements Rule<SentenceWithDependenciesAndNER> {
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			LinkedList<Relation> WorkXWorkYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (WorkXWorkYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundWorkNoun = false;
			
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
				
				if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.prep_of)) &&
						depentent.getNamedEntityType().equals(NamedEntityType.ORGANIZATION) &&
						governon.getNamedEntityType().equals(NamedEntityType.PERSON)) {
					
					strSentenceID = depentent.getSentenceId();
					bFoundWorkNoun = true;
					
					// Save X index
					nX = governon.getTokenId();
					
					// Save Y index
					nY = depentent.getTokenId();
				}
			}

			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.ORGANIZATION)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundWorkNoun && !strX.equals("") && !strY.equals("")) {
				WorkXWorkYSet.add(new Relation(Relation.RelationName.Work_For, strX, strY, strSentenceID));
			}
			
			return (WorkXWorkYSet);
		}
	}
	
	// Wraps an EX2 rule for EX3
	public static class WorkForXFirstYFirstEmployee implements Rule<SentenceWithDependenciesAndNER>{
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			Rule<Sentence> oldRule = new Ex2Configuration.WorkForXFirstYFirstEmployee();
			return oldRule.applyRule(sentence);
		}
	}
	
	/*
	 * --------------------------------------------------------------------------------
	 * 
	 *                                    OrgBased_In part
	 * 
	 * --------------------------------------------------------------------------------
	 */

	/**
	 * Here we taking cases of "X in Y".
	 * @author hazoomm 201337904
	 *
	 */
	public static class OrgBasedInXInY implements Rule<SentenceWithDependenciesAndNER> {
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			LinkedList<Relation> OrgXYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (OrgXYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			boolean bFoundY = false; // Indication we found Y
			boolean bFoundX = false; // Indication we found X
			boolean bFoundIn = false;
			String strSentenceID = new String();
			
			int i = 0;
			int nSize = sentence.getTokens().size();
			
			// Find x
			while (i < nSize && !bFoundX) {
				if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.ORGANIZATION)) {
					bFoundX = true;
					nX = sentence.getTokens().get(i).getTokenId();
				}
				i++;
			}
			
			// Find "in"
			while (i < nSize && !bFoundIn) {
				if (sentence.getTokens().get(i).getLemma().equals("in")) {
					bFoundIn = true;
					strSentenceID = sentence.getTokens().get(i).getSentenceId();
				}
				i++;
			}
			
			// Find Y
			while (i < nSize && !bFoundY) {
				if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
					bFoundY = true;
					nY = sentence.getTokens().get(i).getTokenId();
				}
				i++;
			}

			// Take X as more than 1 word
			if (bFoundX) {	
				i = 0;
				while (i < nSize) {
					boolean bInX = false;
					
					String tmpX = "";
					while (i < nSize &&
							((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.ORGANIZATION)) {
						if (sentence.getTokens().get(i).getTokenId().equals(nX))
							bInX = true;
						tmpX += sentence.getTokens().get(i).getWord() + " ";
						i++;
					}
					
					// Removing the last space
					if (tmpX.length() > 1)
						tmpX = tmpX.substring(0, tmpX.length() - 1);
					
					i++;
					if (bInX) {
						strX = tmpX;
						i = nSize; // Stop the loop
					}
				}
			}
			
			// Take Y as more than 1 word
			if (bFoundY) {
				i = 0;
				while (i < nSize) {
					boolean bInY = false;
					
					String tmpY = "";
					while (i < nSize &&
							((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
						if (sentence.getTokens().get(i).getTokenId().equals(nY))
							bInY = true;
						tmpY += sentence.getTokens().get(i).getWord() + " ";
						i++;
					}
					
					// Removing the last space
					if (tmpY.length() > 1)
						tmpY = tmpY.substring(0, tmpY.length() - 1);
					
					i++;
					if (bInY) {
						strY = tmpY;
						i = nSize; // Stop the loop
					}
				}
			}
			
			// Add the relation to the list
			if (bFoundIn && !strX.equals("") && !strY.equals("")) {
				OrgXYSet.add(new Relation(Relation.RelationName.OrgBased_In, strX, strY, strSentenceID));
			}
			
			return (OrgXYSet);
		}
	}
	
	/**
	 * Here we taking cases of "X in Y" by the dependency prep_in".
	 * @author hazoomm 201337904
	 *
	 */
	public static class OrgBasedInXInYPrep_in implements Rule<SentenceWithDependenciesAndNER> {
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			LinkedList<Relation> OrgXYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (OrgXYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			String strSentenceID = new String();
			boolean bFoundWorkNoun = false;
			
			// Run on the dependencies
			int nDependenciesSize = sentence.getDependencies().size();
			for (int i = 0; i < nDependenciesSize; i++) {
				TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
				TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
				
				if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.prep_in)) &&
						depentent.getNamedEntityType().equals(NamedEntityType.LOCATION) &&
						governon.getNamedEntityType().equals(NamedEntityType.ORGANIZATION)) {
					
					strSentenceID = depentent.getSentenceId();
					bFoundWorkNoun = true;
					
					// Save X index
					nX = governon.getTokenId();
					
					// Save Y index
					nY = depentent.getTokenId();
				}
			}

			// Take X as more than 1 word
			int nTokensSize = sentence.getTokens().size();
			int i = 0;
			while (i < nTokensSize) {
				boolean bInX = false;
				
				String tmpX = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.ORGANIZATION)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nX))
						bInX = true;
					tmpX += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpX.length() > 1)
					tmpX = tmpX.substring(0, tmpX.length() - 1);
				
				i++;
				if (bInX) {
					strX = tmpX;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Take Y as more than 1 word
			i = 0;
			while (i < nTokensSize) {
				boolean bInY = false;
				
				String tmpY = "";
				while (i < nTokensSize &&
						((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
					if (sentence.getTokens().get(i).getTokenId().equals(nY))
						bInY = true;
					tmpY += sentence.getTokens().get(i).getWord() + " ";
					i++;
				}
				
				// Removing the last space
				if (tmpY.length() > 1)
					tmpY = tmpY.substring(0, tmpY.length() - 1);
				
				i++;
				if (bInY) {
					strY = tmpY;
					i = nTokensSize; // Stop the loop
				}
			}
			
			// Add the relation to the list
			if (bFoundWorkNoun && !strX.equals("") && !strY.equals("")) {
				OrgXYSet.add(new Relation(Relation.RelationName.OrgBased_In, strX, strY, strSentenceID));
			}
			
			return (OrgXYSet);
		}
	}
	
	/**
	 * Here we take cases of "Y, AP".
	 * We take the last Noun(Y) before "AP".
	 * @author Moshe Hazoom
	 *
	 */
	public static class OrgBasedYLastAP implements Rule<SentenceWithDependenciesAndNER> {
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
					
			LinkedList<Relation> OrgBasedSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (OrgBasedSet);
			
			int nSize = sentence.getTokens().size();
			boolean bFoundY = false; // Indication we found Y
			boolean bFoundX = false; // Indication we found X
			String strX = new String("");
			String strY = new String("");
			String strSentenceID = new String();
			int i = 0;
			
			while (i < nSize) {
				
				// Run until we found AP
				while (i < nSize && !(bFoundX && bFoundY)) {
					if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.ORGANIZATION) &&
							sentence.getTokens().get(i).getWord().equals("AP")) { // X
						bFoundX = true;
						strSentenceID = sentence.getTokens().get(i).getSentenceId();
						strX = "AP";
						i++;
					} else if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION) &&
							!sentence.getTokens().get(i).getWord().equals("AP")) { // Y
						bFoundY = true;
						strY = "";
						
						while ((i < nSize) && ((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
							strY += (sentence.getTokens().get(i).getWord() + " ");
							i++;
						}
						
						// For removing the last space
						strY = strY.substring(0, strY.length() - 1);
					} else {
						i++;
					}
				}
				
				if (bFoundX && bFoundY) {
						
					// Add the relation to the list
					OrgBasedSet.add(new Relation(Relation.RelationName.OrgBased_In, strX, strY, strSentenceID));
				}
				
				// Initialize the flags, because maybe there will be another OrgBased_In in the sentence
				bFoundX = false;
				bFoundY = false;
				strX = "";
				strY = "";
				strSentenceID = "";
			}
			
			return (OrgBasedSet);
		}
	}
	
	/**
	 * Here we take cases of "Y, AP".
	 * We take the first Noun(Y) before "AP".
	 * @author Moshe Hazoom
	 *
	 */
	public static class OrgBasedYFirstAP implements Rule<SentenceWithDependenciesAndNER> {
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
					
			LinkedList<Relation> OrgBasedSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (OrgBasedSet);
			
			int nSize = sentence.getTokens().size();
			boolean bFoundY = false; // Indication we found Y
			boolean bFoundX = false; // Indication we found X
			String strX = new String("");
			String strY = new String("");
			String strSentenceID = new String();
			int i = 0;
			
			while (i < nSize) {
				
				// Run until we found Y
				while (i < nSize && !bFoundY) {
					if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
						bFoundY = true;
						strSentenceID = sentence.getTokens().get(i).getSentenceId();
						strY = "";
						
						while ((i < nSize) && ((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
							strY += (sentence.getTokens().get(i).getWord() + " ");
							i++;
						}
						
						// For removing the last space
						strY = strY.substring(0, strY.length() - 1);
					} else {
						i++;
					}
				}
				
				// Run until we found x or the end of the sentence
				if (i < nSize && bFoundY) {
					while (i < nSize && !bFoundX) {
						if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.ORGANIZATION)) {
							bFoundX = true;
							strX = "";
							
							while ((i < nSize) && ((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.ORGANIZATION)) {
								strX += (sentence.getTokens().get(i).getWord() + " ");
								i++;
							}
							
							// For removing the last space
							strX = strX.substring(0, strX.length() - 1);
							
							if (!strX.equals("AP")) {
								bFoundX = false;	
							}
						} else {
							i++;
						}
					}
					if (bFoundX && bFoundY) {
						
						// Add the relation to the list
						OrgBasedSet.add(new Relation(Relation.RelationName.OrgBased_In, strX, strY, strSentenceID));
					}
				}
				
				// Initialize the flags, because maybe there will be another OrgBased_In in the sentence
				bFoundX = false;
				bFoundY = false;
				strX = "";
				strY = "";
				strSentenceID = "";
			}
			
			return (OrgBasedSet);
		}
	}
	
	/*
	 * --------------------------------------------------------------------------------
	 * 
	 *                                    Live_In part
	 * 
	 * --------------------------------------------------------------------------------
	 */
	
	/**
	 * Here we take name of person and name of location after it.
	 * @author hazoomm 201337904
	 *
	 */
	public static class LiveInNamePlace implements Rule<SentenceWithDependenciesAndNER> {
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			LinkedList<Relation> LiveXYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (LiveXYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			boolean bFoundY = false; // Indication we found Y
			boolean bFoundX = false; // Indication we found X
			String strSentenceID = new String();
			
			int i = 0;
			int nSize = sentence.getTokens().size();
			
			// Find x
			while (i < nSize && !bFoundX) {
				if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON)) {
					bFoundX = true;
					nX = sentence.getTokens().get(i).getTokenId();
				}
				i++;
			}
			
			// Find Y
			while (i < nSize && !bFoundY) {
				if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION) &&
						sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
					bFoundY = true;
					nY = sentence.getTokens().get(i).getTokenId();
					strSentenceID = sentence.getTokens().get(i).getSentenceId();
				}
				i++;
			}

			// Take X as more than 1 word
			if (bFoundX) {	
				i = 0;
				while (i < nSize) {
					boolean bInX = false;
					
					String tmpX = "";
					while (i < nSize &&
							((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON)) {
						if (sentence.getTokens().get(i).getTokenId().equals(nX))
							bInX = true;
						tmpX += sentence.getTokens().get(i).getWord() + " ";
						i++;
					}
					
					// Removing the last space
					if (tmpX.length() > 1)
						tmpX = tmpX.substring(0, tmpX.length() - 1);
					
					i++;
					if (bInX) {
						strX = tmpX;
						i = nSize; // Stop the loop
					}
				}
			}
			
			// Take Y as more than 1 word
			if (bFoundY) {
				i = 0;
				while (i < nSize) {
					boolean bInY = false;
					
					String tmpY = "";
					while (i < nSize &&
							((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
						if (sentence.getTokens().get(i).getTokenId().equals(nY))
							bInY = true;
						tmpY += sentence.getTokens().get(i).getWord() + " ";
						i++;
					}
					
					// Removing the last space
					if (tmpY.length() > 1)
						tmpY = tmpY.substring(0, tmpY.length() - 1);
					
					i++;
					if (bInY) {
						strY = tmpY;
						i = nSize; // Stop the loop
					}
				}
			}
			
			// Add the relation to the list
			if (!strX.equals("") && !strY.equals("")) {
				LiveXYSet.add(new Relation(Relation.RelationName.Live_In, strX, strY, strSentenceID));
			}
			
			return (LiveXYSet);
		}
	}
	
	/**
	 * Here we take name of place and name of person after it.
	 * @author hazoomm 201337904
	 *
	 */
	public static class LiveInPlaceName implements Rule<SentenceWithDependenciesAndNER> {
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
			
			LinkedList<Relation> LiveXYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (LiveXYSet);
			
			String strX = new String("");
			String strY = new String("");
			Integer nX = new Integer(0);
			Integer nY = new Integer(0);
			boolean bFoundY = false; // Indication we found Y
			boolean bFoundX = false; // Indication we found X
			String strSentenceID = new String();
			
			int i = 0;
			int nSize = sentence.getTokens().size();
			
			// Find Y
			while (i < nSize && !bFoundY) {
				if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
					bFoundY = true;
					nY = sentence.getTokens().get(i).getTokenId();
				}
				i++;
			}
			
			// Find X
			while (i < nSize && !bFoundX) {
				if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON)) {
					bFoundX = true;
					nX = sentence.getTokens().get(i).getTokenId();
					strSentenceID = sentence.getTokens().get(i).getSentenceId();
				}
				i++;
			}

			// Take X as more than 1 word
			if (bFoundX) {	
				i = 0;
				while (i < nSize) {
					boolean bInX = false;
					
					String tmpX = "";
					while (i < nSize &&
							((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON)) {
						if (sentence.getTokens().get(i).getTokenId().equals(nX))
							bInX = true;
						tmpX += sentence.getTokens().get(i).getWord() + " ";
						i++;
					}
					
					// Removing the last space
					if (tmpX.length() > 1)
						tmpX = tmpX.substring(0, tmpX.length() - 1);
					
					i++;
					if (bInX) {
						strX = tmpX;
						i = nSize; // Stop the loop
					}
				}
			}
			
			// Take Y as more than 1 word
			if (bFoundY) {
				i = 0;
				while (i < nSize) {
					boolean bInY = false;
					
					String tmpY = "";
					while (i < nSize &&
							((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
						if (sentence.getTokens().get(i).getTokenId().equals(nY))
							bInY = true;
						tmpY += sentence.getTokens().get(i).getWord() + " ";
						i++;
					}
					
					// Removing the last space
					if (tmpY.length() > 1)
						tmpY = tmpY.substring(0, tmpY.length() - 1);
					
					i++;
					if (bInY) {
						strY = tmpY;
						i = nSize; // Stop the loop
					}
				}
			}
			
			// Add the relation to the list
			if (!strX.equals("") && !strY.equals("")) {
				LiveXYSet.add(new Relation(Relation.RelationName.Live_In, strX, strY, strSentenceID));
			}
			
			return (LiveXYSet);
		}
	}
	
	/*
	 * --------------------------------------------------------------------------------
	 * 
	 *                                    Located_In part
	 * 
	 * --------------------------------------------------------------------------------
	 */
	
	/**
	 * Here we take cases of "X , Y".
	 * We take the first place(X) before the "," and the first place(Y) after it.
	 * @author Moshe Hazoom
	 *
	 */
	public static class LocatedInXPlaceCommaYPlace implements Rule<SentenceWithDependenciesAndNER> {
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
					
			LinkedList<Relation> LocatedInSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (LocatedInSet);
			
			int nSize = sentence.getTokens().size();
			boolean bFoundComma = false; // Indication we found ","
			boolean bFoundY = false; // Indication we found Y
			boolean bFoundX = false; // Indication we found X
			String strX = new String("");
			String strY = new String("");
			String strSentenceID = new String();
			int i = 0;
			
			while (i < nSize) {
				
				// Run until we found X
				while (i < nSize && !bFoundX) {
					if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
						bFoundX = true;
						strSentenceID = sentence.getTokens().get(i).getSentenceId();
						strX = "";
						
						while ((i < nSize) && ((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
							strX += (sentence.getTokens().get(i).getWord() + " ");
							i++;
						}
						
						// For removing the last space
						strX = strX.substring(0, strX.length() - 1);	
					} else {
						i++;
					}
				}
				
				// Run until we found ","
				if (i < nSize && bFoundX) {
					while (i < nSize && !bFoundComma) {
						if (sentence.getTokens().get(i).getWord().equals(",")) {
							bFoundComma = true;
							i++;
						} else {
							i++;
						}
					}
				}
				
				// Run until we found y or the end of the sentence
				if (i < nSize && bFoundComma && bFoundX) {
					while (i < nSize && !bFoundY) {
						if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) { // It's a noun
							bFoundY = true;
							strY = "";
							
							while ((i < nSize) && ((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
								strY += (sentence.getTokens().get(i).getWord() + " ");
								i++;
							}
							
							// For removing the last space
							strY = strY.substring(0, strY.length() - 1);
						} else {
							i++;
						}
					}
					if (bFoundX && bFoundY && bFoundComma) {
						
						// Add the relation to the list
						LocatedInSet.add(new Relation(Relation.RelationName.Located_In, strX, strY, strSentenceID));
						
						// Catch another comma
						bFoundComma = false;
						bFoundY = false;	
						String strLastX = strX;
						strX = strY;
						strY = "";
						
						// Run until we found ","
						while (i < nSize && !bFoundComma) {
							if (sentence.getTokens().get(i).getWord().equals(",")) {
								bFoundComma = true;
								i++;
							} else {
								i++;
							}
						}
						
						// Run until we found y or the end of the sentence
						if (i < nSize && bFoundComma && bFoundX) {
							while (i < nSize && !bFoundY) {
								if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
									bFoundY = true;
									strY = "";
									
									while ((i < nSize) && ((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
										strY += (sentence.getTokens().get(i).getWord() + " ");
										i++;
									}
									
									// For removing the last space
									strY = strY.substring(0, strY.length() - 1);
								} else {
									i++;
								}
							}
							if (bFoundX && bFoundY && bFoundComma) {
								
								// Add the relation to the list
								LocatedInSet.add(new Relation(Relation.RelationName.Located_In, strX, strY, strSentenceID));
								LocatedInSet.add(new Relation(Relation.RelationName.Located_In, strLastX, strY, strSentenceID));
								
							}
						}
					}
				}
				
				// Initialize the flags, because maybe there will be another Located_In in the sentence
				bFoundComma = false;
				bFoundX = false;
				bFoundY = false;
				strX = "";
				strY = "";
				strSentenceID = "";
			}
			
			return (LocatedInSet);
		}
	}
	
	/**
	 * Here we take cases of "X in Y".
	 * We take the first place(X) before the "in" and the first place(Y) after it.
	 * @author Moshe Hazoom
	 *
	 */
	public static class LocatedInXPlaceInYPlace implements Rule<SentenceWithDependenciesAndNER> {
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
					
			LinkedList<Relation> LocatedInSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (LocatedInSet);
			
			int nSize = sentence.getTokens().size();
			boolean bFoundIn = false; // Indication we found "in"
			boolean bFoundY = false; // Indication we found Y
			boolean bFoundX = false; // Indication we found X
			String strX = new String("");
			String strY = new String("");
			String strSentenceID = new String();
			int i = 0;
			
			while (i < nSize) {
				
				// Run until we found X
				while (i < nSize && !bFoundX) {
					if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
						bFoundX = true;
						strSentenceID = sentence.getTokens().get(i).getSentenceId();
						strX = "";
						
						while ((i < nSize) && ((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
							strX += (sentence.getTokens().get(i).getWord() + " ");
							i++;
						}
						
						// For removing the last space
						strX = strX.substring(0, strX.length() - 1);	
					} else {
						i++;
					}
				}
				
				// Run until we found "in"
				if (i < nSize && bFoundX) {
					while (i < nSize && !bFoundIn) {
						if (sentence.getTokens().get(i).getWord().equals("in")) {
							bFoundIn = true;
							i++;
						} else {
							i++;
						}
					}
				}
				
				// Run until we found y or the end of the sentence
				if (i < nSize && bFoundIn && bFoundX) {
					while (i < nSize && !bFoundY) {
						if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) { // It's a noun
							bFoundY = true;
							strY = "";
							
							while ((i < nSize) && ((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
								strY += (sentence.getTokens().get(i).getWord() + " ");
								i++;
							}
							
							// For removing the last space
							strY = strY.substring(0, strY.length() - 1);
						} else {
							i++;
						}
					}
					if (bFoundX && bFoundY && bFoundIn) {
						
						// Add the relation to the list
						LocatedInSet.add(new Relation(Relation.RelationName.Located_In, strX, strY, strSentenceID));
						
						// Catch another "in"
						bFoundIn = false;
						bFoundY = false;	
						String strLastX = strX;
						strX = strY;
						strY = "";
						
						// Run until we found "in"
						while (i < nSize && !bFoundIn) {
							if (sentence.getTokens().get(i).getWord().equals("in")) {
								bFoundIn = true;
								i++;
							} else {
								i++;
							}
						}
						
						// Run until we found y or the end of the sentence
						if (i < nSize && bFoundIn && bFoundX) {
							while (i < nSize && !bFoundY) {
								if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
									bFoundY = true;
									strY = "";
									
									while ((i < nSize) && ((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
										strY += (sentence.getTokens().get(i).getWord() + " ");
										i++;
									}
									
									// For removing the last space
									strY = strY.substring(0, strY.length() - 1);
								} else {
									i++;
								}
							}
							if (bFoundX && bFoundY && bFoundIn) {
								
								// Add the relation to the list
								LocatedInSet.add(new Relation(Relation.RelationName.Located_In, strX, strY, strSentenceID));
								LocatedInSet.add(new Relation(Relation.RelationName.Located_In, strLastX, strY, strSentenceID));
								
							}
						}
					}
				}
				
				// Initialize the flags, because maybe there will be another Located_In in the sentence
				bFoundIn = false;
				bFoundX = false;
				bFoundY = false;
				strX = "";
				strY = "";
				strSentenceID = "";
			}
			
			return (LocatedInSet);
		}
	}
	
	/**
	 * Here we take cases of "X in Y".
	 * We take the last place(X) before the "in" and the first place(Y) after it.
	 * @author Moshe Hazoom
	 *
	 */
	public static class LocatedInXPlaceLastInYPlace implements Rule<SentenceWithDependenciesAndNER> {
		
		public List<Relation> applyRule(SentenceWithDependenciesAndNER sentence) {
					
			LinkedList<Relation> LocatedInSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (LocatedInSet);
			
			int nSize = sentence.getTokens().size();
			boolean bFoundIn = false; // Indication we found "in"
			boolean bFoundY = false; // Indication we found Y
			boolean bFoundX = false; // Indication we found X
			String strX = new String("");
			String strY = new String("");
			String strSentenceID = new String();
			int i = 0;
			
			while (i < nSize) {
				
				// Run until we found X
				while (i < nSize && !(bFoundX && bFoundIn)) {
					if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
						bFoundX = true;
						strSentenceID = sentence.getTokens().get(i).getSentenceId();
						strX = "";
						
						while ((i < nSize) && ((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
							strX += (sentence.getTokens().get(i).getWord() + " ");
							i++;
						}
						
						// For removing the last space
						strX = strX.substring(0, strX.length() - 1);	
					} else if (sentence.getTokens().get(i).getWord().equals("in")) {
						bFoundIn = true;
						i++;
					} else {
						i++;
					}
				}
				
				// Run until we found y or the end of the sentence
				if (i < nSize && bFoundIn && bFoundX) {
					while (i < nSize && !bFoundY) {
						if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
							bFoundY = true;
							strY = "";
							
							while ((i < nSize) && ((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
								strY += (sentence.getTokens().get(i).getWord() + " ");
								i++;
							}
							
							// For removing the last space
							strY = strY.substring(0, strY.length() - 1);
						} else {
							i++;
						}
					}
					if (bFoundX && bFoundY && bFoundIn) {
						
						// Add the relation to the list
						LocatedInSet.add(new Relation(Relation.RelationName.Located_In, strX, strY, strSentenceID));
					}
				}
				
				// Initialize the flags, because maybe there will be another Located_In in the sentence
				bFoundIn = false;
				bFoundX = false;
				bFoundY = false;
				strX = "";
				strY = "";
				strSentenceID = "";
			}
			
			return (LocatedInSet);
		}
	}
}