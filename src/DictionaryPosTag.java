
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
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
public class DictionaryPosTag {
    
    HashMap<String,String> dictionary = new HashMap<String, String>();
    ArrayList<String> tokenized_tweets = new ArrayList<>();
    File file;
    FileWriter writer;
    
    private Connection connection = null;
    private Statement statement = null;
    private PreparedStatement preparedstatement = null;
    private ResultSet resultset = null;
    
    
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
    
//    public static void main(String[] args) {
//    }
    
    /*
        Main data start here
    */
    public void selectDictionary(){
        try {
            preparedstatement = connection.prepareStatement("select * from tb_katadasar");
            resultset = preparedstatement.executeQuery();
            Integer a = 0;
            while (resultset.next()) {
                String katadasar = resultset.getString("katadasar");
                String postag = resultset.getString("tipe_katadasar");
                dictionary.put(katadasar, postag);
            }
            System.out.println("[INFO] Successful inserted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void doReadFile(){
        String filename = "postag_dump_clean";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine())!=null){
                tokenized_tweets.add(line);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void startWriter() throws IOException{
        file = new File("hasil_postag_dictionary");
        writer = new FileWriter(file);
    }
    
    public void closeWriter() throws IOException{
        writer.close();
    }
    
    public void doPosTagging() throws IOException{
        startWriter();
        for (int i = 0; i < tokenized_tweets.size(); i++) {
            String sentence = tokenized_tweets.get(i);
            System.out.println("Tweet Ke - "+i+" "+sentence);
            String[] words = sentence.trim().split("\\s+");
            
            for (int j = 0; j < words.length; j++) {
                String current_word = words[j].replaceAll("\\/\\/", "__DOUBLE_SLASH__");
                String postag;
                
                if(current_word.startsWith("@")){
                    postag = "MENTION";
                }else if(current_word.startsWith("#")){
                    postag = "HASHTAG";
                }else{
                    if(dictionary.get(words[j])!=null){
                        postag = dictionary.get(words[j]);
                    }else{
                        postag = "FW";
                    }
                }
                System.out.println(current_word + "\t" + postag);
                writer.write(current_word + "\t" + postag+"\n");
            }
//            System.out.println("");
            writer.write("\n");
        }
        
        closeWriter();
    }
    
    
    public static void main(String[] args) {
        DictionaryPosTag postager = new DictionaryPosTag();
        postager.StartConnection();
        postager.selectDictionary();
        postager.CloseConnection();
        postager.doReadFile();
        try {
            postager.doPosTagging();
        } catch (IOException ex) {
            Logger.getLogger(DictionaryPosTag.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
