package mallet;

import cc.mallet.fst.Transducer;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.InstanceList;

public interface SequenceLearner {
	public void train(InstanceList trainingdata) throws Exception;
	public void train(InstanceList trainingdata, double splitratio) throws Exception;
	public void train(InstanceList trainingdata, InstanceList evaluationdata) throws Exception;
	
	public void saveModel(String modelfilename) throws Exception;
	public void loadModel(String modelfilename) throws Exception;
	
	public void classify(String filename,OutputCallback outputcallback) throws Exception;
	public SequenceLearnerOptions getLearnerOptions();
	
	public Pipe getInputPipe() throws Exception;
	public Transducer getModel();
}
