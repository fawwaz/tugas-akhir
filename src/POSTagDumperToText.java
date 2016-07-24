
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Asus
 */
public class POSTagDumperToText {
    
    private Connection connection = null;
    private Statement statement = null;
    private PreparedStatement preparedstatement = null;
    private ResultSet resultset = null;
    
    private FileWriter filewriter;
    private PrintWriter writer;
    
    ArrayList<tweet_baru_sanitized> tweet_baru_sanitizeds = new ArrayList<>();
    
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
            preparedstatement = connection.prepareStatement("select * from tweet_baru_sanitized_tokenized_no_url where twitter_tweet_id in (select t.id from (select * from tweet_baru_sanitized limit 5000,5000) as t where is_labelled_anotator1 = 1)");
            resultset = preparedstatement.executeQuery();
            Integer a = 0;
            while (resultset.next()) {
                Long twitter_tweet_id = resultset.getLong("twitter_tweet_id");
                String token = resultset.getString("token");
                
                tweet_baru_sanitized tweet_s = new tweet_baru_sanitized();
                tweet_s.token = token;
                tweet_s.twitter_twet_id = twitter_tweet_id;
                
                tweet_baru_sanitizeds.add(tweet_s);
            }
            System.out.println("[INFO] Successful retrieved");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private class tweet_baru_sanitized{
        public String token;
        public Long twitter_twet_id;
    }
    
    private void startWriter(String filename) throws IOException{
        filewriter = new FileWriter(filename);
        writer = new PrintWriter(filewriter);
    }
    
    private void closeWriter() throws IOException{
        writer.flush();
        writer.close();
        filewriter.close();
    }
    
    private void writeTokenToFile() throws IOException{
        startWriter("old_twokenize");
        for (int i = 0; i < tweet_baru_sanitizeds.size(); i++) {
            if(i>0){ // ignore first tweet
                if(!tweet_baru_sanitizeds.get(i).twitter_twet_id.equals(tweet_baru_sanitizeds.get(i-1).twitter_twet_id)){
                    writer.write("\n");
                }
            }
            writer.write(tweet_baru_sanitizeds.get(i).token);
//            writer.write("\tTAG\n");
//            writer.write(" ");
            writer.write("\n");
        }
        closeWriter();
    }
    
    public static void main(String[] args) {
        POSTagDumperToText postagdumper = new POSTagDumperToText();
        postagdumper.StartConnection();
        postagdumper.selectTokenFromDB();
        try {
            postagdumper.writeTokenToFile();
        } catch (IOException ex) {
            Logger.getLogger(POSTagDumperToText.class.getName()).log(Level.SEVERE, null, ex);
        }
        postagdumper.CloseConnection();
        
    }
}
