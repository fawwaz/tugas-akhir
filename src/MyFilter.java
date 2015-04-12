import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class MyFilter {
	private Connection connection = null;
	private Statement statement = null;
	private PreparedStatement preparedstatement = null;
	private PreparedStatement preparedstatement2 = null;
	private ResultSet resultset = null;
	
	private String twitterurl = "http://t\\.co/\\w+";
	private Integer sisakarakter(String text){
		String modified = text.replaceAll("@\\w+", "").replaceAll("#\\w+", "").replace(twitterurl, "").trim();
		System.out.println("[AFTER] LENGTH :"+modified.length()+"modified : "+modified);
		return modified.length();
	}
	
	
	/*
	 * Membuat confusion matrix
	 * */
	public void evaluate(){
		try{
			preparedstatement = connection.prepareStatement("SELECT count(*) from filtered_tweet where label = 1 AND guessed = 1");
			resultset = preparedstatement.executeQuery();
			
			resultset.next();
			Integer truepositive = resultset.getInt("count(*)");
			
			preparedstatement = connection.prepareStatement("SELECT count(*) from filtered_tweet where label = 1 AND guessed = 2");
			resultset = preparedstatement.executeQuery();
			
			resultset.next();
			Integer falsepositive = resultset.getInt("count(*)");
			
			preparedstatement = connection.prepareStatement("SELECT count(*) from filtered_tweet where label = 2 AND guessed = 2");
			resultset = preparedstatement.executeQuery();
			
			resultset.next();
			Integer truenegative = resultset.getInt("count(*)");
			
			preparedstatement = connection.prepareStatement("SELECT count(*) from filtered_tweet where label = 2 AND guessed = 1");
			resultset = preparedstatement.executeQuery();
			
			resultset.next();
			Integer falsenegative = resultset.getInt("count(*)");
			
			System.out.println("==== CONFUSSION MATRIX ====");
			System.out.println("\tR\t|\tIR\t| / classified as :");
			System.out.println("\t"+truepositive+"\t|\t"+falsepositive+"\t|<---R");
			System.out.println("\t"+falsenegative+"\t|\t"+truenegative+"\t|<---IR");
			// Get first
			
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		System.out.println("STATISTIK SAAT INI :");
		
	}
	
	/*
	 * ITERASI SELURUH TWEET DONT CHANGE
	 * */
	public void updateFilteredTweet(){
		try{
			preparedstatement = connection.prepareStatement("SELECT twitter_tweet_id,tweet from filtered_tweet");
			resultset = preparedstatement.executeQuery();
			while(resultset.next()){
				//doupdatemodifiedlength(resultset.getString("tweet"), resultset.getLong("twitter_tweet_id"));
				//doupdatewordcounter(resultset.getString("tweet"), resultset.getLong("twitter_tweet_id"));
				fill_modified_tweet_1(resultset.getString("tweet"), resultset.getLong("twitter_tweet_id"));
			}
			System.out.println("[INFO] Successful inserted");
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	/*
	 * Mengisi kolom modified_tweet_1
	 * */
	private void fill_modified_tweet_1(String tweet, Long twitter_tweet_id){
		Twokenize twokenizer = new Twokenize();
		ArrayList<String> tweets = new ArrayList<>();
		tweets.addAll(twokenizer.tokenizeRawTweetText(tweet));
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < tweets.size(); i++) {
			if(tweets.get(i).matches(Twokenize.nohp)){
				sb.append("PHONE");
				sb.append(" ");
			}else if(tweets.get(i).matches(Twokenize.bulan_1)){
				sb.append("MONTH");
				sb.append(" ");
			}else if(tweets.get(i).matches(Twokenize.tanggal)){
				sb.append("DATE");
				sb.append(" ");
			}else if(tweets.get(i).matches(Twokenize.hari)){
				sb.append("DAY");
				sb.append(" ");
			}else if(tweets.get(i).matches(Twokenize.bulan_2)){
				sb.append("MONTH");
				sb.append(" ");
			}else if(tweets.get(i).matches(Twokenize.bulan_3)){
				sb.append("MONTH");
				sb.append(" ");
			}else if(tweets.get(i).matches(Twokenize.bulan_4)){
				sb.append("MONTH");
				sb.append(" ");
			}else if(tweets.get(i).matches(Twokenize.bulan_5)){
				sb.append("MONTH");
				sb.append(" ");
			}else{
				sb.append(tweets.get(i));
				if(i<tweets.size()){
					sb.append(" ");
				}
			}
		}
		String tweetmodified = sb.toString();
		System.out.println("[UPDATED] Tweet modified:" + tweetmodified);
		
	}
	
	
	
	/*
	 * Mengisi kolom word_counter
	 * */
	private void doupdatewordcounter(String tweet, Long twitter_tweet_id){
		String[] counter = tweet.split(" ");
		Integer jumlah_kata = counter.length;
		try{
			System.out.println("[UPDATED] Tweet id :" + twitter_tweet_id + " counter character :" +jumlah_kata);
			preparedstatement2 = connection.prepareStatement("UPDATE filtered_tweet SET word_counter = ? WHERE twitter_tweet_id = ? ");
			preparedstatement2.setInt(1, jumlah_kata);
			preparedstatement2.setLong(2, twitter_tweet_id);
			//System.out.println("EXECUTE UPDATE MASIH DI IDABLED BIAR GAK KEPENCET");
			preparedstatement2.executeUpdate();
		}catch (SQLException e){
			e.printStackTrace();
		}
		
	}
	
	
	/*
	 * MEngisi kolom modified length
	 * */
	private void doupdatemodifiedlength(String text,Long twitter_tweet_id){
		//
		// pre process dulu strignnya kaya hitung jumlah karakter, jumlah symbol @ dst
		
		Integer jumlah_karakter = text.length();
		Integer prediction = (sisakarakter(text)>83) ? 1 : 2; 
		try{
			System.out.println("[UPDATED] Tweet id :" + twitter_tweet_id + " modified_length :" + sisakarakter(text));
			preparedstatement2 = connection.prepareStatement("UPDATE filtered_tweet SET guessed = ? WHERE twitter_tweet_id = ? ");
			preparedstatement2.setInt(1, prediction);
			preparedstatement2.setLong(2, twitter_tweet_id);
			//System.out.println("EXECUTE UPDATE MASIH DI IDABLED BIAR GAK KEPENCET");
			preparedstatement2.executeUpdate();
		}catch (SQLException e){
			e.printStackTrace();
		}
	}
	
	/*
	 * Mengisi kolom tweet pada filtered_tweet yang sudah diperbaiki text nya dari query twitter 
	 * */
	public void insertFilterTweet(){
		try{
			preparedstatement = connection.prepareStatement("SELECT id_raw_tweet,twitter_tweet_id,tweet,label from raw_tweet limit 180");
			resultset = preparedstatement.executeQuery();
			while(resultset.next()){
				preparedstatement2 = connection.prepareStatement("INSERT INTO filtered_tweet (id_raw_tweet,twitter_tweet_id,tweet,label) VALUES (?,?,?,?)");
				preparedstatement2.setInt(1, resultset.getInt("id_raw_tweet"));
				preparedstatement2.setLong(2, resultset.getLong("twitter_tweet_id"));
				preparedstatement2.setString(3, resultset.getString("tweet"));
				preparedstatement2.setInt(4, resultset.getInt("label"));
				preparedstatement2.executeUpdate();
			}
			System.out.println("[INFO] Successful inserted");
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
