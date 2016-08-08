package RE.api;

import java.util.List;

import RE.rep.Sentence;

public interface Configuration<S extends Sentence>{
	
	public void generateConfiguration(); 
	
	/**
	 * @return list of rules to be applied according to this configuration.
	 */
	public List<Rule<S>> getConfiguration(); 
	
}
