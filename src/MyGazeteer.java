import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class MyGazeteer {
	
	private Connection connection = null;
	private Statement statement = null;
	private PreparedStatement preparedstatement = null;
	private ResultSet resultset = null;
	
	public void readAndInsertGazeteer(){
		startConnection();
		BufferedReader br = null;
		try{
			String sCurrentLine;
			 
			br = new BufferedReader(new FileReader("gazeteer2"));
 
			while ((sCurrentLine = br.readLine()) != null) {
				//System.out.println(sCurrentLine);
				insertIntoDB(sCurrentLine);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			CloseConnection();
		}
	}
	
	public boolean isGazetteer(String gazetteer){
		try{
			preparedstatement = connection.prepareStatement("select location from gazetteer where location = ?");
			preparedstatement.setString(1, gazetteer);
			resultset = preparedstatement.executeQuery();
			if(resultset.next()){
				return true;
			}else{
				return false;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return false;
	}
	
	
	public void insertIntoDB(String Gazeteer){
		try{
			preparedstatement = connection.prepareStatement("Insert into gazetteer (location) values(?)");
			preparedstatement.setString(1, Gazeteer);
			preparedstatement.executeUpdate();
			System.out.println("[Inserted] inserted to gazetteer"+Gazeteer);
		}catch(SQLException e){
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
