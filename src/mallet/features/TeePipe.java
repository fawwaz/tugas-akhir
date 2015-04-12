package mallet.features;

import java.io.PrintWriter;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Token;

public class TeePipe extends TokenTransformingPipe{
	private static final long serialVersionUID = 4535561324958774377L;


	private transient PrintWriter _outputStream = null;

	public TeePipe() {
		_outputStream = null;
	}

	public TeePipe(PrintWriter outputStream) {
		_outputStream = outputStream;
	}

	@Override
	public Token transform(Token token) {
		if(_outputStream != null)
			_outputStream.println(token);
		return token;
	}
}
