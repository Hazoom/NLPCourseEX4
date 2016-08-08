package RE.api;

import java.util.List;
import RE.rep.Relation;
import RE.rep.Sentence;

public interface Rule<S extends Sentence> {

	/**
	 * @param sentence
	 * @return list of relations extracted from the sentence using a rule. 
	 * If no relations were extracted, return an empty list.
	 */
	public List<Relation> applyRule(S sentence); 

}
