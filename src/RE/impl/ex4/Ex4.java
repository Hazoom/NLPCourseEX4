package RE.impl.ex4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import RE.api.Configuration;
import RE.api.RelationExtractor;
import RE.api.Rule;
import RE.impl.ex3.DependencyName;
import RE.rep.POS;
import RE.rep.Relation;
import RE.rep.SentenceWithDependenciesAndNER;
import RE.rep.SyntacticDependency;
import RE.rep.TokenWithNER;
import RE.rep.TokenWithNER.NamedEntityType;

/**
 * Ex4's main class.
 * @author hazoomm 201337904
 */
public class Ex4 implements RelationExtractor<SentenceWithDependenciesAndNER>{

	private double dNo_RelationFactor = 0.3;
	
	/**
	 * @param filename
	 * @return List of gold-standard relations loaded from the given file
	 */
	public List<Relation> loadAnnotation(String filename) {
		
		String strLine;
		BufferedReader brReader = null;
		String[] words;
		List<Relation> lstRelations = new LinkedList<Relation>();
		
		try {
			// Read the annotation file and process the rows
			brReader = new BufferedReader(new FileReader(filename));
			while ((strLine = brReader.readLine()) != null) {
				words = strLine.split("\t");
				lstRelations.add(new Relation(Relation.RelationName.valueOf(words[2]),
						words[1], words[3], words[0]));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			// Close file
			if (brReader != null) {
				try {
					brReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return (lstRelations);
	}
	
	/** Function uses Java regular expressions to extract all the sentences from the given XML file.
	 * @param String text
	 * @return List of tokens.
	*/
	private List<SentenceWithDependenciesAndNER> extractSentences(String text) {
		
		List<SentenceWithDependenciesAndNER> lstSentences = new LinkedList<SentenceWithDependenciesAndNER>();
		
		Pattern pattern = Pattern.compile("<sentence\\s{1}id=\\\"(\\d+)\\\">(.+?)</sentence>");
		Matcher tokenMatcherSentence = pattern.matcher(text);
		
		// Run on all the sentences
		while (tokenMatcherSentence.find()) {
			
			// The sentence as in the XML file
			String strXMLSentence = tokenMatcherSentence.group(2);
			
			pattern = Pattern.compile("<token\\s{1}id=\\\"(\\d+)\\\">\\s+<word>(.+?)</word>\\s+<lemma>(.+?)</lemma>\\s+.+?<POS>(.+?)</POS>\\s+<NER>(.+?)</NER>.+?</token>");
			Matcher tokenMatcherTokens = pattern.matcher(strXMLSentence);
			List<TokenWithNER> lstTokens = new LinkedList<TokenWithNER>();
			
			Integer nTokenID = new Integer(0);
			String strWord = null;
			String strLemma = null;
			POS pos = null;
			NamedEntityType net = null;
			String strSentenceID = null;
			boolean bContinue = true;
			HashMap<Integer, TokenWithNER> hmTokens = new HashMap<Integer, TokenWithNER>();
			
			// Run on all the tokens
			while (tokenMatcherTokens.find() && bContinue) {
			
				// Extract the token ID
				nTokenID = Integer.parseInt(tokenMatcherTokens.group(1));
				
				// Extract the word
				strWord = tokenMatcherTokens.group(2);
				
				// Extract the lemma
				strLemma = tokenMatcherTokens.group(3);
				
				// Extract the POS
				try {
					pos = POS.valueOf(tokenMatcherTokens.group(4));
				}
				catch (IllegalArgumentException ex) {
					pos = POS.OTHER;
				}
				
				// Extract the ner
				try {
					net = NamedEntityType.valueOf(tokenMatcherTokens.group(5));
				}
				catch (IllegalArgumentException ex) {
					net = NamedEntityType.NOT_DEFINED;
				}
				
				// Check if it's the first token of a sentence
				if (strWord.equals(strLemma) && strWord.startsWith("sent") && strWord.length() > 4) {
					String strTemp = strWord.substring(4, strWord.length());
					try {
						Integer.valueOf(strTemp);
						
						// Set the sentence ID
						strSentenceID = strWord;

						TokenWithNER twn = new TokenWithNER(nTokenID, strSentenceID, strWord, strLemma, pos, net);
						lstTokens.add(twn);
						hmTokens.put(nTokenID, twn);
					// Just a regular word that begins with 'sent'
					} catch (NumberFormatException e) {
						TokenWithNER twn = new TokenWithNER(nTokenID, strSentenceID, strWord, strLemma, pos, net);
						lstTokens.add(twn);
						hmTokens.put(nTokenID, twn);
					}
				// Just a regular word
				} else {
					TokenWithNER twn = new TokenWithNER(nTokenID, strSentenceID, strWord, strLemma, pos, net);
					lstTokens.add(twn);
					hmTokens.put(nTokenID, twn);
				}
			}
			
			// Extract the dependencies
			if (bContinue) {
				pattern = Pattern.compile("<collapsed-dependencies>(.+?)</collapsed-dependencies>");
				Matcher tokenMatcherDependendent = pattern.matcher(strXMLSentence);
				List<SyntacticDependency> lstDependencies = new ArrayList<SyntacticDependency>();
				
				// Extract the dependencies section
				while (tokenMatcherDependendent.find()) {
					
					String strDepentdencies = tokenMatcherDependendent.group(1);
					
					pattern = Pattern.compile("<dep\\s{1}type=\\\"(.+?)\\\">\\s+<governor\\s{1}idx=\\\"(\\d+)\\\">(.+?)</governor>\\s+<dependent\\s{1}idx=\\\"(\\d+)\\\">(.+?)</dependent>\\s+</dep>");
					Matcher tokenMatcherDependencies = pattern.matcher(strDepentdencies);
					DependencyName dependencyName = null;
					
					// Run on all the dependencies
					while (tokenMatcherDependencies.find()) {
						
						// Extract the dependency type
						try {
							dependencyName = DependencyName.valueOf(tokenMatcherDependencies.group(1));
						} catch (IllegalArgumentException ex) {
							dependencyName = null;
						}
						
						if (dependencyName != null &&
								!tokenMatcherDependencies.group(3).equals(strSentenceID) &&
								!tokenMatcherDependencies.group(5).equals(strSentenceID)) {
							
							// Add the dependency to the list
							lstDependencies.add(new SyntacticDependency(dependencyName, 
									hmTokens.get(Integer.valueOf(tokenMatcherDependencies.group(2))), 
									hmTokens.get(Integer.valueOf(tokenMatcherDependencies.group(4)))));
						}	
					}
				}
				
				// Add the sentence to the list of sentences
				if (bContinue)
					lstSentences.add(new SentenceWithDependenciesAndNER(strXMLSentence, lstTokens, lstDependencies));
			}
		}
		
		return (lstSentences);
	}
	
	/**
	 * @param sentence
	 * @return list of relations extracted from the sentence using the given set of rules in the given Configuration.
	 * If no relations were extracted, return an empty list.
	 */
	public List<Relation> extractRelations(SentenceWithDependenciesAndNER sentence, Configuration<SentenceWithDependenciesAndNER> conf) {
		LinkedList<Relation> lstRelations = new LinkedList<Relation>();
		
		for (Rule<SentenceWithDependenciesAndNER> rule : conf.getConfiguration()) {
			lstRelations.addAll((LinkedList<Relation>) rule.applyRule(sentence));
		}
		
		return (lstRelations);
	}
	
	/**
	 * Method creates the header of the initial ARFF file
	 * @param bwWriter
	 * @throws IOException
	 */
	public void createInitialHeaderOfARFF(BufferedWriter bwWriter) throws IOException {
		bwWriter.append("@RELATION ex4");
		bwWriter.newLine();
		bwWriter.newLine();
		bwWriter.append("@ATTRIBUTE killVerb NUMERIC");
		bwWriter.newLine();
		bwWriter.append("@ATTRIBUTE killNoun NUMERIC");
		bwWriter.newLine();
		bwWriter.append("@ATTRIBUTE OrganizationInLocation NUMERIC");
		bwWriter.newLine();
		bwWriter.append("@ATTRIBUTE YCommaAP NUMERIC");
		bwWriter.newLine();
		bwWriter.append("@ATTRIBUTE NamePlace NUMERIC");
		bwWriter.newLine();
		bwWriter.append("@ATTRIBUTE PlaceCommaPlace NUMERIC");
		bwWriter.newLine();
		bwWriter.append("@ATTRIBUTE LocationInLocation NUMERIC");
		bwWriter.newLine();
		bwWriter.append("@ATTRIBUTE PersonForOrganization NUMERIC");
		bwWriter.newLine();
		bwWriter.append("@ATTRIBUTE class {OrgBased_In,Located_In,Work_For,Live_In,Kill,No_Relation}");
		bwWriter.newLine();
		bwWriter.newLine();
		bwWriter.append("@DATA");
		bwWriter.newLine();
	}
	
	/**
	 * Method creates the header of the ARFF file
	 * @param bwWriter
	 * @throws IOException
	 */
	public void createHeaderOfARFF(BufferedWriter bwWriter) throws IOException {
		bwWriter.append("@RELATION ex4");
		bwWriter.newLine();
		bwWriter.newLine();
		bwWriter.append("@ATTRIBUTE killVerb NUMERIC");
		bwWriter.newLine();
		bwWriter.append("@ATTRIBUTE killNoun NUMERIC");
		bwWriter.newLine();
		bwWriter.append("@ATTRIBUTE OrganizationInLocation NUMERIC");
		bwWriter.newLine();
		bwWriter.append("@ATTRIBUTE YCommaAP NUMERIC");
		bwWriter.newLine();
		bwWriter.append("@ATTRIBUTE NamePlace NUMERIC");
		bwWriter.newLine();
		bwWriter.append("@ATTRIBUTE PlaceCommaPlace NUMERIC");
		bwWriter.newLine();
		bwWriter.append("@ATTRIBUTE LocationInLocation NUMERIC");
		bwWriter.newLine();
		bwWriter.append("@ATTRIBUTE PersonForOrganization NUMERIC");
		bwWriter.newLine();
		bwWriter.append("@ATTRIBUTE PersonOfOrganization NUMERIC");
		bwWriter.newLine();
		bwWriter.append("@ATTRIBUTE XDirectorOfY NUMERIC");
		bwWriter.newLine();
		bwWriter.append("@ATTRIBUTE XYEmployee NUMERIC");
		bwWriter.newLine();
		bwWriter.append("@ATTRIBUTE NNPsDistance NUMERIC");
		bwWriter.newLine();
		bwWriter.append("@ATTRIBUTE LocationLocationDistance NUMERIC");
		bwWriter.newLine();
		bwWriter.append("@ATTRIBUTE APOccurence NUMERIC");
		bwWriter.newLine();
		bwWriter.append("@ATTRIBUTE class {OrgBased_In,Located_In,Work_For,Live_In,Kill,No_Relation}");
		bwWriter.newLine();
		bwWriter.newLine();
		bwWriter.append("@DATA");
		bwWriter.newLine();
	}
	
	/**
	 * Method create the initialConfiguration ARFF file.
	 * @param bwWriter
	 * @param lstSentences
	 * @param lstGoldStandartRelations
	 * @throws IOException
	 */
	public void createInitialConfigurationARFF(BufferedWriter bwWriter, List<SentenceWithDependenciesAndNER> lstSentences, List<Relation> lstGoldStandartRelations) throws IOException {
		
		// Create HashMap containing the relations
		HashMap<String, ArrayList<Relation>> hmRelations = new HashMap<String, ArrayList<Relation>>();
		
		// Number of gold standard relations
		int numberOfGoldStandardRelations = lstGoldStandartRelations.size();
		
		for (int i = 0; i < numberOfGoldStandardRelations; i++) {
			
			// Not contains
			if (!hmRelations.containsKey(lstGoldStandartRelations.get(i).getSentenceId())) {
				
				// Create a new array for the relation
				ArrayList<Relation> arr = new ArrayList<Relation>();
				arr.add(lstGoldStandartRelations.get(i));
				hmRelations.put(lstGoldStandartRelations.get(i).getSentenceId(),arr);
			// Contains
			} else {
				
				// Add the relation to the list of the relations of that sentence ID
				ArrayList<Relation> arr = hmRelations.get(lstGoldStandartRelations.get(i).getSentenceId());
				arr.add(lstGoldStandartRelations.get(i));
				
				// Remove it and insert it again
				hmRelations.remove(lstGoldStandartRelations.get(i).getSentenceId());
				hmRelations.put(lstGoldStandartRelations.get(i).getSentenceId(),arr);
			}
		}
		
		// Run over all the sentences
		for (SentenceWithDependenciesAndNER sentence : lstSentences) {
			String sentenceId = sentence.getTokens().get(0).getSentenceId();

			StringBuilder sb = new StringBuilder();
			int index = 0;
			sb.append("{");
			
			// KillVerb feature
			int killVerbValue = this.calculateKillVerb(sentence);
			if (killVerbValue > 0) {
				sb.append(String.valueOf(index));
				sb.append(" ");
				sb.append(String.valueOf(killVerbValue));
				sb.append(",");
			}

			index++;
			
			// KillNoun feature
			int killNounValue = this.calculateKillNoun(sentence);
			if (killNounValue > 0) {
				sb.append(String.valueOf(index));
				sb.append(" ");
				sb.append(String.valueOf(killNounValue));
				sb.append(",");
			}
			
			index++;
			
			// OrganizationInLocation feature
			int OrganizationInLocation = this.calculateOrganizationInLocation(sentence);
			if (OrganizationInLocation > 0) {
				sb.append(String.valueOf(index));
				sb.append(" ");
				sb.append(String.valueOf(OrganizationInLocation));
				sb.append(",");
			}
			
			index++;
			
			// YCommaAP feature
			int yCommaAP = this.calculateYCommaAP(sentence);
			if (yCommaAP > 0) {
				sb.append(String.valueOf(index));
				sb.append(" ");
				sb.append(String.valueOf(yCommaAP));
				sb.append(",");
			}
			
			index++;
			
			// NamePlace feature
			int namePlace = this.calculateNamePlace(sentence);
			if (namePlace > 0) {
				sb.append(String.valueOf(index));
				sb.append(" ");
				sb.append(String.valueOf(namePlace));
				sb.append(",");
			}
			
			index++;
			
			
			// PlaceCommaPlace feature
			int placeCommaPlace = this.calculatePlaceCommaPlace(sentence);
			if (placeCommaPlace > 0) {
				sb.append(String.valueOf(index));
				sb.append(" ");
				sb.append(String.valueOf(placeCommaPlace));
				sb.append(",");
			}
			
			index++;
			
			// LocationInLocation feature
			int locationInLocation = this.calculateLocationInLocation(sentence);
			if (locationInLocation > 0) {
				sb.append(String.valueOf(index));
				sb.append(" ");
				sb.append(String.valueOf(locationInLocation));
				sb.append(",");
			}
			
			index++;
			
			// PersonForOrganization feature
			int personForOrganization = this.calculatePersonForOrganization(sentence);
			if (personForOrganization > 0) {
				sb.append(String.valueOf(index));
				sb.append(" ");
				sb.append(String.valueOf(personForOrganization));
				sb.append(",");
			}
			
			index++;
			
			sb.append(String.valueOf(index));
			sb.append(" ");
			
			// Add the class
			ArrayList<Relation> arr = hmRelations.get(sentenceId);
			
			// If it's a positive example
			if (arr != null) {
				for (Relation rel : arr) {
					bwWriter.append(sb.toString().concat(rel.getRelName().toString()).concat("}"));
					
					// New line
					bwWriter.newLine();
				}
			// A negative example
			} else {
				bwWriter.append(sb.toString().concat("No_Relation}"));
				
				// New line
				bwWriter.newLine();
			}
		}
	}
	
	/**
	 * Method create the finalConfiguration ARFF file.
	 * @param bwWriter
	 * @param lstSentences
	 * @param lstGoldStandartRelations
	 * @throws IOException
	 */
	public void createFinalConfigurationARFF(BufferedWriter bwWriter, List<SentenceWithDependenciesAndNER> lstSentences, List<Relation> lstGoldStandartRelations) throws IOException {
		
		// Create HashMap containing the relations
		HashMap<String, ArrayList<Relation>> hmRelations = new HashMap<String, ArrayList<Relation>>();
		
		// Number of gold standard relations
		int numberOfGoldStandardRelations = lstGoldStandartRelations.size();
		
		int maxNumberOfNoRelationExamples = (int) (this.dNo_RelationFactor * numberOfGoldStandardRelations);
		int numberOfNoRelationExamples = 0;
		
		for (int i = 0; i < numberOfGoldStandardRelations; i++) {
			
			// Not contains
			if (!hmRelations.containsKey(lstGoldStandartRelations.get(i).getSentenceId())) {
				
				// Create a new array for the relation
				ArrayList<Relation> arr = new ArrayList<Relation>();
				arr.add(lstGoldStandartRelations.get(i));
				hmRelations.put(lstGoldStandartRelations.get(i).getSentenceId(),arr);
			// Contains
			} else {
				
				// Add the relation to the list of the relations of that sentence ID
				ArrayList<Relation> arr = hmRelations.get(lstGoldStandartRelations.get(i).getSentenceId());
				arr.add(lstGoldStandartRelations.get(i));
				
				// Remove it and insert it again
				hmRelations.remove(lstGoldStandartRelations.get(i).getSentenceId());
				hmRelations.put(lstGoldStandartRelations.get(i).getSentenceId(),arr);
			}
		}
		
		// Run over all the sentences
		for (SentenceWithDependenciesAndNER sentence : lstSentences) {
			String sentenceId = sentence.getTokens().get(0).getSentenceId();

			StringBuilder sb = new StringBuilder();
			int index = 0;
			sb.append("{");
			
			// KillVerb feature
			int killVerbValue = this.calculateKillVerb(sentence);
			if (killVerbValue > 0) {
				sb.append(String.valueOf(index));
				sb.append(" ");
				sb.append(String.valueOf(killVerbValue));
				sb.append(",");
			}

			index++;
			
			// KillNoun feature
			int killNounValue = this.calculateKillNoun(sentence);
			if (killNounValue > 0) {
				sb.append(String.valueOf(index));
				sb.append(" ");
				sb.append(String.valueOf(killNounValue));
				sb.append(",");
			}
			
			index++;
			
			// OrganizationInLocation feature
			int OrganizationInLocation = this.calculateOrganizationInLocation(sentence);
			if (OrganizationInLocation > 0) {
				sb.append(String.valueOf(index));
				sb.append(" ");
				sb.append(String.valueOf(OrganizationInLocation));
				sb.append(",");
			}
			
			index++;
			
			// YCommaAP feature
			int yCommaAP = this.calculateYCommaAP(sentence);
			if (yCommaAP > 0) {
				sb.append(String.valueOf(index));
				sb.append(" ");
				sb.append(String.valueOf(yCommaAP));
				sb.append(",");
			}
			
			index++;
			
			// NamePlace feature
			int namePlace = this.calculateNamePlace(sentence);
			if (namePlace > 0) {
				sb.append(String.valueOf(index));
				sb.append(" ");
				sb.append(String.valueOf(namePlace));
				sb.append(",");
			}
			
			index++;
			
			
			// PlaceCommaPlace feature
			int placeCommaPlace = this.calculatePlaceCommaPlace(sentence);
			if (placeCommaPlace > 0) {
				sb.append(String.valueOf(index));
				sb.append(" ");
				sb.append(String.valueOf(placeCommaPlace));
				sb.append(",");
			}
			
			index++;
			
			// LocationInLocation feature
			int locationInLocation = this.calculateLocationInLocation(sentence);
			if (locationInLocation > 0) {
				sb.append(String.valueOf(index));
				sb.append(" ");
				sb.append(String.valueOf(locationInLocation));
				sb.append(",");
			}
			
			index++;
			
			// PersonForOrganization feature
			int personForOrganization = this.calculatePersonForOrganization(sentence);
			if (personForOrganization > 0) {
				sb.append(String.valueOf(index));
				sb.append(" ");
				sb.append(String.valueOf(personForOrganization));
				sb.append(",");
			}
			
			index++;
			
			// PersonOfOrganization feature
			int personofOrganization = this.calculatePersonOfOrganization(sentence);
			if (personofOrganization > 0) {
				sb.append(String.valueOf(index));
				sb.append(" ");
				sb.append(String.valueOf(personofOrganization));
				sb.append(",");
			}
			
			index++;
				
			// XDirectorOfY feature
			int XDirectorOfY = this.calculateXDirectorOfY(sentence);
			if (XDirectorOfY > 0) {
				sb.append(String.valueOf(index));
				sb.append(" ");
				sb.append(String.valueOf(XDirectorOfY));
				sb.append(",");
			}
			
			index++;
			
			// XYEmployee feature
			int XYEmployee = this.calculateXYEmployee(sentence);
			if (XYEmployee > 0) {
				sb.append(String.valueOf(index));
				sb.append(" ");
				sb.append(String.valueOf(XYEmployee));
				sb.append(",");
			}
			
			index++;
			
			// NNPsDistance feature
			int NNPsDistance = this.calculateNNPsDistance(sentence);
			if (NNPsDistance > 0) {
				sb.append(String.valueOf(index));
				sb.append(" ");
				sb.append(String.valueOf(NNPsDistance));
				sb.append(",");
			}
			
			index++;
			
			// LocationLocationDistance feature
			int LocationLocationDistance = this.calculateLocationLocationDistance(sentence);
			if (LocationLocationDistance > 0) {
				sb.append(String.valueOf(index));
				sb.append(" ");
				sb.append(String.valueOf(LocationLocationDistance));
				sb.append(",");
			}
			
			index++;
			
			// APOccurence feature
			int APOccurence = this.calculateAPOccurence(sentence);
			if (APOccurence > 0) {
				sb.append(String.valueOf(index));
				sb.append(" ");
				sb.append(String.valueOf(APOccurence));
				sb.append(",");
			}
			
			index++;
			
			sb.append(String.valueOf(index));
			sb.append(" ");
			
			// Add the class
			ArrayList<Relation> arr = hmRelations.get(sentenceId);
			
			// A positive example
			if (arr != null) {
				for (Relation rel : arr) {
					bwWriter.append(sb.toString().concat(rel.getRelName().toString()).concat("}"));
					
					// New line
					bwWriter.newLine();
				}
			// A negative example
			} else {
				
				if (numberOfNoRelationExamples < maxNumberOfNoRelationExamples) {
					bwWriter.append(sb.toString().concat("No_Relation}"));
					
					// New line
					bwWriter.newLine();
					
					numberOfNoRelationExamples++;
				}
			}
		}
	}
	
	/**
	 * Method returns 1 if the sentence contains one of the kill verbs or 0 otherwise.
	 * @param sentence
	 * @return
	 */
	public int calculateKillVerb(SentenceWithDependenciesAndNER sentence) {
		int result = 0;
		int nTokensSize = sentence.getTokens().size();
		
		ArrayList<String> arrKillVerbs = new ArrayList<String>();
		arrKillVerbs.add("killing");
		arrKillVerbs.add("murdering");
		arrKillVerbs.add("shooting");
		arrKillVerbs.add("assassination");
		arrKillVerbs.add("slaying");
		
		for (int i = 0; i < nTokensSize; i++) {
			if (arrKillVerbs.contains(sentence.getTokens().get(i).getLemma())) {
				return (1);
			}
		}
		
		return (result);
	}
	
	/**
	 * Method returns 1 if the sentence contains one of the kill nouns or 0 otherwise.
	 * @param sentence
	 * @return
	 */
	public int calculateKillNoun(SentenceWithDependenciesAndNER sentence) {
		int result = 0;
		int nTokensSize = sentence.getTokens().size();
		
		ArrayList<String> arrKillNouns = new ArrayList<String>();	
		arrKillNouns.add("kill");
		arrKillNouns.add("murder");
		arrKillNouns.add("shoot");
		arrKillNouns.add("assassinate");
		arrKillNouns.add("slay");
		
		for (int i = 0; i < nTokensSize; i++) {
			if (arrKillNouns.contains(sentence.getTokens().get(i).getLemma())) {
				return (1);
			}
		}
		
		return (result);
	}
	
	/**
	 * Method returns 1 if the sentence contains "X in Y" where X is an organization and
	 * Y is a location and 0 otherwise.
	 * @param sentence
	 * @return
	 */
	public int calculateOrganizationInLocation(SentenceWithDependenciesAndNER sentence) {
		int result = 0;
		boolean bFoundY = false; // Indication we found Y
		boolean bFoundX = false; // Indication we found X
		boolean bFoundIn = false;
		
		int i = 0;
		int nSize = sentence.getTokens().size();
		
		// Find x
		while (i < nSize && !bFoundX) {
			if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.ORGANIZATION)) {
				bFoundX = true;
			}
			i++;
		}
		
		// Find "in"
		while (i < nSize && !bFoundIn) {
			if (sentence.getTokens().get(i).getLemma().equals("in")) {
				bFoundIn = true;
			}
			i++;
		}
		
		// Find Y
		while (i < nSize && !bFoundY) {
			if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
				bFoundY = true;
			}
			i++;
		}
		
		// Found
		if (bFoundIn && bFoundX && bFoundY) {
			return (1);
		}
		
		return (result);
	}
	
	/**
	 * Method returns 1 if the sentence contains "Y, AP" where Y is a location and 0 otherwise.
	 * @param sentence
	 * @return
	 */
	public int calculateYCommaAP(SentenceWithDependenciesAndNER sentence) {
		int result = 0;
		boolean bFoundY = false; // Indication we found Y
		boolean bFoundX = false; // Indication we found X
		
		int i = 0;
		int nSize = sentence.getTokens().size();
		String strY = new String("");
		
		while (i < nSize) {
			
			// Run until we found AP
			while (i < nSize && !(bFoundX && bFoundY)) {
				if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.ORGANIZATION) &&
						sentence.getTokens().get(i).getWord().equals("AP")) { // X
					bFoundX = true;
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
				return (1);
			}
		}
		
		return (result);
	}
	
	/**
	 * Method returns 1 if the sentence contains "X in Y" where X is a location and
	 * Y is a location and 0 otherwise.
	 * @param sentence
	 * @return
	 */
	public int calculateLocationInLocation(SentenceWithDependenciesAndNER sentence) {
		int result = 0;
		boolean bFoundY = false; // Indication we found Y
		boolean bFoundX = false; // Indication we found X
		boolean bFoundIn = false;
		
		int i = 0;
		int nSize = sentence.getTokens().size();
		
		// Find x
		while (i < nSize && !bFoundX) {
			if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
				bFoundX = true;
			}
			i++;
		}
		
		// Find "in"
		while (i < nSize && !bFoundIn) {
			if (sentence.getTokens().get(i).getLemma().equals("in")) {
				bFoundIn = true;
			}
			i++;
		}
		
		// Find Y
		while (i < nSize && !bFoundY) {
			if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
				bFoundY = true;
			}
			i++;
		}
		
		// Found
		if (bFoundIn && bFoundX && bFoundY) {
			return (1);
		}
		
		return (result);
	}
	
	/**
	 * Method returns 1 if the sentence contains "X in Y" where X is a person and
	 * Y is a location and 0 otherwise.
	 * @param sentence
	 * @return
	 */
	public int calculateNamePlace(SentenceWithDependenciesAndNER sentence) {
		int result = 0;
		boolean bFoundY = false; // Indication we found Y
		boolean bFoundX = false; // Indication we found X
		boolean bFoundIn = false;
		
		int i = 0;
		int nSize = sentence.getTokens().size();
		
		// Find x
		while (i < nSize && !bFoundX) {
			if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON)) {
				bFoundX = true;
			}
			i++;
		}
		
		// Find "in" or "of"
		while (i < nSize && !bFoundIn) {
			if (sentence.getTokens().get(i).getLemma().equals("in") ||
				sentence.getTokens().get(i).getLemma().equals("of")) {
				bFoundIn = true;
			}
			i++;
		}
		
		// Find Y
		while (i < nSize && !bFoundY) {
			if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
				bFoundY = true;
			}
			i++;
		}
		
		// Found
		if (bFoundIn && bFoundX && bFoundY) {
			return (1);
		}
		
		return (result);
	}
	
	/**
	 * Method returns 1 if the sentence contains "X , Y" where X is a location and
	 * Y is a location and 0 otherwise.
	 * @param sentence
	 * @return
	 */
	public int calculatePlaceCommaPlace(SentenceWithDependenciesAndNER sentence) {
		int result = 0;
		boolean bFoundY = false; // Indication we found Y
		boolean bFoundX = false; // Indication we found X
		boolean bFoundComma = false;
		
		int i = 0;
		int nSize = sentence.getTokens().size();
		
		// Find x
		while (i < nSize && !bFoundX) {
			if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
				bFoundX = true;
			}
			i++;
		}
		
		// Find ","
		while (i < nSize && !bFoundComma) {
			if (sentence.getTokens().get(i).getWord().equals(",")) {
				bFoundComma = true;
			}
			i++;
		}
		
		// Find Y
		while (i < nSize && !bFoundY) {
			if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
				bFoundY = true;
			}
			i++;
		}
		
		// Found
		if (bFoundComma && bFoundX && bFoundY) {
			return (1);
		}
		
		return (result);
	}
	
	/**
	 * Method returns 1 if the sentence contains "X for Y" where X is a person and
	 * Y is an organization and 0 otherwise.
	 * @param sentence
	 * @return
	 */
	public int calculatePersonForOrganization(SentenceWithDependenciesAndNER sentence) {
		int result = 0;
		boolean bFoundY = false; // Indication we found Y
		boolean bFoundX = false; // Indication we found X
		
		// Run on the dependencies
		int nDependenciesSize = sentence.getDependencies().size();
		for (int i = 0; i < nDependenciesSize; i++) {
			TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
			
			if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nsubj) ||
				sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.nsubjpass)) &&
				(depentent.getNamedEntityType().equals(NamedEntityType.PERSON))) {
				
				bFoundX = true;
			} else if ((sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.prep_for)) &&
					(depentent.getNamedEntityType().equals(NamedEntityType.ORGANIZATION))) {
				bFoundY = true;
			}
		}
		
		// Found
		if (bFoundX && bFoundY) {
			return (1);
		}
		
		return (result);
	}
	
	/**
	 * Method returns 1 if the sentence contains "X of Y" where X is a person and
	 * Y is an organization and 0 otherwise.
	 * @param sentence
	 * @return
	 */
	public int calculatePersonOfOrganization(SentenceWithDependenciesAndNER sentence) {
		int result = 0;
		boolean bFound = false;
		
		// Run on the dependencies
		int nDependenciesSize = sentence.getDependencies().size();
		for (int i = 0; i < nDependenciesSize; i++) {
			TokenWithNER depentent = (TokenWithNER)sentence.getDependencies().get(i).getDependentToken();
			TokenWithNER governon = (TokenWithNER)sentence.getDependencies().get(i).getGovernorToken();
			
			if (sentence.getDependencies().get(i).getDependencyName().equals(DependencyName.prep_of) &&
					depentent.getNamedEntityType().equals(NamedEntityType.ORGANIZATION) &&
					governon.getNamedEntityType().equals(NamedEntityType.PERSON)) {
		
				bFound = true;
			}
		}
		
		// Found
		if (bFound) {
			return (1);
		}
		
		return (result);
	}
	
	/**
	 * Method returns 1 if the sentence contains "X director of Y" where X is a person and
	 * Y is an organization and 0 otherwise.
	 * @param sentence
	 * @return
	 */
	public int calculateXDirectorOfY(SentenceWithDependenciesAndNER sentence) {
		
		int result = 0;
		
		ArrayList<String> lstWorkForNouns = new ArrayList<String>();
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
		
		boolean bFoundY = false; // Indication we found Y
		boolean bFoundX = false; // Indication we found X
		boolean bFoundWorkNoun = false;
		
		int i = 0;
		int nSize = sentence.getTokens().size();
		
		// Find x
		while (i < nSize && !bFoundX) {
			if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.PERSON)) {
				bFoundX = true;
			}
			i++;
		}
		
		// Find work noun
		while (i < nSize && !bFoundWorkNoun) {
			if (lstWorkForNouns.contains(sentence.getTokens().get(i).getLemma())) {
				bFoundWorkNoun = true;
			}
			i++;
		}
		
		// Find Y
		while (i < nSize && !bFoundY) {
			if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.ORGANIZATION)) {
				bFoundY = true;
			}
			i++;
		}
		
		// Found
		if (bFoundWorkNoun && bFoundX && bFoundY) {
			return (1);
		}
		
		return (result);
	}
	
	/**
	 * Method returns 1 if the sentence contains "X Y employee" where X is a NNP POS and
	 * Y is a NNP POS and 0 otherwise.
	 * @param sentence
	 * @return
	 */
	public int calculateXYEmployee(SentenceWithDependenciesAndNER sentence) {
		
		int result = 0;
		
		boolean bFoundY = false; // Indication we found Y
		boolean bFoundX = false; // Indication we found X
		boolean bFoundEmp = false;
		
		int i = 0;
		int nSize = sentence.getTokens().size();
		
		// Run until we found X or the end of the sentence
		while (i < nSize && !bFoundX) {
			if (sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
				bFoundX = true;
			}
			i++;
		}
		
		// Run until we found y or the end of the sentence
		if (i < nSize && bFoundX) {
			while (i < nSize && !bFoundY) {
				if (sentence.getTokens().get(i).getPos().equals(POS.NNP)) { // It's a noun
					bFoundY = true;
				}
				i++;
			}
			
			// If we found X and Y
			if (bFoundX && bFoundY) {
				
				// Run until we found "employee"
				while (i < nSize && !bFoundEmp) {
					if (sentence.getTokens().get(i).getWord().equals("employee"))  { // It's a noun
						bFoundEmp = true;
					}
					i++;
				}
			}
		}
		
		// Found
		if (bFoundEmp && bFoundX && bFoundY) {
			return (1);
		}
		
		return (result);
	}
	
	/**
	 * Method returns the distance between 2 NNPs POS.
	 * @param sentence
	 * @return
	 */
	public int calculateNNPsDistance(SentenceWithDependenciesAndNER sentence) {
		
		int result = 0;
		
		int indexX = -1; 
		int indexY = -1; 
		
		int i = 0;
		int nSize = sentence.getTokens().size();
		
		// Run until we found X or the end of the sentence
		while (i < nSize && indexX == -1) {
			if (sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
				indexX = i;
			}
			i++;
		}
		
		// Run until we found y or the end of the sentence
		if (i < nSize && indexX > -1) {
			while (i < nSize && indexY == -1) {
				if (sentence.getTokens().get(i).getPos().equals(POS.NNP)) {
					indexY = i;
				}
				i++;
			}
			
			// If we found X and Y
			if (indexX > -1 && indexY > -1) {
				return (indexY - indexX);
			}
		}
		
		return (result);
	}
	
	/**
	 * Method returns the distance between a first location and a second location
	 * @param sentence
	 * @return
	 */
	public int calculateLocationLocationDistance(SentenceWithDependenciesAndNER sentence) {
		
		int result = 0;
		
		int indexX = -1; 
		int indexY = -1; 
		
		int i = 0;
		int nSize = sentence.getTokens().size();
		
		// Run until we found X or the end of the sentence
		while (i < nSize && indexX == -1) {
			if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
				indexX = i;
			}
			i++;
		}
		
		// Run until we found y or the end of the sentence
		if (i < nSize && indexX > -1) {
			while (i < nSize && indexY == -1) {
				if (((TokenWithNER)sentence.getTokens().get(i)).getNamedEntityType().equals(NamedEntityType.LOCATION)) {
					indexY = i;
				}
				i++;
			}
			
			// If we found X and Y
			if (indexX > -1 && indexY > -1) {
				return (indexY - indexX);
			}
		}
		
		return (result);
	}

	/**
	 * Method returns 1 if the sentence contains "AP" or 0 otherwise.
	 * @param sentence
	 * @return
	 */
	public int calculateAPOccurence(SentenceWithDependenciesAndNER sentence) {
		int result = 0;
		int nTokensSize = sentence.getTokens().size();
		
		ArrayList<String> arrAP = new ArrayList<String>();
		arrAP.add("AP");
		
		for (int i = 0; i < nTokensSize; i++) {
			if (arrAP.contains(sentence.getTokens().get(i).getLemma())) {
				return (1);
			}
		}
		
		return (result);
	}
	
	/**
	 * @param args (args[0] development-set corpus filename, args[1] development-set annotation filename, args[2] test-set corpus filename, args[3] test-set annotation filename and args[4] output folder name.)
	 */
	public static void main(String[] args) {
		
		Ex4 ex4 = new Ex4();
		StringBuffer sb = new StringBuffer();
		String strDevCorpusFileName = args[0];
		String strTestCorpusFileName = args[2];
		String strDevAnnotationFileName = args[1];
		String strTestAnnotationFileName = args[3];
		String strOutFolderName = args[4];
		String strLine = null;
		String strDevCorpusFile = null; // The development-set corpus file as string
		String strTestCorpusFile = null; // The test-set corpus file as string
		BufferedReader brReader = null;
		BufferedWriter bwDevInitialWriter = null;
		BufferedWriter bwDevFinalWriter = null;
		BufferedWriter bwTestWriter = null;
		
		// Read all the development-set corpus file into StringBuffer using loop
		try {
			brReader = new BufferedReader(new FileReader(strDevCorpusFileName));
			while ((strLine = brReader.readLine()) != null) {
				sb.append(strLine);
			}
			
			// Save the file as String
			strDevCorpusFile = sb.toString();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
			sb = null;
			
			// Close file
			if (brReader != null) {
				try {
					brReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		sb = new StringBuffer();
		
		// Read all the test-set corpus file into StringBuffer using loop
		try {
			brReader = new BufferedReader(new FileReader(strTestCorpusFileName));
			while ((strLine = brReader.readLine()) != null) {
				sb.append(strLine);
			}
			
			// Save the file as String
			strTestCorpusFile = sb.toString();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
			// Close file
			if (brReader != null) {
				try {
					brReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		// Load the annotations from development-set annotations file
		List<Relation> lstDevGoldStandartRelations = ex4.loadAnnotation(strDevAnnotationFileName);
		
		// Extract all the development-set sentences from the XML file
		List<SentenceWithDependenciesAndNER> lstDevSentences = ex4.extractSentences(strDevCorpusFile);
		
		// Load the annotations from test-set annotations file
		List<Relation> lstTestGoldStandartRelations = ex4.loadAnnotation(strTestAnnotationFileName);
		
		// Extract all the test-set sentences from the XML file
		List<SentenceWithDependenciesAndNER> lstTestSentences = ex4.extractSentences(strTestCorpusFile);
		
		// Create ARFF files
		try {
			
			// Open output folder and delete one if exists
			File fOutFolder = new File(strOutFolderName);
			if (fOutFolder.exists()) {
				String[] lstFiles = fOutFolder.list();
				if (lstFiles != null) {
					for (int i = 0; i < lstFiles.length; i++) {
						File entry = new File(fOutFolder, lstFiles[i]);
						entry.delete();
					}
				}
				fOutFolder.delete();
			}
			fOutFolder.mkdir();
			
			// Create the initial configuration of development-set
			bwDevInitialWriter = new BufferedWriter(new FileWriter(fOutFolder.getPath() + File.separatorChar + "initialTraining.arff"));
			
			// Create header of ARFF file
			ex4.createInitialHeaderOfARFF(bwDevInitialWriter);
			
			// Create data of ARFF file
			ex4.createInitialConfigurationARFF(bwDevInitialWriter, lstDevSentences, lstDevGoldStandartRelations);
			
			// Create the final configuration of development-set
			bwDevFinalWriter = new BufferedWriter(new FileWriter(fOutFolder.getPath() + File.separatorChar + "finalTraining.arff"));
			
			// Create header of ARFF file
			ex4.createHeaderOfARFF(bwDevFinalWriter);
			
			// Create data of ARFF file
			ex4.createFinalConfigurationARFF(bwDevFinalWriter, lstDevSentences, lstDevGoldStandartRelations);
			
			// Create the test ARFF
			bwTestWriter = new BufferedWriter(new FileWriter(fOutFolder.getPath() + File.separatorChar + "test.arff"));
			
			// Create header of ARFF file
			ex4.createHeaderOfARFF(bwTestWriter);
			
			// Create data of ARFF file
			ex4.createFinalConfigurationARFF(bwTestWriter, lstTestSentences, lstTestGoldStandartRelations);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
			// Close file
			if (bwDevInitialWriter != null) {
				try {
					bwDevInitialWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			// Close file
			if (bwDevFinalWriter != null) {
				try {
					bwDevFinalWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			// Close file
			if (bwTestWriter != null) {
				try {
					bwTestWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
