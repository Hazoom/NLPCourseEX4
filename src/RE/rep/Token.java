package RE.rep;

public class Token {
	
	private Integer tokenId;
	private String sentenceId;
	private String word;
	private String lemma;
	private POS pos;
	
	/**
	 * @param tokenId
	 * @param sentenceId - use the first token of each sentence as sentenceId (e.g. "sent23")
	 * @param word
	 * @param lemma
	 * @param pos - pay attention, "OTHER" pos tag was added
	 */
	public Token(Integer tokenId, String sentenceId, String word,
			String lemma, POS pos) {
		super();
		this.tokenId = tokenId;
		this.sentenceId = sentenceId;
		this.word = word;
		this.lemma = lemma;
		this.pos = pos;
	}

	public Integer getTokenId() {
		return tokenId;
	}

	public void setTokenId(Integer tokenId) {
		this.tokenId = tokenId;
	}

	public String getSentenceId() {
		return sentenceId;
	}

	public void setSentenceId(String sentenceId) {
		this.sentenceId = sentenceId;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getLemma() {
		return lemma;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	public POS getPos() {
		return pos;
	}

	public void setPos(POS pos) {
		this.pos = pos;
	}
	
	
	
	
}
