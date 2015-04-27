import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.FeatureSequence2FeatureVector;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.PrintInputAndTarget;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.iterator.ArrayDataAndTargetIterator;
import cc.mallet.pipe.tsf.RegexMatches;
import cc.mallet.types.InstanceList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class MyTokenizer {
	public static ArrayList<String> result = new ArrayList<>();


	public static void main(String[] args) {
		//MyTokenizer.readFile();
		
		// Perbaikan token FINAL dulu
		/*
		MyRepair mr = new MyRepair();
		mr.startConnection();
		mr.CrawlTweet();
		mr.CloseConnection();
		*/
		
		// MASUKAN TOKEN FINAL KE FILTERED FINAL
		/*
		MyFilter mf = new MyFilter();
		mf.startConnection();
		//mf.insertFilterTweet();
		//mf.updateFilteredTweet();
		//System.out.println("Evaluator di disabled");
		System.out.println("Evaluating");
		mf.evaluate();
		mf.CloseConnection();
		*/
		
		// ---
		

		// Insert For anotation 
		/*
		MyAnotatorDB madb = new MyAnotatorDB();
		madb.startConnection();
		madb.insertTokentoWrongTweet();
		madb.CloseConnection();
		*/
		
		// Test filter		
		/*
		MyFilter mf = new MyFilter();
		mf.startConnection();
		mf.updateFilteredTweet();
		mf.CloseConnection();
		*/
		
		// Melakukan tokenisasi
		ArrayList<String> hasil = doTokenization();
		//WriteFile(hasil, "dumptokenisasifinal");
		//System.out.println("Done Writing tokenization result" + hasil.size());
		
		
		// Melakukan anotasi ke database
		MyAnotatorDB madb = new MyAnotatorDB();
		madb.startConnection();
		madb.insertTokentoAnotasiDB();
		System.out.println("Inserted");
		madb.CloseConnection();
		
		
		// modifikasi simpletagger agar bisa melakukan anotasi di anotasi final (Set source as twitter_tweet_id, masukan ke database)
		// Run simpletagger, 
		// Perbaiki hasil anotasi dari simpletagger
		// lakukan cross validation 10 times jika belum ada tulis kode secara manual
		/*
		hasil.add(Twokenize.embeddedApostrophe.toString());
		hasil.add(Twokenize.EdgePunctLeft.toString());
		hasil.add(Twokenize.EdgePunctRight.toString());
		//WriteFile(hasil, "hasil_tokenisasi_paling_akhir");
		*/
		
		// Mecoba anotasi ke teks
		/*
		MyAnotatorText ma = new MyAnotatorText(doTokenization());
		WriteFile(ma.getExtractedFeature(), "hasil_tokenisasi_paling_akhir_anotasi");
		*/
		
		
		
		
		
		/*
		MyMallet mm = new MyMallet();
		mm.domallet(doTokenization());
		System.out.println("ingat ini masih lagi tahapan coba coba ");
		*/
		
	}

	public static void readFile(){
		BufferedReader br = null;
		try{
			String currentline;
			br = new BufferedReader(new FileReader("tweet-1.csv"));
			while((currentline = br.readLine()) != null){
				result.addAll(Twokenize.tokenizeRawTweetText(currentline));				
			}
			printres();

		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				if(br!=null) br.close();
			}catch (IOException ex){
				ex.printStackTrace();
			}
		}
	}


	public static void printres(){
		System.out.println("==== HASIL ====");
		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i));
		}
	}

	/**
	 * 1. Connect ke twitter
	 * 2. Lookup id twitter save ke database tweet textnya localhost aja
	 * 2. bikin query show id twitter 
	 * 3. 
	 */ 

	public static void WriteFile(ArrayList<String> input, String filename){
		BufferedWriter writer = null;
		try{
			writer = new BufferedWriter(new FileWriter(filename));
			
			for (int i = 0; i < input.size(); i++) {
				writer.write(input.get(i)+"\n");
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				if(writer!=null){
					writer.close();
				}
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
	
	public static ArrayList<String> doTokenization(){
		MyDBConnector dbcon = new MyDBConnector();
		dbcon.startConnection();
		
		// get arraylist
		//ArrayList<String> tweets = dbcon.getTweetFromLocalDB();
		ArrayList<String> tweets = dbcon.getTweetFromLocalDBWhereLabelTrue();
		ArrayList<String> tokenized = new ArrayList<>();
		dbcon.CloseConnection();
		
		
		Twokenize tokenizer = new Twokenize();
		for (int i = 0; i < tweets.size(); i++) {
			tokenized.addAll(tokenizer.tokenizeRawTweetText(tweets.get(i)));
			//tokenized.add("\n---\n");
		}
		
		return tokenized;
	}
	
	public void examplemallet(){
		/*
		System.out.println("DO NOTHING");
		
		ArrayList<String> trainingdata = new ArrayList<>();
		ArrayList<String> label	= new ArrayList<>();
		
		trainingdata.add("konser");
		trainingdata.add("januari");
		trainingdata.add("12");
		trainingdata.add("/");
		trainingdata.add("trainig5");
		
		label.add("label1");
		label.add("label2");
		label.add("label1");
		label.add("label3");
		label.add("label2");
		// harus sequence
		
		ArrayList<Pipe> pipelist = new ArrayList<>();
		pipelist.add(new CharSequence2TokenSequence());
		pipelist.add(new RegexMatches("ISNUMBER", Pattern.compile("\\d+")));
		pipelist.add(new RegexMatches("WORD", Pattern.compile("\\d+")));
		pipelist.add(new RegexMatches("PUNCTUATION", Pattern.compile("[/-]")));
		pipelist.add(new RegexMatches("ISMONTH", Pattern.compile("(?>march|mar|maret|januari|februari)")));
		pipelist.add(new PrintInputAndTarget());
		Pipe processpipe = new SerialPipes(pipelist);
		
		InstanceList instancelist = new InstanceList(processpipe);
		instancelist.addThruPipe(new ArrayDataAndTargetIterator(trainingdata, label));
		*/
	}

}
