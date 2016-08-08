package RE.rep;

import RE.impl.ex3.DependencyName;

public final class SyntacticDependency {
			
	private DependencyName dependencyName;
	private Token governorToken;
	private Token dependentToken;
	
	public SyntacticDependency(DependencyName dependencyName, Token governorToken,
			Token dependentToken) {
		super();
		this.dependencyName = dependencyName;
		this.governorToken = governorToken;
		this.dependentToken = dependentToken;
	}

	public DependencyName getDependencyName() {
		return dependencyName;
	}

	public void setDependencyName(DependencyName dependencyName) {
		this.dependencyName = dependencyName;
	}

	public Token getGovernorToken() {
		return governorToken;
	}

	public void setGovernorToken(Token governorToken) {
		this.governorToken = governorToken;
	}

	public Token getDependentToken() {
		return dependentToken;
	}

	public void setDependentToken(Token dependentToken) {
		this.dependentToken = dependentToken;
	}


}
