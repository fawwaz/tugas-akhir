import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


public class MyEndProcess {
	private Connection connection = null;
	private Statement statement = null;
	private PreparedStatement preparedstatement = null;
	private ResultSet resultset = null;
	
	
	public void fillEventTable(){
		try {
			preparedstatement = connection.prepareStatement("SELECT sequence_num,twitter_tweet_id,token,label2 from anotasi_tweet_final");
			
			resultset = preparedstatement.executeQuery();
			
			Long last_tweet_id = (long) 0;
			StringBuffer name = new StringBuffer("");
			StringBuffer place = new StringBuffer("");
			StringBuffer time = new StringBuffer("");
			StringBuffer info = new StringBuffer("");
			boolean firsttime = true;
			Integer a=0;
			
			
			while(resultset.next()){
				
				// switch according to label
				String token 			= resultset.getString("token").toLowerCase();
				String label2			= resultset.getString("label2").toLowerCase();
				Long twitter_tweet_id 	= resultset.getLong("twitter_tweet_id");
				
				
				if(firsttime){
					System.out.println("TOD");
					last_tweet_id = twitter_tweet_id;
					firsttime=false;
				}
				
				
				// cek dulu apakah sudah berubah last tweet dr yng sekarang ?
				if(!last_tweet_id.equals(twitter_tweet_id)){
					// insert dulu ke database, semuanya to String;  perhatikan yang diinsert previous tweet id (last_twet_id) beda sama yang kalau udah akhir (kolom diabwah)
					insertTupleToDB(last_tweet_id, name.toString(), place.toString(), time.toString(), info.toString());
					/*
					System.out.println("===Tuple Yg akan diinsert===");
					System.out.println("Tuple ke : "+a);
					System.out.println("Tweet_id "+twitter_tweet_id);
					System.out.println("Name:\t"+name.toString());
					System.out.println("Place:\t"+place.toString());
					System.out.println("Time:\t"+time.toString());
					System.out.println("Info:\t"+info.toString());
					*/
					// Switch
					last_tweet_id = twitter_tweet_id;
					name = new StringBuffer("");
					place = new StringBuffer("");
					time = new StringBuffer("");
					info = new StringBuffer("");
					a++;
				}else{
					
				}


				
				
				if(label2.equals("i-name")){
					name.append(token+ " ");
				}else if(label2.equals("i-place")){
					place.append(token+" ");
				}else if(label2.equals("i-time")){
					time.append(token + " ");
				}else if(label2.equals("i-info")){
					info.append(token+" ");
				}
				

				if(resultset.isLast()){
					// kalau ini yagn diinsert current_twitter_tweet_id beda
					insertTupleToDB(twitter_tweet_id, name.toString(), place.toString(), time.toString(), info.toString());
					/*
					System.out.println("===Tuple Yg akan diinsert===");
					System.out.println("Tuple ke : "+a);
					System.out.println("Tweet_id "+twitter_tweet_id);
					System.out.println("Name:\t"+name.toString());
					System.out.println("Place:\t"+place.toString());
					System.out.println("Time:\t"+time.toString());
					System.out.println("Info:\t"+info.toString());
					*/
				}
				// Switch sb.to string kalau iterasi dengan twitter_tweet_id nya udah beda dari yang before
				
					// insert ke database tuple data sb.tostring
				// warn
				/*
				System.out.println("Tweet :"+tweet);
				List<String> tokenized = tokenizer.tokenizeRawTweetText(tweet);
				for(String token : tokenized){
					InsertTokenToAnotasiTweet(token, twitter_tweet_id);
				}
				*/
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void insertTupleToDB(Long twitter_tweet_id,String name,String place,String time,String info){
		try {
			preparedstatement = connection.prepareStatement("insert into event values (?,?,?,?,?)");
			preparedstatement.setLong(1, twitter_tweet_id);
			preparedstatement.setString(2, name);
			preparedstatement.setString(3, place);
			preparedstatement.setString(4, time);
			preparedstatement.setString(5, info);
			preparedstatement.executeUpdate();
			System.out.println("[SUCCESS] inserted tupple to table event");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("[ERROR] Failed to insert tuple to databaase event");
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
