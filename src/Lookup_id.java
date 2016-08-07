
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Asus
 */
public class Lookup_id {
    private Connection connection = null;
    private Statement statement = null;
    private PreparedStatement preparedstatement = null;
    private ResultSet resultset = null;
    
    private File file;
    private FileWriter writer;
    
    ArrayList<pair> queue = new ArrayList<>();
    
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
    
    public void Lookup(String text, Integer urutan) {
        try {
            
            text = text.replace("!", "!!")
                        .replace("%", "!%")
                        .replace("_", "!_")
                        .replace("[", "![");
            /**/
            //System.out.println("Text : "+text);
            preparedstatement = connection.prepareStatement("select * from (select * from (select * from tweet_baru_sanitized limit 5000,5000) as t2 where is_labelled_anotator1=1) as t where text like ? escape '!'");
            //preparedstatement.setString(1, "%\""+text+"%\"");
            //preparedstatement.setString(1, text.replaceAll("%", "")+"%");
            preparedstatement.setString(1, text+"%");
            resultset = preparedstatement.executeQuery();
            Integer a = 0;
            /*
            boolean isMoreThanOne = resultset.first() && resultset.next();
            boolean isempty = ! resultset.first();
            if(!isMoreThanOne && !isempty){
                queue.get(urutan).tweet_id = resultset.getLong("id");
            }else if(isempty){
                System.out.println("[ERROR KOSONG UNTUK ... ]");
                System.out.println("Text : "+text);
            }else{
                System.out.println("[ERROR LEBH DARI 1 loh]");
                System.exit(-1);
            }
            */
            while(resultset.next()){
                System.out.println("Text : "+text);
                System.out.println(resultset.getLong("id"));
                queue.get(urutan).tweet_id = resultset.getLong("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void doReadFile(){
        String filename = "gold_standard/DumpRetrievedTweetAll";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine())!=null){
                pair p = new pair();
                p.text = line;
                p.tweet_id = 0L;
                queue.add(p);
            }
            //closeWriter();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
   
    
    public void startwriter(String filename) throws IOException{
         file = new File(filename);
         writer = new FileWriter(file);
    }
    
    public void closeWriter() throws IOException{
        writer.close();
    }
    
    public static void main(String[] args){
        Lookup_id finder = new Lookup_id();
        finder.StartConnection();
        finder.doReadFile();
        finder.doLookUp();
        try{
            finder.doWriteLookupResult();
        }catch(Exception e){
            e.printStackTrace();
        }
        finder.CloseConnection();
    }
    
    public void doWriteLookupResult() throws IOException{
        startwriter("gold_standard/TweetAndDumped");
        for (int i = 0; i < queue.size(); i++) {
            writer.write(queue.get(i).tweet_id + "\t"+ queue.get(i).text+"\n");
        }
        closeWriter();
    }
    
    public void doLookUp(){
        for (int i = 0; i < queue.size(); i++) {
            String text = queue.get(i).text;
            if(!text.equals("")){
                Lookup(text.substring(0,text.length()-2), i);
            }
        }
    }
    
    private class pair{
        public String text;
        public Long tweet_id;
    }
    
}
