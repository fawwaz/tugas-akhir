
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
public class MyCountProbablityOfToken {
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
    
    public static void main(String[] args) {
        String format = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date;
        try {
            date = sdf.parse("24/04/2015");
            System.out.println(date.getTime());
        } catch (ParseException ex) {
            Logger.getLogger(MyCountProbablityOfToken.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            MyCountProbablityOfToken.iterateoverDate("10/04/2015", "22/04/2015");
        } catch (ParseException ex) {
            Logger.getLogger(MyCountProbablityOfToken.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void iterateoverDate(String start, String end) throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date startdate = sdf.parse(start);
        Date enddate = sdf.parse(end);
        
        Calendar startcal = Calendar.getInstance();
        startcal.setTime(startdate);
        Calendar endcal = Calendar.getInstance();
        endcal.setTime(enddate);
        
        for(Date date = startcal.getTime(); startcal.before(endcal); startcal.add(Calendar.DATE, 1), date = startcal.getTime()){
            System.out.print("Tanggal :"+ date);
            System.out.println(" UTC format : "+date.getTime());
            // Jangna lupa date time nya ditambah 1
        }
    }
    
    private class temporary_token{
        HashMap<String, Integer> tokens_to_counter;
        Integer total_counter;
        
        public temporary_token(){
            tokens_to_counter = new HashMap<>();
            total_counter = 0;
        }
    }
}
