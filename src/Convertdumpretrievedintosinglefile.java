
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Asus
 */
public class Convertdumpretrievedintosinglefile {
    public static void main(String[] args) throws IOException{
        ArrayList<String> queue = new ArrayList<>();
        // Read file
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
        
        // Write to single file ..
        FileWriter writer = new FileWriter(new File("Tokenized_gold_standard"));
        for (int i = 0; i < queue.size(); i++) {
            String[] tokenizeds = queue.get(i).split("\\s+");
            for (int j = 0; j < tokenizeds.length; j++) {
                writer.write(tokenizeds[j]+"\n");
            }
            writer.write("\n");
        }
        writer.close();
    }
}
