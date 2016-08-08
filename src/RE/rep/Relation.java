package RE.rep;

public final class Relation {
	
	public enum RelationName {
		OrgBased_In, Located_In, Work_For, Live_In, Kill
	}

	private RelationName relName;
	private String arg1;
	private String arg2;
	private String sentenceId;

	public Relation(RelationName relName, String arg1, String arg2,
			String sentenceId) {
		super();
		this.relName = relName;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.sentenceId = sentenceId;
	}

	@Override
	public String toString() {
		return sentenceId + "\t" + arg1 + "\t" + relName + "\t"+arg2;
	}

	public RelationName getRelName() {
		return relName;
	}

	public void setRelName(RelationName relName) {
		this.relName = relName;
	}

	public String getArg1() {
		return arg1;
	}

	public void setArg1(String arg1) {
		this.arg1 = arg1;
	}

	public String getArg2() {
		return arg2;
	}

	public void setArg2(String arg2) {
		this.arg2 = arg2;
	}

	public String getSentenceId() {
		return sentenceId;
	}

	public void setSentenceId(String sentenceId) {
		this.sentenceId = sentenceId;
	}	
}
