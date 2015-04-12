package mallet.features;

import java.io.PrintWriter;

import cc.mallet.pipe.Pipe;
import mallet.SequenceLearnerOptions;

public abstract class FeatureFactory {

	protected SequenceLearnerOptions _learnerOptions = null;

	/**
	 * Build features
	 */
	public abstract void build();

	/**
	 * 
	 * @return a pipe for the feature factory
	 */
	public abstract Pipe getPipe();

	/**
	 * dumpCopy of the features for debugging
	 * @param outputStream
	 */
	public abstract void setFeatureDumpStream(PrintWriter outputStream);

	@SuppressWarnings("unchecked")
	public static final FeatureFactory loadFeatureFactory(SequenceLearnerOptions options,
			String featureFactoryString) throws Exception {
		Class featureFactoryClass = Class.forName(featureFactoryString);
		Object featureFactoryInstance = featureFactoryClass.newInstance();
		if(featureFactoryInstance instanceof FeatureFactory) {
			FeatureFactory factory =  (FeatureFactory)featureFactoryInstance;
			factory.setLearnerOptions(options);
			return factory;
		}
		throw new Exception("Loaded object is not a feature factory!");
	}

	public final void setLearnerOptions(SequenceLearnerOptions options) {
		_learnerOptions = options;
	}

	protected Object getOptionValue(String optionID) {
		return _learnerOptions.get(optionID);
	}
}
