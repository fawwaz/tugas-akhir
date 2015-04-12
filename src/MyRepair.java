import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class MyRepair {
	
	private Connection connection = null;
	private Statement statement = null;
	private PreparedStatement preparedstatement = null;
	private ResultSet resultset = null;

	
	public void CrawlTweet(){
		System.out.println("[INFO] Connecting to twitter");
		ConfigurationBuilder conf = new ConfigurationBuilder();
		conf.setDebugEnabled(true)
			.setOAuthConsumerKey("dmDMgfbO9cA0aH3uiNvEcgAPu")
			.setOAuthConsumerSecret("vdGlY077SK4Cw1y26oNxcfmBwe0oyFv1Xj17wKai72wZwBZsd6")
			.setOAuthAccessToken("470084145-mFLzF9yv4wlfQuRWqUcARJ9Wpfjq4fQBG3Z52MNQ")
			.setOAuthAccessTokenSecret("fkULaBtZKHfd7IUIYhmwYWgH0Fjm34QHygCeT5EoYgjlN");
		
		TwitterFactory tf = new TwitterFactory(conf.build());
		Twitter twitter = tf.getInstance();
		System.out.println("[INFO] Connected");
		ArrayList<Long> tweettofind = selectId();
		for (int i = 0; i < tweettofind.size(); i++) {
			System.out.println("[INFO] Getting information about id = " + tweettofind.get(i));
			try {
				updateText(twitter.showStatus(tweettofind.get(i)));
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				System.out.println("[WARNING] Failed to update tweet with id = "+tweettofind.get(i));
				e.printStackTrace();
			}
		}
	}

	public ArrayList<Long> selectId(){
		ArrayList<Long> ids = new ArrayList<>();
		try{
			/*
			 * LIMIT NYA TWITTER 180 JANGAN LUPA 
			 * 
			 * */
			preparedstatement = connection.prepareStatement("select twitter_tweet_id from mytomcatapp.raw_tweet limit 180");

			resultset = preparedstatement.executeQuery();
			while(resultset.next()){
				ids.add(resultset.getLong("twitter_tweet_id"));
			}
			return ids;
		}catch(SQLException e){
			e.printStackTrace();
		}
		return ids;
	}
	
	public void updateText(Status status){
		try{
			preparedstatement = connection.prepareStatement("UPDATE raw_tweet SET tweet = ? WHERE twitter_tweet_id = ?");
			preparedstatement.setString(1, status.getText());
			preparedstatement.setLong(2, status.getId());
			preparedstatement.executeUpdate();
			
			System.out.println("[INFO] Updated id = "+status.getId() + " with text = " + status.getText());
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
}
