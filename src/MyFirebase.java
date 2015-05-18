import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class MyFirebase {
	private Connection connection = null;
	private Statement statement = null;
	private PreparedStatement preparedstatement = null;
	private ResultSet resultset = null;
	Firebase fb;
	
	MyFirebase(){
		fb = new Firebase("https://twitterevents.firebaseio.com/");
	}
	
	public void explodeTweet(){
		final AtomicBoolean done = new AtomicBoolean(false);
		System.out.println("[MASUK KE SINI]");
		fb.child("crawled_tweets").addListenerForSingleValueEvent(new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot crawled_tweets) {
				// TODO Auto-generated method stub
				for (DataSnapshot crawled_tweet : crawled_tweets.getChildren()) {
					System.out.println("Tweet is : "+crawled_tweet.child("text").getValue(String.class));
				}
				
				done.set(true);
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub
				System.out.println("[ERROR] Failed To Get from firebase");
			}
		});
		
		while(!done.get());
	}
	
	
	public void MakeRelevangOrIrrelevantData(){
		// ambil labelnya dulu
		ArrayList<MyPair> tweets = getTweetandLabel();
		for (int i = 0; i < tweets.size(); i++) {
			MyPair p = tweets.get(i);
			final AtomicBoolean done = new AtomicBoolean(false);
			if(p.label.equals(1)){
				// set as relevant
				fb.child("relevant_tweets").child(String.valueOf(p.id_tweet)).child("is_processed").setValue(false, new Firebase.CompletionListener() {
					
					@Override
					public void onComplete(FirebaseError arg0, Firebase arg1) {
						if(arg0!=null){System.out.println("Error saving relevant");}else{System.out.println("Success saving relevant");}
						done.set(true);
					}
				});
				
			}else if(p.label.equals(2)){
				// set as irrelevant
				fb.child("irrelevant_tweets").child(String.valueOf(p.id_tweet)).child("is_processed").setValue(false,new Firebase.CompletionListener() {
					
					@Override
					public void onComplete(FirebaseError arg0, Firebase arg1) {
						if(arg0!=null){System.out.println("Error saving irrelevant");}else{System.out.println("Success saving not relevant");}
						done.set(true);
					}
				});
			}
			while(!done.get());
		}
		
	}
	
	
	
	public ArrayList<MyPair> getTweetandLabel(){
		ArrayList<MyPair> ids = new ArrayList<>();
		try{
			/*
			 * LIMIT NYA TWITTER 180 JANGAN LUPA 
			 * 
			 * */
			preparedstatement = connection.prepareStatement("select twitter_tweet_id,label from mytomcatapp.raw_tweet_final where label <> 0");

			resultset = preparedstatement.executeQuery();
			while(resultset.next()){
				MyPair p = new MyPair(resultset.getLong("twitter_tweet_id"), resultset.getInt("label"));
				ids.add(p);
			}
			return ids;
		}catch(SQLException e){
			e.printStackTrace();
		}
		return ids;
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
	
	public class MyPair{
		public Long id_tweet;
		public Integer label;
		public MyPair(Long a,Integer b) {
			id_tweet = a; label = b;
		}
	}
}
