import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

import cc.mallet.fst.CRF;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.ArrayIterator;
import cc.mallet.pipe.iterator.LineGroupIterator;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;


public class MyMallet {
	static Pipe pipe=null;
	static InstanceList instancelist = null;
	
	public MyMallet(){
		pipe = buildPipe();
	}
	

	public void domallet(ArrayList<String> input){		
		InstanceList instances = new InstanceList(pipe);
		/*for (int i = 0; i < input.size(); i++) {
			instances.addThruPipe(new ArrayIterator(input));
		}*/
		String[] sumber = input.toArray(new String[input.size()]);
		instances.addThruPipe(new Instance(sumber, null, "hasilinput", null));
	
	}
		
		
	
	public Pipe buildPipe(){
		ArrayList pipeList = new ArrayList();
		
		pipeList.add(new CharSequenceArray2TokenSequence());
		pipeList.add(new TokenSequence2FeatureSequence());
		//pipeList.add(new FeatureSequence2FeatureVector());
		//pipeList.add(new Target2Label());
		pipeList.add(new PrintInputAndTarget());
		// Tujuan akhir featuressequence2featurevector
		return(new SerialPipes(pipeList));
	}
	
	

	// Minimal nyoba kode dulu aja 
	/*public void doMallet(){
		String[] coba = {"Ini tes string cboa lagi","bisa gak ini string juga bukan", "nah ini juga iya gak ?"};
		Instance ob = new Instance(coba, null, "array-1", null);
		CharSequenceArray2TokenSequence converter = new CharSequenceArray2TokenSequence();
		Instance res = converter.pipe(ob);
		Object o = res.getData();
		System.out.println(o.toString());
	}*/

}
