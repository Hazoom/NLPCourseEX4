package RE.rep;

import java.util.List;
import RE.rep.Sentence;

public final class SentenceWithDependenciesAndNER extends Sentence {
	
	public SentenceWithDependenciesAndNER(String xmlFormattedSentence,
			List<? extends Token> tokens, List<SyntacticDependency> dependencies) {
		super(xmlFormattedSentence, tokens);
		this.dependencies = dependencies;
	}

	private List<SyntacticDependency> dependencies; //notice that dependencies attribute is added  
	

	public List<SyntacticDependency> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<SyntacticDependency> dependencies) {
		this.dependencies = dependencies;
	}	
	
	public Sentence getSentence(){
		return new Sentence(getXmlFormattedSentence(),getTokens());
	}
}
