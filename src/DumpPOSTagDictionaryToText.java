
import java.io.File;
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
import java.util.HashMap;
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
public class DumpPOSTagDictionaryToText {
    
    private Connection connection = null;
    private Statement statement = null;
    private PreparedStatement preparedstatement = null;
    private ResultSet resultset = null;
    
    private FileWriter filewriter;
    private PrintWriter writer;
    
    ArrayList<String> kata = new ArrayList<>();
    ArrayList<String> tag = new ArrayList<>();
    
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
            preparedstatement = connection.prepareStatement("select * from tb_katadasar");
            //preparedstatement = connection.prepareStatement("select * from gazetteer");
            resultset = preparedstatement.executeQuery();
            while (resultset.next()) {
                
                String _kata = resultset.getString("katadasar");
                String _tipe_katadasar = resultset.getString("tipe_katadasar");
                //String _kata = resultset.getString("location");
                kata.add(_kata);
                tag.add(_tipe_katadasar);
            }
            System.out.println("[INFO] Successful retrieved");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void writetoFile() throws IOException{
        FileWriter writer2 = new FileWriter(new File("tested/CMUTools/old_location_gazetteer"));
        for (int i = 0; i < kata.size(); i++) {
            String _kata = kata.get(i);
            String _tag = tag.get(i);
            writer2.write(_kata+"\t"+_tag+"\n");
            //writer2.write(_kata+"\t"+_tag"\n");
        }
        writer2.close();
    }
    
    public static void main(String[] args){
        DumpPOSTagDictionaryToText dumper = new DumpPOSTagDictionaryToText();
        dumper.StartConnection();
        dumper.selectFromDB();
        dumper.CloseConnection();
        try {
            dumper.writetoFile();
        } catch (IOException ex) {
            Logger.getLogger(DumpPOSTagDictionaryToText.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
