/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestTweetTools;


import IndonesianNLP.IndonesianPOSTagger;
import IndonesianNLP.IndonesianSentenceFormalization;
import cmu.Twokenize;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Asus
 */
public class TestPosTagInaNlp {
    ArrayList<String> tokenized_tweets = new ArrayList<>();
    ArrayList<String> english_dict = new ArrayList();
    IndonesianPOSTagger postagger = new IndonesianPOSTagger();
    
    File file;
    FileWriter writer;
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
    
    public void doReadEnglishDictionary(){
        String filename = "google-10000-english.txt";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine())!=null){
                english_dict.add(line);
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
    public void doPosTagging() throws IOException{
        IndonesianSentenceFormalization formalizer = new IndonesianSentenceFormalization();
        int urutan = 0;
        for (int i = 0; i < tokenized_tweets.size(); i++) {
            if((i%30)==0){
                urutan++;
                startwriter("gold_standard/pos_tag_url/hasil_postag_InaNLP_gold_standard"+urutan);
            }
            String sentence = tokenized_tweets.get(i).replaceAll("\\/\\/", "__DOUBLE_SLASH__");
            //sentence = formalizer.formalizeSentence(sentence);
            
//            System.out.println("Tweet ke : "+i + " "+sentence);
            ArrayList<String[]> postagged = postagger.doPOSTag(sentence);
            
            for (int j = 0; j < postagged.size(); j++) {
//                System.out.ppos_tagrintln("["+j+"] "+postagged.get(j)[0] + "\t" + postagged.get(j)[1]+"\n");
//                System.out.println(postagged.get(j)[0] + "\t\t\t" + postagged.get(j)[1]);
                if(postagged.get(j)[0].equals("@")){
                    writer.write(postagged.get(j)[0] + "\t" + "IN"+"\n");
                }else if(postagged.get(j)[0].startsWith("@")){
                    writer.write(postagged.get(j)[0] + "\t" + "MENTION"+"\n");
                }else if(postagged.get(j)[0].startsWith("#")){
                    writer.write(postagged.get(j)[0] + "\t" + "HASHTAG"+"\n");
                }
                /*else if(english_dict.contains(postagged.get(j)[0])){
                    writer.write(postagged.get(j)[0] + "\t" + "FW"+"\n");
                }*/else{
                    writer.write(postagged.get(j)[0] + "\t" + postagged.get(j)[1]+"\n");
                }
            }
            writer.write("\n---\n");
            
            if(((i+1)%30)==0){
                closeWriter();
            }
            if(i==tokenized_tweets.size()-1){
                closeWriter();
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        TestPosTagInaNlp tester = new TestPosTagInaNlp();
        tester.doReadFile();
        //tester.doReadEnglishDictionary();
        
        try{
            tester.doPosTagging();
        }catch(IOException ex){
                Logger.getLogger(TestPosTagInaNlp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
