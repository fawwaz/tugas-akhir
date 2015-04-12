import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyAnotator {
	ArrayList<String> inputs;
	ArrayList<MyToken> tokens; 
	
	static final String NUMBER 				= "ISNUMBER";
	static final String DATE 				= "ISDATE";
	static final String PHONENUMBER 		= "ISPHONENUMBER";
	static final String DISKON				= "ISDISKON";
	static final String HARGA				= "ISHARGA";
	static final String JAM					= "ISJAM";
	static final String PINBB				= "ISPINBB";
	static final String URL					= "ISURL";
	static final String PUNCTUATION			= "ISPUNCTUATION";
	static final String MENTION				= "ISMENTION";
	static final String HASHTAG				= "ISHASHTAG";
	static final String SEPARATOR			= "ISSEPARATOR";
	static final String EMOTICON			= "ISEMOTICON";
	static final String UNICODE				= "ISUNICODE";
	
	
	public MyAnotator(ArrayList<String> inputs){
		this.inputs = inputs;
		this.tokens = new ArrayList<>();
		anotate();
	}
	
	
	private void anotate(){
		for (int i = 0; i < inputs.size(); i++) {
			String input = inputs.get(i);
			MyToken token = new MyToken(input);
			
			if(input.matches(Twokenize.waktu_tgl_1) || input.matches(Twokenize.waktu_jam_1)){
				token.addFeature(DATE);
			}
			
			if(input.matches(Twokenize.nohp)){
				token.addFeature(PHONENUMBER);
			}
			
			if(input.matches(Twokenize.diskon)){
				token.addFeature(DISKON);
			}
			
			if(input.matches(Twokenize.matauang)){
				token.addFeature(HARGA);
			}
			
			if(input.matches(Twokenize.pinBB)){
				token.addFeature(PINBB);
			}
			
			if(input.matches(Twokenize.url)){
				token.addFeature(URL);
			}
			
			if(input.matches(Twokenize.AtMention)){
				token.addFeature(MENTION);
			}
			
			if(input.matches(Twokenize.Hashtag)){
				token.addFeature(HASHTAG);
			}
			
			if(input.matches(Twokenize.emoticon)){
				token.addFeature(EMOTICON);
			}
			
			System.out.println("Masih ada kode yang di hardcode terutama yang Number, punctutation mulai -->");
			if(input.matches("\\d+")){
				token.addFeature(NUMBER);
			}
			
			if(input.matches("\\p{P}")){
				token.addFeature(PUNCTUATION);
			}
			
			
			
			
			tokens.add(token);
		}
	}
	

	public ArrayList<String> getExtractedFeature(){
		ArrayList<String> retval = new ArrayList<>();
		for (Iterator iterator = tokens.iterator(); iterator.hasNext();) {
			MyToken token = (MyToken) iterator.next();
			retval.add(token.getString());
		}
		return retval;
	}
}
