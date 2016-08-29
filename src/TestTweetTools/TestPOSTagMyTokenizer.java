/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestTweetTools;

import cmu.Twokenize;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Asus
 */
public class TestPOSTagMyTokenizer {
    ArrayList<String> tokenized_tweets = new ArrayList<>();
    HashMap<String,String> kateglo_postag = new HashMap<>();
    ArrayList<ArrayList<String>> hasil_tagging = new ArrayList<>();
    
    public void doReadFile(){
        String filename = "gold_standard/Tokenized_gold_standard_repaired";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            StringBuffer sb = new StringBuffer();
            //startwriter("tes_yg_awal");
            boolean firsttime = true;
            Pattern truncated = Pattern.compile(".+\u2026");
            while((line = br.readLine())!=null){
                //String[] splitted = line.split("\\s");
                if(line.equals("")){
                    tokenized_tweets.add(sb.toString()
                            //.replaceAll(Twokenize.url, "")
                            .trim()); // jangan lupa harusnya replace ellipsis dengan url. karena gak peting
                    sb = new StringBuffer();
                }else{
                    Matcher matcher = truncated.matcher(line);
                    //if(!matcher.find()){
                        sb.append(line).append(" "); // kalau gak truncated karena ellipsis berarti abaikan
                    //}
                }
                
            }
            //closeWriter();
        }catch(IOException e){
            e.printStackTrace();
        }
        
        for (int i = 0; i < tokenized_tweets.size(); i++) {
            System.out.println("["+i+"] "+tokenized_tweets.get(i));
        }
    }
    
    public void doReadOldPOSTAG() {
        String filename = "tested/CMUTools/kateglo_postag";
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals("")) {

                } else {
                    String[] splitted = line.split("\t");
                    String token = splitted[0].trim();
                    String tag = splitted[1].trim();

                    // Pisahin dulu.. sesuai dengan tagnya...
                    String real_tag = "";
                    if (tag.equals("Nomina")) {
                        real_tag = "N";
                    } else if (tag.equals("Verba")) {
                        real_tag = "V";
                    } else if (tag.equals("Adjektiva")) {
                        real_tag = "A";
                    } else if (tag.equals("Adverbia")) {
                        real_tag = "R";
                    } else if (tag.equals("Preposisi")) {
                        real_tag = "P";
                    } else if (tag.equals("Interjeksi")) {
                        real_tag = "!";
                    } else if (tag.equals("Konjungsi")) {
                        real_tag = "P"; // kebanyakan kongjungsi adalah Sub coor conjunction
                    } else if (tag.equals("Pronomina")) {
                        real_tag = "O";
                    } else if (tag.equals("Lain-lain")) {
                        real_tag = "G";
                    } else if (tag.equals("Numeralia")) {
                        real_tag = "$";
                    }

                    if (!real_tag.equals("")) {
                        kateglo_postag.put(token, real_tag);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void doTagging(){
        for (int i = 0; i < tokenized_tweets.size(); i++) {
            String sentence = tokenized_tweets.get(i);
            String[] splitted = sentence.split(" ");
            
            ArrayList<String> single_tweet = new ArrayList<String>();
            for (int j = 0; j < splitted.length; j++) {
                
                
                String token = splitted[j];
                String label;
                if(token.matches(Twokenize.url) || token.matches(Twokenize.AtMention) || token.matches(Twokenize.Hashtag)){
                    if(token.matches(Twokenize.url)){
                        label = "U";
                    }else if(token.matches(Twokenize.AtMention)){
                        label = "@";
                    }else if(token.matches(Twokenize.Hashtag)){
                        label = "#";
                    }else{
                        label = "G";
                    }
                }else if(kateglo_postag.containsKey(token.toLowerCase())){
                    label = kateglo_postag.get(token.toLowerCase());
                }else{
                    if (token.matches("\\bke\\w+an\\b|\\bpe\\w+an\\b|\\bpe\\w+\\b|\\b\\w+an\\b|\\bke\\w+\\b|\\b\\w+at\\b|\\b\\w+in\\b")) {
                        label = "N"; 
                    } else if (token.matches("\\bme\\w+\\b|\\bber\\w+\\b|\\b\\w+kan\\b|\\bdi\\w+\\b|\\bter\\w+\\b|\\b\\w+i\\b")) {
                        label = "V";
                    } else if (token.matches("\\byuk\\b|\\bmari\\b|\\bayo\\b|\\beh\\b|\\bhai\\b")) {
                        label = "!";
                    } else {
                        label = "G"; // Paling jelek langsung kasih sebagai G aja..
                    }
                }
                
                String tobewrittern = token + "\t" + label;
                single_tweet.add(tobewrittern);
            }
            hasil_tagging.add(single_tweet);
        }
    }
    
    public void doWriting() throws IOException{
        FileWriter writer = new FileWriter(new File("tested/old_method/POSTagged_old_method"));
        for (int i = 0; i < hasil_tagging.size(); i++) {
            ArrayList<String> get = hasil_tagging.get(i);
            for (int j = 0; j < get.size(); j++) {
                writer.write(get.get(j)+"\n");
                
            }
            writer.write("\n");
        }
        writer.close();
        
    }
    
    public static void main(String[] args){
        TestPOSTagMyTokenizer tester = new TestPOSTagMyTokenizer();
        tester.doReadFile();
        tester.doReadOldPOSTAG();
        tester.doTagging();
        try {
            tester.doWriting();
        } catch (IOException ex) {
            Logger.getLogger(TestPOSTagMyTokenizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
}
