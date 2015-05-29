import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MyTFIDF {
	private Connection connection = null;
	private Statement statement = null;
	private PreparedStatement preparedstatement = null;
	private ResultSet resultset = null;
	//ArrayList<tftable> relevants;
	//ArrayList<tftable> irrelevants;
	ArrayList<String> relevants;
	ArrayList<String> irrelevants;
	
	public MyTFIDF(){
		relevants = new ArrayList<>();
		irrelevants = new ArrayList<>();
	}
	
    public double tf(List<String> doc, String term) {
        double result = 0;
        for (String word : doc) {
            if (term.equalsIgnoreCase(word))
                result++;
        }
        return result / doc.size();
    }
    
    public double idf(List<ArrayList<String>> documents, String term) {
        double n = 0;
        for (List<String> doc : documents) {
            for (String word : doc) {
                if (term.equalsIgnoreCase(word)) {
                    n++;
                    break;
                }
            }
        }
        return Math.log(documents.size() / n);
    }
    
    public double tfIdf(List<String> doc, List<ArrayList<String>> documents, String term) {
        return tf(doc, term) * idf(documents, term);
    }
    
    
    public void CountTFIDF(){
    	startConnection();
    	// Concatenate all list from database
    	
    	selectRelevantTweetwithFrequency(3, 3);
    	//for (tftable t : relevants) {
		//	System.out.println(t.token+ " "+ t.counter);
		//}
    	//for (tftable t : irrelevants) {
    	//	System.out.println(t.token+ " "+ t.counter);
		//}
    	
    	// gabungkan dokumen:
    	List<ArrayList<String>> documents = Arrays.asList(relevants,irrelevants);
    	ArrayList<String> doc = new ArrayList<>();    	
    	for (int i = 0; i < documents.size(); i++) {
    		Double sum=(double) 0;
    		//if(i<1){
    			doc = documents.get(i);
    		//}
    			
    		for (String string : documents.get(1)) {
				System.out.println(">>"+string);
			}
			
			
			for (String token: doc) {
				System.out.print("TF IDF token : "+token + " \n");
				System.out.format("adalah %.9f%n",tfIdf(doc, documents, token));
				sum = sum + tfIdf(doc, documents, token);
			}
			System.out.println("SUM : "+sum);
		}
    	
    	CloseConnection();
    }
    
    
    
    public void selectRelevantTweetwithFrequency(int freqrelevant,int freqirrelevant){
    	try{
    		preparedstatement = connection.prepareStatement("select distinct token,count(token) from anotasi_tweet_final group by token having count(token) > ? order by count(token)");
    		preparedstatement.setInt(1, freqrelevant);
    		resultset  = preparedstatement.executeQuery();
    		while(resultset.next()){
    			//tftable t = new tftable(resultset.getString("token"), resultset.getInt("count(token)"));
    			//relevants.add(t);
    			relevants.add(resultset.getString("token"));
    		}
    		
    		
    		preparedstatement = connection.prepareStatement("select distinct token,count(token) from wrong_tweet_final group by token having count(token) > ? order by count(token)");
    		preparedstatement.setInt(1, freqirrelevant);
    		resultset  = preparedstatement.executeQuery();
    		while(resultset.next()){
    			//tftable t = new tftable(resultset.getString("token"), resultset.getInt("count(token)"));
    			//relevants.add(t);
    			relevants.add(resultset.getString("token"));
    		}
    	}catch(SQLException e){
    		e.printStackTrace();
    	}
    }
    
    
    public void startConnection(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

		String DB_USERNAME	 	= "root";
		String DB_PASSWORD		= "";
		String DB_URL 			= "mysql://localhost/";
		String DB_NAME			= "mytomcatapp";
		String URL 				= "jdbc:"+DB_URL+DB_NAME;

		System.out.println("[INFO] Getting environment variables");
		System.out.println("DB_USERNAME \t: "+ DB_USERNAME);
		System.out.println("DB_PASSWORD \t: "+ DB_PASSWORD);
		System.out.println("DB_URL	\t: "+ DB_URL);
		System.out.println("DB_NAME \t: "+ DB_NAME);
		System.out.println("URL \t\t:"+URL);

		try{
			connection 	= (Connection) DriverManager.getConnection(URL,DB_USERNAME,DB_PASSWORD);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public void CloseConnection(){
		try{
			if(resultset!=null){
				resultset.close();
			}
			if(statement!=null){
				statement.close();
			}
			if(connection!=null){
				connection.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	class tftable{
		public String token;
		public Integer counter;
		public tftable(String t,Integer c){token=t;counter=c;}
	}
}
