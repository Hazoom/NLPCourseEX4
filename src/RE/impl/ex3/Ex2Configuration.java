package RE.impl.ex3;

import java.util.List;
import java.util.LinkedList;

import RE.api.Configuration;
import RE.api.Rule;
import RE.rep.POS;
import RE.rep.Relation;
import RE.rep.Sentence;

/**
 * Ex2 reused rules.
 * @author hazoomm 201337904
 */
public class Ex2Configuration implements Configuration<Sentence>{
	private LinkedList<Rule<Sentence>> configuration;
	
	public List<Rule<Sentence>> getConfiguration() {
		return configuration;
	}
	
	public void generateConfiguration() {
		configuration = new LinkedList<Rule<Sentence>>();
		configuration.add(new WorkForXFirstYFirstEmployee());
	}


	/**
	 * Here we take cases of "X Y employee".
	 * @author hazoomm 201337904
	 *
	 */
	public static class WorkForXFirstYFirstEmployee implements Rule<Sentence> {

		public List<Relation> applyRule(Sentence sentence) {
			
			LinkedList<Relation> WorkForXKillYSet = new LinkedList<Relation>();
			
			// If sentence is empty - return an empty list
			if (sentence.getTokens() == null)
				return (WorkForXKillYSet);
			
			int nSize = sentence.getTokens().size();
			boolean bFoundY = false; // Indication we found Y
			boolean bFoundX = false; // Indication we found X
			boolean bFoundEmp = false;
			String strX = new String("");
			String strY = new String("");
			String strSentenceID = new String();
			int i = 0;
			
			while (i < nSize) {
				
				// Run until we found X or the end of the sentence
				while (i < nSize && !bFoundX) {
					if (sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
						bFoundX = true;
						strX = "";
						while ((i < nSize) && sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
							strX += (sentence.getTokens().get(i).getWord() + " ");
							i++;
						}
						
						// For removing the last space
						strX = strX.substring(0, strX.length() - 1);
					} else {
						i++;
					}
				}
				
				// Run until we found y or the end of the sentence
				if (i < nSize && bFoundX) {
					while (i < nSize && !bFoundY) {
						if (sentence.getTokens().get(i).getPos().equals(POS.NNP)) { // It's a noun
							bFoundY = true;
							strY = "";
							while ((i < nSize) && (sentence.getTokens().get(i).getPos().equals(POS.NNP))) {
								strY += (sentence.getTokens().get(i).getWord() + " ");
								i++;
							}
							// For removing the last space
							strY = strY.substring(0, strY.length() - 1);
						} else {
							i++;
						}
					}
					
					// If we found X and Y
					if (bFoundX && bFoundY) {
						
						// Run until we found "employee"
						while (i < nSize && !bFoundEmp) {
							if (sentence.getTokens().get(i).getWord().equals("employee")) { // It's a noun
								bFoundEmp = true;
								strSentenceID = sentence.getTokens().get(i).getSentenceId();
							} else {
								i++;
							}
						}
						
						if (bFoundEmp)
							WorkForXKillYSet.add(new Relation(Relation.RelationName.Work_For, strX, strY, strSentenceID));
					}
				}
				
				// Initialize the flags, because maybe there will be another work_for in the sentence
				bFoundX = false;
				bFoundY = false;
				bFoundEmp = false;
				strX = "";
				strY = "";
				strSentenceID = "";
			}
			
			return (WorkForXKillYSet);
		}
	}
}