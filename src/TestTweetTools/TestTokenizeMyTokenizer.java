package TestTweetTools;

import IndonesianNLP.IndonesianPOSTagger;
import IndonesianNLP.IndonesianSentenceFormalization;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import old_method.Twokenize;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Asus
 */
public class TestTokenizeMyTokenizer {
    ArrayList<String> tweets = new ArrayList<>();
    ArrayList<String> english_dict = new ArrayList();
    IndonesianPOSTagger postagger = new IndonesianPOSTagger();
    
    File file;
    FileWriter writer;
    public void doReadFile(){
        String filename = "gold_standard/DumpRetrievedTweetAll";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            StringBuffer sb = new StringBuffer();
            Pattern truncated = Pattern.compile(".+\u2026");
            while((line = br.readLine())!=null){
                tweets.add(line);
            }
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
    
    // 1. Formalized
    // 2. Plain
    // 3. Plain with dictionary
    public void doTokenization() throws IOException{
        
        int urutan = 0;
        startwriter("tested/old_method/tokenizer");
        for (int i = 0; i < tweets.size(); i++) {
            String sentence = tweets.get(i);
            
            
            List<String> tokenizeds = Twokenize.tokenizeRawTweetText(sentence);
            
            for (int j = 0; j < tokenizeds.size(); j++) {
                writer.write(tokenizeds.get(j)+"\n");
            }
            writer.write("\n");
        }
        closeWriter();
    }
    
    public static void main(String[] args) throws Exception {
        TestTokenizeMyTokenizer tester = new TestTokenizeMyTokenizer();
        tester.doReadFile();
        
        
        try{
            tester.doTokenization();
        }catch(IOException ex){
                Logger.getLogger(TestPosTagInaNlp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
