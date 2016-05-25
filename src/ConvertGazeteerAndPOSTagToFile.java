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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
public class ConvertGazeteerAndPOSTagToFile {

    private Connection connection = null;
    private Statement statement = null;
    private PreparedStatement preparedstatement = null;
    private ResultSet resultset = null;
    HashMap<String, String> word_to_postag;
    ArrayList<String> gazeteer;

    public void startConnection() {
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

    public ConvertGazeteerAndPOSTagToFile() {
        word_to_postag = new HashMap<>();
        gazeteer = new ArrayList<>();
        startConnection();
        retrieveGazeteerFromDB();
        retrievePOSTagFromDB();
        writeGazeteer();
        writePOSTag();
        CloseConnection();
    }
    
    private void retrievePOSTagFromDB(){
        try {
            preparedstatement = connection.prepareStatement("select * from tb_katadasar;");
            resultset = preparedstatement.executeQuery();
            while (resultset.next()) {
                String kata = resultset.getString("katadasar");
                String postag = resultset.getString("tipe_katadasar");
                word_to_postag.put(kata,postag);
            }
            System.out.println("[INFO] POSTAG Retrieved");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void retrieveGazeteerFromDB(){
        try {
            preparedstatement = connection.prepareStatement("select distinct location from gazetteer;");
            resultset = preparedstatement.executeQuery();
            while (resultset.next()) {
                gazeteer.add(resultset.getString("location"));
            }
            System.out.println("[INFO] POSTAG Retrieved");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void writeGazeteer() {
        File file = new File("gazeteer");
        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            
            for (int i = 0; i < gazeteer.size(); i++) {
                writer.write(gazeteer.get(i)+"\n");
            }
            
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(ConvertGazeteerAndPOSTagToFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private void writePOSTag() {
        File file = new File("dictionary_postag");
        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            
            Iterator iterator = word_to_postag.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry pair = (Map.Entry) iterator.next();
                writer.write(pair.getKey() + ":" + pair.getValue() + "\n");
            }
            
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(ConvertGazeteerAndPOSTagToFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
        ConvertGazeteerAndPOSTagToFile converter = new ConvertGazeteerAndPOSTagToFile();
    }


    
}
