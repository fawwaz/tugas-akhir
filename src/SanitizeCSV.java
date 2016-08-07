import old_method.Twokenize;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
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
public class SanitizeCSV {
    Connection connection;
    ResultSet resultset;
    Statement statement;
    PreparedStatement preparedstatement;

    ArrayList<String> texts;
    ArrayList<String> ids;
    ArrayList<String> bool_value;

    public SanitizeCSV() {
        texts = new ArrayList<>();
        ids = new ArrayList<>();
        bool_value = new ArrayList<>();
    }

    public void parseCSV() {
        Twokenize twokenizer = new Twokenize();
        try {
            CSVReader reader = new CSVReader(new FileReader("hasil bersih-bersih duplikat.csv"), ';');
            String[] nextline;
            boolean firsttime = true;
            while ((nextline = reader.readNext()) != null) {
                //ids.add(Long.valueOf(nextline[0]));
                if (firsttime) {
                    firsttime = false;
                } else {
                    // sekip sekali di header doang
                    ids.add(nextline[0]);
                    texts.add(nextline[1].replaceAll(Twokenize.url, "").replace("&amp;", "&"));
                    bool_value.add(nextline[2]);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SanitizeCSV.class.getName()).log(Level.SEVERE, "Tidak ditemukan filenya", ex);
        } catch (IOException ex) {
            Logger.getLogger(SanitizeCSV.class.getName()).log(Level.SEVERE, "Ada format csv yang salah .. atau io exception", ex);
        }

    }

    public void writeCSV() {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter("hasil bersih-bersih duplikat-sanitized.csv"));
            for (int i = 0; i < texts.size(); i++) {
                String[] entry = new String[3];
                entry[0] = ids.get(i);
                entry[1] = texts.get(i);
                entry[2] = bool_value.get(i);

                writer.writeNext(entry);
            }
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(SanitizeCSV.class.getName()).log(Level.SEVERE, "tidak bisa writing", ex);
        }
    }

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
    
    public void doInsertion(){
        for (int i = 0; i < texts.size(); i++) {
            InsertTokenToTweetBaruSanitized(Long.valueOf(ids.get(i)), texts.get(i), Integer.valueOf(bool_value.get(i)));
        }
    }
    
    private void InsertTokenToTweetBaruSanitized(Long id,String text, int is_processed) {
        try {
            preparedstatement = connection.prepareStatement("INSERT into tweet_baru_sanitized (id,text,is_labelled) VALUES (?,?,?)");
            preparedstatement.setLong(1, id);
            preparedstatement.setString(2, text);
            preparedstatement.setInt(3, is_processed);
            preparedstatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SanitizeCSV sanitizer = new SanitizeCSV();
        sanitizer.parseCSV();
        sanitizer.startConnection();
        sanitizer.doInsertion();
        sanitizer.CloseConnection();
        //sanitizer.writeCSV();
    }
}
