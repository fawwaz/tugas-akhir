import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class MyDBConnector {
	private Connection connection = null;
	private Statement statement = null;
	private PreparedStatement preparedstatement = null;
	private ResultSet resultset = null;


	public ArrayList<String> getTweetFromLocalDB(){
		ArrayList<String> retval = new ArrayList<>();
		try{
			preparedstatement = connection.prepareStatement("SELECT tweet from raw_tweet_final LIMIT 180");
			resultset = preparedstatement.executeQuery();
			while(resultset.next()){
				retval.add(resultset.getString("tweet"));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return retval;
	}
	
	public ArrayList<String> getTweetFromLocalDBWhereGuessedTrue(){
		ArrayList<String> retval = new ArrayList<>();
		try{
			preparedstatement = connection.prepareStatement("SELECT tweet from filtered_tweet_final WHERE `guessed-rule1`=1 LIMIT 180");
			resultset = preparedstatement.executeQuery();
			while(resultset.next()){
				retval.add(resultset.getString("tweet"));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return retval;
	}
	
	public ArrayList<String> getTweetFromLocalDBWhereLabelTrue(){
		ArrayList<String> retval = new ArrayList<>();
		try{
			preparedstatement = connection.prepareStatement("SELECT tweet from filtered_tweet_final WHERE label=1 ");
			resultset = preparedstatement.executeQuery();
			while(resultset.next()){
				retval.add(resultset.getString("tweet"));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return retval;
	}
	
	public ArrayList<String> getTweetFromLocalDBWhereLabelFalse(){
		ArrayList<String> retval = new ArrayList<>();
		try{
			preparedstatement = connection.prepareStatement("SELECT tweet from filtered_tweet_final WHERE label=2 ");
			resultset = preparedstatement.executeQuery();
			while(resultset.next()){
				retval.add(resultset.getString("tweet"));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return retval;
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
	
	public void InsertTokenToAnotasiTweet(String token,Long twitter_tweet_id){
		try{
			preparedstatement = connection.prepareStatement("INSERT into anotasi_tweet_final (token,twitter_tweet_id) VALUES (?,?)");
			preparedstatement.setString(1, token);
			preparedstatement.setLong(1, twitter_tweet_id);
			preparedstatement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
}
