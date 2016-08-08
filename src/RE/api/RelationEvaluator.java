package RE.api;

import java.util.List;

import RE.rep.Relation;

public interface RelationEvaluator {

	/**
	 * @param filename
	 * @return List of gold-standard relations loaded from the given file
	 * Assume that the file, which the method will fetch, is in the "Annotation" file format
	 * (see ex2 instructions)
	 */
	public List<Relation> loadAnnotation(String filename);
	
	/**
	 * @param relA
	 * @param relB
	 * @return true if the two relations match, otherwise - false
	 * 
	 * Two relations match, if they are extracted from the same sentence,
	 * have the same relation name and exactly the same values of arguments
	 */
	public boolean isMatch(Relation relA, Relation relB);
	
	/**
	 * @param relation
	 * @param annotation - gold-standard list of relations
	 * @return true if the relation is found within the gold-standard annotation.
	 * 
	 * The relation is considered correctly extracted, if exactly the same relation is found in the gold-standard annotation.
	 * Use isMatch() method to define whether the relation matches some gold-standard relation or not.
	 */
	public boolean isCorrectlyExtractedRelation(Relation relation, List<Relation> annotation);
		
	/** calculate the evaluation measures as described in ex2 instructions**/
	public Double getRecall(Integer numberOfCorrectlyExtractedRelations, Integer numberOfGoldStandardRelationsToBeExtracted);
	public Double getPrecision(Integer numberOfCorrectlyExtractedRelations, Integer numberOfAllExtractedRelations);
	public Double getF1(Double recall, Double precision);
	
}
