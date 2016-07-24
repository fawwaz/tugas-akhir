
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import IndonesianNLP.IndonesianPOSTagger;
import IndonesianNLP.IndonesianSentenceFormalization;
import java.io.File;
import java.io.FileWriter;
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
public class InaNLPPosTag {
    
    ArrayList<String> tokenized_tweets = new ArrayList<>();
    ArrayList<String> english_dict = new ArrayList();
    
    File file;
    FileWriter writer;
    public void doReadFile(){
        String filename = "postag_dump_clean";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine())!=null){
                tokenized_tweets.add(line);
            }
        }catch(IOException e){
            e.printStackTrace();
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
        //startwriter("hasil_postag_InaNLP_english_dictionary");
        IndonesianSentenceFormalization formalizer = new IndonesianSentenceFormalization();
        int urutan = 0;
        for (int i = 0; i < tokenized_tweets.size(); i++) {
            if((i%30)==0){
                urutan++;
                startwriter("hasil_postag_InaNLP_gold_standard_"+urutan);
            }
            String sentence = tokenized_tweets.get(i).replaceAll("\\/\\/", "__DOUBLE_SLASH__");
            //sentence = formalizer.formalizeSentence(sentence);
            
            System.out.println("Tweet ke : "+i + " "+sentence);
            ArrayList<String[]> postagged = IndonesianPOSTagger.doPOSTag(sentence);
            
            for (int j = 0; j < postagged.size(); j++) {
                System.out.println("["+j+"] "+postagged.get(j)[0] + "\t" + postagged.get(j)[1]+"\n");
//                System.out.println(postagged.get(j)[0] + "\t\t\t" + postagged.get(j)[1]);
                if(postagged.get(j)[0].startsWith("@")){
                    writer.write(postagged.get(j)[0] + "\t" + "MENTION"+"\n");
                }else if(postagged.get(j)[0].startsWith("#")){
                    writer.write(postagged.get(j)[0] + "\t" + "HASHTAG"+"\n");
                }else if(english_dict.contains(postagged.get(j)[0])){
                    writer.write(postagged.get(j)[0] + "\t" + "FW"+"\n");
                }else{
                    writer.write(postagged.get(j)[0] + "\t" + postagged.get(j)[1]+"\n");
                }
            }
            writer.write("\n---\n");
            
            if(((i+1)%30)==0){
                closeWriter();
            }
        }
    }
    
    public static void main(String[] args) {
        InaNLPPosTag postager = new InaNLPPosTag();
        postager.doReadFile();
        postager.doReadEnglishDictionary();
        try {
            postager.doPosTagging();
        } catch (IOException ex) {
            Logger.getLogger(InaNLPPosTag.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
