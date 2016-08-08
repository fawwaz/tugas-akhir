import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Asus
 */
public class DumpRetrievedTweet {
    private Connection connection = null;
    private Statement statement = null;
    private PreparedStatement preparedstatement = null;
    private ResultSet resultset = null;
    
    File file;
    FileWriter writer;
    
    ArrayList<Long> tweet_ids = new ArrayList<>();
    
    public void StartConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String DB_USERNAME = "root";
        String DB_PASSWORD = "";
        String DB_URL = "mysql://localhost/";
        String DB_NAME = "mytomcatapp";
        String URL = "jdbc:" + DB_URL + DB_NAME;

        System.out.println("[INFO] Getting environment variables");
        System.out.println("DB_USERNAME \t: " + DB_USERNAME);
        System.out.println("DB_PASSWORD \t: " + DB_PASSWORD);
        System.out.println("DB_URL	\t: " + DB_URL);
        System.out.println("DB_NAME \t: " + DB_NAME);
        System.out.println("URL \t\t:" + URL);

        try {
            connection = (Connection) DriverManager.getConnection(URL, DB_USERNAME, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void CloseConnection() {
        try {
            if (resultset != null) {
                resultset.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void selectTokenFromDB() {
        try {
            //preparedstatement = connection.prepareStatement("select t.id from (select * from tweet_baru_sanitized limit 5000,5000) as t where is_labelled_anotator1 = 1 && is_labelled_anotator3=0");
            preparedstatement = connection.prepareStatement("select twitter_tweet_id as id from filtered_tweet_final where label = 1 and isRetrieved =0");
            resultset = preparedstatement.executeQuery();
            Integer a = 0;
            while (resultset.next()) {
                Long twitter_tweet_id = resultset.getLong("id");
                tweet_ids.add(twitter_tweet_id);
            }
            System.out.println("[INFO] Successful retrieved");
            System.out.println("Yang belum di ambil : "+tweet_ids.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void doGetTweetData() throws IOException{
        startWriter("Old_DumpRetrievedTweetWithId");
        try{
            GetTweetData();
        }catch(TwitterException twe){
            System.out.println("Exception occured with twe");
            twe.printStackTrace();
            closeWriter();
        }
    }
    private void GetTweetData() throws TwitterException, IOException {
        System.out.println("Seting up twitter");
        Twitter twitter = new TwitterFactory().getInstance();
//        String CONSUMER_KEY = "9zVRlQTXlTKf4QeYMiucyXqg9";
//        String CONSUMER_SECRET = "xH0H2SfcXj914z1DGB7HgZZ012ntSqRbfnLFw5A6v1TB94Y2O1";
//        String TWITTER_TOKEN = "753222147695259648-QnzInB98PtwpFb75dqlP1J7SSOQMcMX";
//        String TWITTER_TOKEN_SECRET = "uxJHGE1e2kFBJYoNRRFfy5gBLXYaRrzCUycRncSkoLsle";
        
//        String CONSUMER_KEY = "nNV9KxWFVmdI0a2shhJImcEGk";
//        String CONSUMER_SECRET = "I4AvaGOZRFtLFTqAkpKrfFOnV13Ej2seiwnNQ47z0qE9ORoBhW";
//        String TWITTER_TOKEN = "753222147695259648-kY3Gq1O6FqHw6LQViuTQAhLbEQLZRZX";
//        String TWITTER_TOKEN_SECRET = "a6hsmYipeGvqJcrIUUZah4ZhH7B8wbY77hqNWwojQysiO";
        
//        String CONSUMER_KEY = "PBMKK9P2YD9MFgDfdANQt2Zzc";
//        String CONSUMER_SECRET = "ldB0FybCQIjfYLzuYE4CeGtvcevaSkFxPENGbFd19Yn2crXa5R";
//        String TWITTER_TOKEN = "753222147695259648-ZWqj3CvdQAXeVl9zfa71BCRcxeD2yAi";
//        String TWITTER_TOKEN_SECRET = "1bypYSL1D1t1Gt6nQLwEbyRg5rniOUHfJhTYuVWYWvAvP";
        
//        String CONSUMER_KEY = "tf0Cbp3ZQcJh9vwkQ9vInw6WS";
//        String CONSUMER_SECRET = "gRmcjuqrSH40K47CkRFEO5OVthUtExEW7xrZU2oWiRcpPmzRz9";
//        String TWITTER_TOKEN = "126239739-qaXgO4urp91PXLT2RgkxlyONwAQyXnq236dhqtTe";
//        String TWITTER_TOKEN_SECRET = "AIykIeAKGFHRucTPgNYZd9bqdbZEJQhMsF5cGiyBLUII7";
        
        String CONSUMER_KEY = "EmmSsTEML5XrB3SYBUOOKxqn6";
        String CONSUMER_SECRET = "J1TrjmcqhBd1cURBTjUCwNqcf5IxCfdInAs74TVT9axvSwLnlJ";
        String TWITTER_TOKEN = "126239739-ktoaE8NhM4S9CjFhrktTdpuvY7fkbqj1nYxfaQsD";
        String TWITTER_TOKEN_SECRET = "a5fFQbKO9meoqnY31wwlZF0nqDHtqfxlwUla1uaAT8Y0p";
        
        twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        AccessToken accesstoken = new AccessToken(TWITTER_TOKEN, TWITTER_TOKEN_SECRET);
        twitter.setOAuthAccessToken(accesstoken);
        writer.write("\n");
        for (int i = 0; i < tweet_ids.size(); i++) {
            Long tweet_id = tweet_ids.get(i);
            System.out.println("["+i+"] Getting Tweet id "+tweet_id);
            Status status = twitter.showStatus((long) tweet_ids.get(i));
            if(status == null){
                System.out.println("[FAILED] Doesnot exist twitter with tweet id : " +tweet_id);
            } else {
                System.out.println("status asli : " + status.getText());
                writer.write(tweet_id+"\t"+status.getText().replaceAll("\n", " "));
                writer.write("\n");
                updateIsLabelledAnotator3(tweet_id);
            }
        }
    }
    
    private void updateIsLabelledAnotator3(Long id){
        try {
            //preparedstatement = connection.prepareStatement("update tweet_baru_sanitized set is_labelled_anotator3 = 1 where id = ?");
            preparedstatement = connection.prepareStatement("UPDATE `filtered_tweet_final` SET `isRetrieved`=1 WHERE `twitter_tweet_id`=?");
            preparedstatement.setLong(1, id);
            int result = preparedstatement.executeUpdate();
            if(result == 0){
                System.out.println("Error updating is_labelled_anotator3 for id = "+id);
            }else{
                System.out.println("[SUCCESS] updating is_labelled_anotator3 for id ="+id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void startWriter(String filename) throws IOException{
        file = new File(filename);
        writer = new FileWriter(file,true);
    }
    
    private void closeWriter() throws IOException{
        writer.close();
    }
    
    public static void main(String[] args){
        DumpRetrievedTweet dumper = new DumpRetrievedTweet();
        dumper.StartConnection();
        dumper.selectTokenFromDB();
        try {
            dumper.doGetTweetData();
        } catch (IOException ex) {
            Logger.getLogger(DumpRetrievedTweet.class.getName()).log(Level.SEVERE, null, ex);
        }
        dumper.CloseConnection();
    }
}
