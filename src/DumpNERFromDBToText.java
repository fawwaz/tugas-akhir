
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Asus
 */
public class DumpNERFromDBToText {
    
    private Connection connection = null;
    private Statement statement = null;
    private PreparedStatement preparedstatement = null;
    private ResultSet resultset = null;
    
    private FileWriter filewriter;
    private PrintWriter writer;
    
    private ArrayList<ArrayList<hasillabelling>> overall = new ArrayList<>();
    
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
    
    public void selectFromDB(){
        try {
            preparedstatement = connection.prepareStatement("select * from tweet_baru_sanitized_tokenized_no_url where twitter_tweet_id in (select t.id from (select * from tweet_baru_sanitized limit 5000,5000) as t where is_labelled_anotator1 = 1)");
            resultset = preparedstatement.executeQuery();
            Integer a = 0;
            boolean firsttime = true;
            Long PreviousTweetId = null;
            ArrayList<hasillabelling> _hasillabelling = new ArrayList<>();
            while (resultset.next()) {
                Long currentTweetId = resultset.getLong("twitter_tweet_id");
                
                String token = resultset.getString("token");
                String label = resultset.getString("label_anotator1");
                if(firsttime){
                    PreviousTweetId = currentTweetId;
                    firsttime       = false;
                }
                
                hasillabelling temp = new hasillabelling();
                temp.token = token;
                temp.label = label;
                
                System.out.println("Current tweet id : "+currentTweetId+ " Prev tweet id : "+PreviousTweetId);
                if(currentTweetId.equals(PreviousTweetId)){
                    _hasillabelling.add(temp);
                }else{
                    overall.add(_hasillabelling);
                    _hasillabelling = new ArrayList<>();
                    _hasillabelling.add(temp);
                    // udah gak sama artinya create baru dan ...
                }
                PreviousTweetId = currentTweetId;
                
            }
            System.out.println("[INFO] Successful retrieved");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private class hasillabelling{
        public String token;
        public String label;
    }
    
    public void Print(){
        for (int i = 0; i < overall.size(); i++) {
            for (int j = 0; j < overall.get(i).size(); j++) {
                System.out.println(overall.get(i).get(j).token + "\t"+overall.get(i).get(j).label);
            }
            System.out.println("---");
        }
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
    
    public static void main(String[] args){
        DumpNERFromDBToText dumper = new DumpNERFromDBToText();
        dumper.StartConnection();
        dumper.selectFromDB();
        dumper.Print();
        dumper.CloseConnection();
    }
}
