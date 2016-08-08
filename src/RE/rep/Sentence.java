package RE.rep;

import java.util.List;

public class Sentence{
	private String xmlFormattedSentence;
	private List<? extends Token> tokens;
	
	public Sentence(String xmlFormattedSentence, List<? extends Token> tokens) {
		super();
		this.xmlFormattedSentence = xmlFormattedSentence;
		this.tokens = tokens;
	}

	public String getXmlFormattedSentence() {
		return xmlFormattedSentence;
	}

	public void setXmlFormattedSentence(String xmlFormattedSentence) {
		this.xmlFormattedSentence = xmlFormattedSentence;
	}

	public List<? extends Token> getTokens() {
		return tokens;
	}

	public void setTokens(List<? extends Token> tokens) {
		this.tokens = tokens;
	}


	
	
}
