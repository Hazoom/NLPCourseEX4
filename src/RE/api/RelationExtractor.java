package RE.api;

import java.util.List;

import RE.rep.Relation;
import RE.rep.Sentence;

public interface RelationExtractor<S extends Sentence> {

	/**
	 * @param sentence
	 * @return list of relations extracted from the sentence using the given set of rules in the given Configuration.
	 * If no relations were extracted, return an empty list.
	 */
	public List<Relation> extractRelations(S sentence, Configuration<S> conf); 

}
