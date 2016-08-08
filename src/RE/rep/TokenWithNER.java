package RE.rep;

public final class TokenWithNER extends Token {
	
	public enum NamedEntityType{
		ORGANIZATION,
		LOCATION,
		PERSON,
		NOT_DEFINED
	}

	private NamedEntityType namedEntityType;
	
	/**
	 * @param tokenId
	 * @param sentenceId - use the first token of each sentence as sentenceId (e.g. "sent23")
	 * @param word
	 * @param lemma
	 * @param pos - pay attention, "OTHER" pos tag was added
	 * @param namedEntityType - see enum NamedEntityType above
	 */
	public TokenWithNER(Integer tokenId, String sentenceId, String word,
			String lemma, POS pos, NamedEntityType namedEntityType) {
		super(tokenId, sentenceId, word, lemma, pos);
		this.namedEntityType = namedEntityType;
	}


	public NamedEntityType getNamedEntityType() {
		return namedEntityType;
	}

	public void setNamedEntityType(NamedEntityType namedEntityType) {
		this.namedEntityType = namedEntityType;
	}
}
