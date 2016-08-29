/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilitycode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Asus
 */
public class GazetteerGenerator {
    HashMap<String, Double> word_dictionary = new HashMap<>(); // first store for counting, last replace it with it's probability
    HashMap<String, Double> label_dictionary = new HashMap<>(); // first sore it for counting, last replace it with it's probability
    
    HashMap<String, Double> b_name_counter = new HashMap<>(); // store it for counting first... then replace it with its' value
    HashMap<String, Double> i_name_counter = new HashMap<>();
    HashMap<String, Double> b_loc_counter = new HashMap<>();
    HashMap<String, Double> i_loc_counter = new HashMap<>();
    HashMap<String, Double> b_time_counter = new HashMap<>();
    HashMap<String, Double> i_time_counter = new HashMap<>();
    HashMap<String, Double> o_counter = new HashMap<>();
    
    ArrayList<String> name_gazetteer = new ArrayList<>();
    ArrayList<String> location_gazetteer = new ArrayList<>();
    
    Double total_word;
    Double total_label;
    Double total_word_label;
    Double treshold = 0.9;
    int fold_num = 4;
    
    private void doReset(){
        word_dictionary = new HashMap<>(); // first store for counting, last replace it with it's probability
        label_dictionary = new HashMap<>(); // first sore it for counting, last replace it with it's probability
    
        b_name_counter = new HashMap<>(); // store it for counting first... then replace it with its' value
        i_name_counter = new HashMap<>();
        b_loc_counter = new HashMap<>();
        i_loc_counter = new HashMap<>();
        b_time_counter = new HashMap<>();
        i_time_counter = new HashMap<>();
        o_counter = new HashMap<>();
    
        name_gazetteer = new ArrayList<>();
        location_gazetteer = new ArrayList<>();
    
        total_word =0.0;
        total_label =0.0;
        total_word_label =0.0;
    }
    
    public void doReadFileTraining(int iteration){
        System.out.println("Reading Training data for iteration "+iteration);
        String filename = "tested/CMUTools/training_merged_"+iteration+".training";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine())!=null){
                if(line.equals("")){
                    
                }else{
                    String[] splitted = line.split(" ");
                    String token = splitted[1].trim();
                    String label = splitted[splitted.length-1].trim();
                    Double counter_word = word_dictionary.containsKey(token) ? word_dictionary.get(token) : 0.0;
                    word_dictionary.put(token, counter_word+1);
                    
                    Double counter_label = label_dictionary.containsKey(label) ? label_dictionary.get(label) : 0.0;
                    label_dictionary.put(label, counter_label+1);
                    
                    increaseLabel(token, label);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    private void increaseLabel(String token,String label){
        Double counter;
        if(label.equals("B-Name")){
            counter = b_name_counter.containsKey(token) ? b_name_counter.get(token) : 0.0;
            b_name_counter.put(token, counter+1);
        }else if(label.equals("I-Name")){
            counter = i_name_counter.containsKey(token) ? i_name_counter.get(token) : 0.0;
            i_name_counter.put(token, counter+1);
        }else if(label.equals("B-Location")){
            counter = b_loc_counter.containsKey(token) ? b_loc_counter.get(token) : 0.0;
            b_loc_counter.put(token, counter+1);
        }else if(label.equals("I-Location")){
            counter = i_loc_counter.containsKey(token) ? i_loc_counter.get(token) : 0.0;
            i_loc_counter.put(token, counter+1);
        }else if(label.equals("B-Time")){
            counter = b_time_counter.containsKey(token) ? b_time_counter.get(token) : 0.0;
            b_time_counter.put(token, counter+1);
        }else if(label.equals("I-Time")){
            counter = i_time_counter.containsKey(token) ? i_time_counter.get(token) : 0.0;
            i_time_counter.put(token, counter+1);
        }else if(label.equals("O")){
            counter = o_counter.containsKey(token) ? o_counter.get(token) : 0.0;
            o_counter.put(token, counter+1);
        }
    }
    
    private void printEverything(){
        System.out.println("\n\n\nWord Dictionary is : \n===");
        for (Map.Entry<String, Double> entrySet : word_dictionary.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue();
            System.out.println(key+"\t"+value);
        }
        
        System.out.println("\n\n\nLabel Dictionary is : \n===");
        for (Map.Entry<String, Double> entrySet : label_dictionary.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue();
            System.out.println(key+"\t"+value);
        }
        
        System.out.println("\n\n\n\n\n\n-------------------------------------\n\n\n\n\n\nB-Name Dictionary is : \n===");
        for (Map.Entry<String, Double> entrySet : b_name_counter.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue();
            System.out.println(key+"\t"+value);
        }
        
        System.out.println("\n\n\nI-Name Dictionary is : \n===");
        for (Map.Entry<String, Double> entrySet : i_name_counter.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue();
            System.out.println(key+"\t"+value);
        }
        
        System.out.println("\n\n\nB-Location Dictionary is : \n===");
        for (Map.Entry<String, Double> entrySet : b_loc_counter.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue();
            System.out.println(key+"\t"+value);
        }
        
        System.out.println("\n\n\nI-Location Dictionary is : \n===");
        for (Map.Entry<String, Double> entrySet : i_loc_counter.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue();
            System.out.println(key+"\t"+value);
        }
        
        System.out.println("\n\n\nB-Time Dictionary is : \n===");
        for (Map.Entry<String, Double> entrySet : b_time_counter.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue();
            System.out.println(key+"\t"+value);
        }
        
        System.out.println("\n\n\nI-Time Dictionary is : \n===");
        for (Map.Entry<String, Double> entrySet : i_time_counter.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue();
            System.out.println(key+"\t"+value);
        }
        
        System.out.println("\n\n\nO Dictionary is : \n===");
        for (Map.Entry<String, Double> entrySet : o_counter.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue();
            System.out.println(key+"\t"+value);
        }
        
        System.out.println("\n\n\nTotal Word is : "+total_word);
        System.out.println("Total Label is : "+total_label);
        
    }
    
    private void countTotal(){
        total_word = 0.0;
        total_label = 0.0;
        
        // Gak harus total label itu pasti sama..po
        for (Map.Entry<String, Double> entrySet : word_dictionary.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue();
            total_word = total_word + value;
        }
        
        for (Map.Entry<String, Double> entrySet : label_dictionary.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue();
            total_label = total_label + value;
        }         
    }
    
    private void countPriorProbability(){
        for (Map.Entry<String, Double> entrySet : word_dictionary.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue();
            Double new_value = value / total_word;
            word_dictionary.put(key, new_value);
        }
        
        for (Map.Entry<String, Double> entrySet : label_dictionary.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue();
            Double new_value = value / total_label;
            label_dictionary.put(key, new_value);
        } 
    }
    
    private void countMutualInformation(){
        for (Map.Entry<String, Double> entrySet : b_name_counter.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue(); // counter term which has a label b_name
            Double top_value = value / total_word;
            Double bottom_value = word_dictionary.get(key) * label_dictionary.get("B-Name"); // prior probability
            
            Double new_value = Math.log10(top_value / bottom_value);
            b_name_counter.put(key, new_value);
        }
        
        for (Map.Entry<String, Double> entrySet : i_name_counter.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue(); // counter term which has a label i_name
            Double top_value = value / total_word;
            Double bottom_value = word_dictionary.get(key) * label_dictionary.get("I-Name");// prior probability
            
            Double new_value = Math.log10(top_value / bottom_value);
            i_name_counter.put(key, new_value);
        }
        
        for (Map.Entry<String, Double> entrySet : b_loc_counter.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue(); // counter term which has a label i_name
            Double top_value = value / total_word;
            Double bottom_value = word_dictionary.get(key) * label_dictionary.get("B-Location"); // prior probability
            
            Double new_value = Math.log10(top_value / bottom_value);
            b_loc_counter.put(key, new_value);
        }
        
        for (Map.Entry<String, Double> entrySet : i_loc_counter.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue(); // counter term which has a label i_name
            Double top_value = value / total_word;
            Double bottom_value = word_dictionary.get(key) * label_dictionary.get("I-Location"); // prior probability
            
            Double new_value = Math.log10(top_value / bottom_value);
            i_loc_counter.put(key, new_value);
        }
        
        for (Map.Entry<String, Double> entrySet : b_time_counter.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue(); // counter term which has a label i_name
            Double top_value = value / total_word;
            Double bottom_value = word_dictionary.get(key) * label_dictionary.get("B-Time"); // prior probability
            
            Double new_value = Math.log10(top_value / bottom_value);
            b_time_counter.put(key, new_value);
        }
        
        for (Map.Entry<String, Double> entrySet : i_time_counter.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue(); // counter term which has a label i_name
            Double top_value = value / total_word;
            Double bottom_value = word_dictionary.get(key) * label_dictionary.get("I-Time"); // prior probability
            
            Double new_value = Math.log10(top_value / bottom_value);
            i_time_counter.put(key, new_value);
        }
        
        for (Map.Entry<String, Double> entrySet : o_counter.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue(); // counter term which has a label i_name
            Double top_value = value / total_word;
            Double bottom_value = word_dictionary.get(key) * label_dictionary.get("O"); // prior probability
            
            Double new_value = Math.log10(top_value / bottom_value);
            o_counter.put(key, new_value);
        }
    }
    
    private void addToGazetteer(){
        b_name_counter = (HashMap<String, Double>) MapUtil.sortByValue(b_name_counter);
        i_name_counter = (HashMap<String, Double>) MapUtil.sortByValue(i_name_counter);
        b_loc_counter = (HashMap<String, Double>) MapUtil.sortByValue(b_loc_counter);
        i_loc_counter = (HashMap<String, Double>) MapUtil.sortByValue(i_loc_counter);
        
        for (Map.Entry<String, Double> entrySet : b_name_counter.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue();
            if(value>treshold){
                name_gazetteer.add(key);
            }
        }
        /*
        for (Map.Entry<String, Double> entrySet : i_name_counter.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue();
            if(value>treshold){
                name_gazetteer.add(key);
            }
        }
        /**/
        for (Map.Entry<String, Double> entrySet : b_loc_counter.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue();
            if(value>treshold){
                location_gazetteer.add(key);
            }
        }
        /*
        for (Map.Entry<String, Double> entrySet : i_loc_counter.entrySet()) {
            String key = entrySet.getKey();
            Double value = entrySet.getValue();
            if(value>treshold){
                location_gazetteer.add(key);
            }
        }
        /**/
    }
    
    private void writeGazetteer(int iteration) throws IOException{
        FileWriter writer = new FileWriter(new File("tested/CMUTools/gazetteer_prev_name_"+iteration));
        for (int i = 0; i < name_gazetteer.size(); i++) {
            String nama = name_gazetteer.get(i);
            double val = b_name_counter.containsKey(nama) ? b_name_counter.get(nama) : i_name_counter.get(nama);
            writer.write(nama+"\t"+val+"\n");
        }
        writer.close();
        
        
        FileWriter writer2 = new FileWriter(new File("tested/CMUTools/gazetteer_prev_location_"+iteration));
        for (int i = 0; i < location_gazetteer.size(); i++) {
            String get = location_gazetteer.get(i);
            double val = b_loc_counter.containsKey(get) ? b_loc_counter.get(get) : i_loc_counter.get(get);
            writer2.write(get+"\t"+val+"\n");
        }
        writer2.close();
    }
    
    private void doGenerate() throws IOException{
        int urutan = 0;
        
        for (int i = 0; i < fold_num; i++) {
            // Instatntiate everything agar reset deri awal..
            doReset();
            doReadFileTraining(i);
            countTotal();
            countPriorProbability();
            //printEverything();
            countMutualInformation();
            //printEverything();
            addToGazetteer();
            writeGazetteer(i);
        }
    }
    
    public static void main(String[] args){
        GazetteerGenerator generator = new GazetteerGenerator();
        try {
            generator.doGenerate();
        } catch (IOException ex) {
            Logger.getLogger(GazetteerGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
