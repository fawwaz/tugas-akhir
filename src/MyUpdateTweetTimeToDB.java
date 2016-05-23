import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import twitter4j.Twitter;
import java.util.Map;
import twitter4j.Status;
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
public class MyUpdateTweetTimeToDB {

    private Connection connection = null;
    private Statement statement = null;
    private PreparedStatement preparedstatement = null;
    private ResultSet resultset = null;

    HashMap<Long, Long> tweet_id_to_time;

    public MyUpdateTweetTimeToDB() {
        tweet_id_to_time = new HashMap<>();
    }

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

    public void selectTweetIdFromDB() {
        try {
            preparedstatement = connection.prepareStatement("SELECT distinct twitter_tweet_id from anotasi_tweet_final where time is NULL;");
            resultset = preparedstatement.executeQuery();
            Integer a = 0;
            while (resultset.next()) {
                Long twitter_tweet_id = resultset.getLong("twitter_tweet_id");
                tweet_id_to_time.put(twitter_tweet_id, 0L);
            }
            System.out.println("[INFO] Successful inserted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void GetTweetTime() {
        Twitter twitter = new TwitterFactory().getInstance();
        String CONSUMER_KEY = "4enxNevEGWMqBuKzcJoQ";
        String CONSUMER_SECRET = "UUDCNSvkFoZRaRUV4b8eHBcgR2N1LSrHXX7IELxdIk";
        String TWITTER_TOKEN = "470084145-qwjAlIxq7GFB1I62TO5ebyoO10tXgUuZJfgTLu7G";
        String TWITTER_TOKEN_SECRET = "9y47ww1xk0Nc5DhL9ZW0HOC6Qoig2wP101tOjsERnqNNv";
        twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        AccessToken accesstoken = new AccessToken(TWITTER_TOKEN, TWITTER_TOKEN_SECRET);
        twitter.setOAuthAccessToken(accesstoken);

        Iterator iterator = tweet_id_to_time.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            try {
                Status status = twitter.showStatus((long) pair.getKey());
                if (status == null) {
                    System.out.println("Doesnot exist twitter with tweet id : " + pair.getKey());
                } else {
                    tweet_id_to_time.put((Long) pair.getKey(), status.getCreatedAt().getTime());
                }
            } catch (TwitterException e) {
                tweet_id_to_time.put((Long) pair.getKey(), 1L);
                e.printStackTrace();
            }
            System.out.println("Id : " + pair.getKey() + "value " + pair.getValue());
        }
    }

    public void updateTweetTime() {
        Iterator iterator = tweet_id_to_time.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            Long value = (Long) pair.getValue();
            if(value.equals(0L) || value.equals(1L)){ // kalau hasilnya bukan 0 , lakukan update
                System.out.println("Not Yet Found or has a problem");
            }else{
                doUpdateDatabase((Long) pair.getKey(), (Long) pair.getValue());
            }
            System.out.println("Id : " + pair.getKey() + "value " + pair.getValue());
        }
    }

    private void doUpdateDatabase(Long tweet_id, Long time) {
        try {
            preparedstatement = connection.prepareStatement("UPDATE anotasi_tweet_final SET time=? WHERE twitter_tweet_id=?");
            preparedstatement.setLong(1, time);
            preparedstatement.setLong(2, tweet_id);
            preparedstatement.executeUpdate();
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
        MyUpdateTweetTimeToDB updater = new MyUpdateTweetTimeToDB();
        updater.StartConnection();
        updater.selectTweetIdFromDB();
        updater.GetTweetTime();
        updater.updateTweetTime();
        updater.CloseConnection();
    }
}
