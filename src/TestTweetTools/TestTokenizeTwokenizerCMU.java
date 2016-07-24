/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestTweetTools;

import IndonesianNLP.IndonesianSentenceTokenizer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import cmu.Twokenize;
import static cmu.Twokenize.tokenizeRawTweetText;
import java.util.List;
/**
 *
 * @author Asus
 */
public class TestTokenizeTwokenizerCMU {
    ArrayList<String> queue = new ArrayList<>();
    File file;
    FileWriter writer;
    public void readFile(){
        BufferedReader br = null;
        for (int i = 1; i <= 11; i++) {
            try{
                String currentline;
                br = new BufferedReader(new FileReader("dumped_text/DumpRetrievedTweet_"+i));
                while((currentline = br.readLine())!=null){
                    queue.add(currentline);
                }
            }catch(IOException e){
                e.printStackTrace();
            }finally{
                try{
                    if(br!=null) br.close();
                }catch(IOException ioe){
                    ioe.printStackTrace();
                }
            }
        }
    }
    
    private void startWriter(String filename) throws IOException{
        writer = new FileWriter(new File(filename));
    }
    
    private void closeWriter() throws IOException{
        writer.close();
    }
    
    
    public void doTokenizing() throws IOException{
        startWriter("tested/CMUTools/tokenizer");
        Twokenize twokenizer = new Twokenize();
        for (int i = 0; i < queue.size(); i++) {
            String sentence = queue.get(i);
            List<String> tokens = twokenizer.tokenizeRawTweetText(sentence);
            
            for (int j = 0; j < tokens.size(); j++) {
                writer.write(tokens.get(j));
                writer.write("\n");
            }
            
            writer.write("\n");
        }
        closeWriter();
    }
    
    public static void main(String[] args){
        TestTokenizeTwokenizerCMU tester = new TestTokenizeTwokenizerCMU();
        tester.readFile();
        try {
            tester.doTokenizing();
        } catch (IOException ex) {
            Logger.getLogger(TestTokenizerInaNlp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
