import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class MyToken {
	String token;
	Set<String> features;
	public MyToken(String token){
		this.token = token;
		features = new HashSet<>();
	}
	public void setFeatures(ArrayList<String> features){
		for (int i = 0; i < features.size(); i++) {
			this.features.add(features.get(i));
		}
	}
	
	public void addFeature(String featurename){
		this.features.add(featurename);
	}
	
	public String getString(){
		StringBuilder sb = new StringBuilder();
		sb.append(token);
		for (Iterator iterator = features.iterator(); iterator.hasNext();) {
			String feature = (String) iterator.next();
			sb.append(" ");
			sb.append(feature);
			
		}
		sb.append(" ");
		return sb.toString();
	}
}
