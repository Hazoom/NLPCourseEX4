package RE.impl.ex3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import RE.api.Configuration;
import RE.api.RelationEvaluator;
import RE.api.RelationExtractor;
import RE.api.Rule;
import RE.rep.POS;
import RE.rep.Relation;
import RE.rep.SentenceWithDependenciesAndNER;
import RE.rep.SyntacticDependency;
import RE.rep.TokenWithNER;
import RE.rep.TokenWithNER.NamedEntityType;

/**
 * Ex3 is main class.
 * @author hazoomm 201337904
 */
public class Ex3 implements RelationExtractor<SentenceWithDependenciesAndNER>, RelationEvaluator{

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

	/**
	 * @param relA
	 * @param relB
	 * @return true if the two relations match, otherwise - false
	 * 
	 * Two relations match, if they are extracted from the same sentence,
	 * have the same relation name and exactly the same values of arguments
	 */
	public boolean isMatch(Relation relA, Relation relB) {
		
		if ((relA.getRelName().ordinal() == relB.getRelName().ordinal()) &&
			(relA.getSentenceId().equals(relB.getSentenceId())) &&
			(relA.getArg1().equals(relB.getArg1())) &&
			(relA.getArg2().equals(relB.getArg2())))
			return (true);
		
		return (false);
	}


	/**
	 * @param relation
	 * @param annotation - gold-standard list of relations
	 * @return true if the relation is found within the gold-standard annotation.
	 * 
	 * The relation is considered correctly extracted, if exactly the same relation is found in the gold-standard annotation.
	 */
	public boolean isCorrectlyExtractedRelation(Relation relation, List<Relation> annotation) {
		
		// Run over the list and use isMatch method
		for (Relation rel : annotation) {
			if (this.isMatch(rel, relation))
				return (true);
		}
		return false;
	}

	/**
	 * Recall shows how many of the gold-standard relations your system managed to extract correctly.
     * Recall = (number of correctly extracted relations) / (number of relations in the gold-standard annotation).
	 */
	public Double getRecall(Integer numberOfCorrectlyExtractedRelations,
			Integer numberOfGoldStandardRelationsToBeExtracted) {
		if (numberOfGoldStandardRelationsToBeExtracted == 0)
			return (new Double(1.0));
		return (new Double(new Double(numberOfCorrectlyExtractedRelations)/ new Double(numberOfGoldStandardRelationsToBeExtracted)));
	}

	/**
	 * Precision shows how many of the relations extracted by your system were extracted correctly.
	 * Precision = (number of correctly extracted relations) / (total number of extracted relations).
	 */
	public Double getPrecision(Integer numberOfCorrectlyExtractedRelations,
			Integer numberOfAllExtractedRelations) {
		if (numberOfAllExtractedRelations == 0)
			return (new Double(0.0));
		return (new Double(new Double(numberOfCorrectlyExtractedRelations) / new Double(numberOfAllExtractedRelations)));
	}

	/**
	 * F1 is the Harmonic mean of Precision and Recall.
	 * F1 = (2*Precision*Recall) / (Precision + Recall).
	 */
	public Double getF1(Double recall, Double precision) {
		Double denominator = recall + precision;
		
		// When the values equal to zero - so the Harmonic mean
		if (denominator == 0)
			return (new Double(0.0));
		return new Double((2.0 * recall * precision) / denominator);
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
	 * Method prints the last line to the output file - the measures (4 digits after the dot).
	 * @param bwWriter
	 * @param numberOfCorrectlyExtractedRelations
	 * @param numberOfGoldStandardRelationsToBeExtracted
	 * @param numberOfAllExtractedRelations
	 * @throws IOException 
	 */
	private void printMeasures(BufferedWriter bwWriter, Integer numberOfCorrectlyExtractedRelations,
			Integer numberOfGoldStandardRelationsToBeExtracted, Integer numberOfAllExtractedRelations) throws IOException {
		Double recall = this.getRecall(numberOfCorrectlyExtractedRelations, numberOfGoldStandardRelationsToBeExtracted);
		Double precision = this.getPrecision(numberOfCorrectlyExtractedRelations, numberOfAllExtractedRelations);
		bwWriter.append(String.valueOf(((float)Math.round(recall * 10000) / 10000)) + "\t" +
				String.valueOf(((float)Math.round(precision * 10000) / 10000)) + "\t" +
				this.getF1(recall, precision));
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
	 * Method sorts an array and keeps the order of elements
	 * @param arlList - list of relations
	 */
	private void removeDuplicateWithOrder(LinkedList<Relation> arrList) {
	   List<Relation> newList = new LinkedList<Relation>();
	   for (Iterator<Relation> iter = arrList.iterator(); iter.hasNext();) {
	      Relation element = (Relation)iter.next();
	      if (!isRelationFound(newList, element))
	         newList.add(element);
	   }
	   arrList.clear();
	   arrList.addAll(newList);
	}
	
	/**
	 * Methods checks if given relation is found in the given list.
	 * @param arrList - list of relations
	 * @param rel - given relation
	 * @return if given relation is found in the given list
	 */
	private boolean isRelationFound(List<Relation> arrList, Relation rel) {
		
		if (rel.getArg1() == null || rel.getArg2()== null ||
				rel.getRelName() == null || rel.getSentenceId() == null) {
			return (true);
		}
		
		for (Relation relation : arrList) {
			if (relation.getArg1().equals(rel.getArg1()) && relation.getArg2().equals(rel.getArg2()) &&
				relation.getRelName().equals(rel.getRelName()) && relation.getSentenceId().equals(rel.getSentenceId()))
				return (true);
		}
		
		return (false);
	}
	
	/**
	 * @param args (arg[0] - "Corpus" file name; arg[1] - "Annotation" file name; arg[2] - configuration type, arg[3] - output file name)
	 */
	public static void main(String[] args) {
		
		Ex3 ex3 = new Ex3();
		StringBuffer sb = new StringBuffer();
		String strCorpusFileName = args[0];
		String strAnnotationFileName = args[1];
		boolean bBest = false;
		if (args[2].equals("best")) {
			bBest = true;
		}
		String strOutFileName = args[3];
		String strLine = null;
		String strCorpusFile = null; // The corpus file as string
		BufferedReader brReader = null;
		BufferedWriter bwWriter = null;

		try {
			// Read all the corpus file into StringBuffer using loop
			brReader = new BufferedReader(new FileReader(strCorpusFileName));
			while ((strLine = brReader.readLine()) != null) {
				sb.append(strLine);
			}
			
			// Save the file as String
			strCorpusFile = sb.toString();
			
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
		
		// Load the annotations from annotations file
		List<Relation> lstGoldStandartRelations = ex3.loadAnnotation(strAnnotationFileName);
		
		// Number of gold standard relations
		Integer numberOfGoldStandardRelationsToBeExtracted = lstGoldStandartRelations.size();
		
		// Extract all the sentences from the XML file
		List<SentenceWithDependenciesAndNER> lstSentences = ex3.extractSentences(strCorpusFile);
		
		// Create the configuration - best or full
		Configuration<SentenceWithDependenciesAndNER> conf = null;
		if (bBest)
			conf = new Ex3BestConfiguration();
		else
			conf = new Ex3FullConfiguration();
		
		conf.generateConfiguration();
		
		Integer numberOfCorrectlyExtractedRelations = new Integer(0);
		Integer numberOfAllExtractedRelations = new Integer(0);
		
		// Create the list of all relations
		LinkedList<Relation> lstRelations = new LinkedList<Relation>();
		
		// Run on all sentences and for each one creates its relations
		for (SentenceWithDependenciesAndNER sen : lstSentences) {
			lstRelations.addAll(ex3.extractRelations(sen, conf));
		}
		
		// Remove duplicates
		ex3.removeDuplicateWithOrder(lstRelations);
		
		try {
			bwWriter = new BufferedWriter(new FileWriter(strOutFileName));
			
			// Determine which are good and which are bad
			for (Relation rel : lstRelations) {
				if (ex3.isCorrectlyExtractedRelation(rel, lstGoldStandartRelations))
					numberOfCorrectlyExtractedRelations++;
				numberOfAllExtractedRelations++;
				bwWriter.append(rel.toString());
				bwWriter.newLine();
			}
			
			// Print the last line - the measures
			ex3.printMeasures(bwWriter, numberOfCorrectlyExtractedRelations, numberOfGoldStandardRelationsToBeExtracted, numberOfAllExtractedRelations);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
			// Close file
			if (bwWriter != null) {
				try {
					bwWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
