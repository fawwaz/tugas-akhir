
import cmu.Twokenize;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
public class CleanNER {
    ArrayList<String> token = new ArrayList<String>();
    
    public void doReadGoldStandard(){
        // READ Previous name gazetteer
        String filename = "tested/CMUTools/NER_gold_standard";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine())!=null){
                if(line.equals("")){
                    token.add(line);
                }else{
                    String[] splitted = line.split("\\s");
                    token.add(splitted[0]);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void writeFile() throws IOException{
        FileWriter writer = new FileWriter(new File("tested/CMUTools/NER_gold_standard_arief"));
        for (int i = 0; i < token.size(); i++) {
            String get = token.get(i);
            if(get.equals("")){
                writer.write("\n");
            }else{
                if(get.matches(Twokenize.url)){
                    writer.write(get+"\tO\n");
                }else{
                    writer.write(get+"\t\n");
                }
            }
        }
        writer.close();
    }
    
    public static void main(String[] args){
        CleanNER cleaner = new CleanNER();
        cleaner.doReadGoldStandard();
        try {
            cleaner.writeFile();
        } catch (IOException ex) {
            Logger.getLogger(CleanNER.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
