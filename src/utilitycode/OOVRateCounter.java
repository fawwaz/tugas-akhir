/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilitycode;

import cmu.Twokenize;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Asus
 */
public class OOVRateCounter {
    
    HashSet<String> training_datas = new HashSet<>();
    HashSet<String> testing_datas = new HashSet<>();
    HashSet<String> oov_words = new HashSet<>();
    int oov_rekap[] = new int[4];
    int train_rekap[] = new int[4];
    int test_rekap[] = new int[4];
    int num_folds = 4;
    
    public void doReadTraining(int iteration){
        System.out.println("Reading Training data for iteration "+iteration);
        String filename = "tested/CMUTools/training_merged_"+iteration+".training";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine())!=null){
                if(line.equals("")){
                    
                }else{
                    String[] splitted = line.split(" ");
                    if(isWord(splitted[0])){
                        //training_datas.add(splitted[0]);
                        training_datas.add(splitted[0].toLowerCase());
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void doReadTesting(int iteration){
        System.out.println("Reading Training data for iteration "+iteration);
        String filename = "tested/CMUTools/testing_merged_"+iteration+".untagged";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine())!=null){
                if(line.equals("")){
                    
                }else{
                    String[] splitted = line.split(" ");
                    if(isWord(splitted[0])){
                        //testing_datas.add(splitted[0]);
                        testing_datas.add(splitted[0].toLowerCase());
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    private String generateReport(int iteration, int oov_counter, int train_size, int test_size, HashSet<String> oov_words){
        StringBuffer sb = new StringBuffer();
        sb.append("Iteration : "+iteration);
        sb.append("\n----------");
        sb.append("\nOOV : "+oov_words.size());
        sb.append("\nTraining data : "+train_size);
        sb.append("\nTest data :"+test_size);
        sb.append("\nWordList ");
        sb.append("\n=====");
        
        for (Iterator<String> iterator = oov_words.iterator(); iterator.hasNext();) {
            String next = iterator.next();
            sb.append("\n"+next);
        }
        sb.append("\n\n");
        return sb.toString();
    }
    
    public void calculateOOV(int iteration) throws IOException{
        FileWriter writer = new FileWriter(new File("tested/CMUTools/rekap_oov_cf_"+iteration));
        
        
        int oov_counter = 0;
        
        for (Iterator<String> iterator = testing_datas.iterator(); iterator.hasNext();) {
            String next = iterator.next();
            if(!training_datas.contains(next)){
                oov_counter++;
                oov_words.add(next);
            }
        }
        
        // Masukin ke array..
        oov_rekap[iteration] = oov_words.size();
        train_rekap[iteration] = training_datas.size();
        test_rekap[iteration] = testing_datas.size();
        
        System.out.println("Iteration : "+iteration+"\n---");
        System.out.println("OOV :"+oov_words.size());
        System.out.println("Training data :"+training_datas.size());
        System.out.println("Testing data : "+ testing_datas.size());
        
        for (Iterator<String> iterator = oov_words.iterator(); iterator.hasNext();) {
            String next = iterator.next();
            System.out.println(next);
        }
        
        System.out.println("");
        
        writer.write(generateReport(iteration, oov_counter, training_datas.size(), testing_datas.size(), oov_words));
        writer.write("\n");
        writer.close();
    }
    
    public void doCounting() throws IOException{
        for (int i = 0; i < num_folds; i++) {
            training_datas = new HashSet<>();
            testing_datas = new HashSet<>();
            oov_words = new HashSet<>();
            
            doReadTraining(i);
            doReadTesting(i);
            calculateOOV(i);
            
        }
        
        double total_oov = 0.0,total_train=0.0,total_test=0.0;
        for (int i = 0; i < oov_rekap.length; i++) {
            double p1 = (double) oov_rekap[i];
            double p2 = (double) train_rekap[i];
            double p3 = (double) test_rekap[i];
            total_oov   = total_oov + p1;
            total_train = total_train + p2;
            total_test  = total_test + p3;
        }
        total_oov = total_oov / (double) oov_rekap.length;
        total_test = total_test / (double) oov_rekap.length;
        total_train = total_train / (double) oov_rekap.length;
        System.out.println("Average OOV : ");
        System.out.println(total_oov);
        System.out.println("Average Train");
        System.out.println(total_train);
        System.out.println("Average Test");
        System.out.println(total_test);
        
    }
    
    private boolean isWord(String token){
        /*
        if((token.matches(Twokenize.url) 
                  || (token.matches(Twokenize.AtMention)) 
                  || (token.matches(Twokenize.Hashtag))
            )){
            return false;
        }else{
            return true;
        }
        /**/
        return true;
    }
    
    
    public static void main(String[] args){
        OOVRateCounter counter = new OOVRateCounter();
        try {
            counter.doCounting();
        } catch (IOException ex) {
            Logger.getLogger(OOVRateCounter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
