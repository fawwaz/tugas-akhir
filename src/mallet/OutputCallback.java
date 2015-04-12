package mallet;

import cc.mallet.types.Sequence;

public interface OutputCallback {
	public void process(Sequence input, Sequence output);
}
