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
import javafx.scene.paint.Color;

/**
 *
 * @author Asus
 */
public class InsertIntoGazeteer {
    public String foldername="incrimental_learning_1";
    public int current_sub_iteration=1;
    public int current_iteration=3;
    
    ArrayList<String> label_standard = new ArrayList<>();
    ArrayList<String> gold_standard = new ArrayList<>();
    ArrayList<String> result = new ArrayList<>();
    ArrayList<String> original = new ArrayList<>();
    
    HashMap<String, Integer> b_name_counter = new HashMap<>();
    HashMap<String, Integer> i_name_counter = new HashMap<>();
    HashMap<String, Integer> b_loc_counter = new HashMap<>();
    HashMap<String, Integer> i_loc_counter = new HashMap<>();
    HashMap<String, Integer> b_time_counter = new HashMap<>();
    HashMap<String, Integer> i_time_counter = new HashMap<>();
    HashMap<String, Integer> o_counter = new HashMap<>();
    HashMap<String, Float> total_counter = new HashMap<>();
    ArrayList<String> name_gazetter = new ArrayList<>();
    ArrayList<String> loc_gazetteer = new ArrayList<>();
    
    float treshold_limit = 0.8f;
    
    public InsertIntoGazeteer(){
        label_standard = new ArrayList<>();
        label_standard.add("B-Name");
        label_standard.add("I-Name");
        label_standard.add("B-Time");
        label_standard.add("I-Time");
        label_standard.add("B-Location");
        label_standard.add("I-Location");
        label_standard.add("O");
    }
    
    public void doReadFileGoldStandard(){
        // cuma di run sekali ..
        //String filename = foldername+"/incrimental_iteration_"+current_iteration+"/testing_merged_"+current_sub_iteration+".gold_standard";
        String filename = foldername+"/incrimental_iteration_"+current_iteration+"/testing_merged_sub_iteration_"+current_sub_iteration+".gold_standard";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine())!=null){
                if(!line.equals("")){
                    gold_standard.add(line.trim());
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void doReadFileResult(){
        // cuma di run sekali..
        //String filename = foldername+"/incrimental_iteration_"+current_iteration+"/testing_merged_"+current_sub_iteration+".result";
        String filename = foldername+"/incrimental_iteration_"+current_iteration+"/testing_merged_sub_iteration_"+current_sub_iteration+".result";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine())!=null){
                if(!line.equals("")){
                    result.add(line.trim());
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void doReadFileUntagged(){
        // cuma di run sekali
        //String filename = foldername+"/incrimental_iteration_"+current_iteration+"/testing_merged_start_iteration_"+current_sub_iteration+".untagged";
        String filename = foldername+"/incrimental_iteration_"+current_iteration+"/testing_merged_sub_iteration_"+current_sub_iteration+".untagged";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine())!=null){
                if(!line.equals("")){
                    String[] splitted = line.split(" ");
                    original.add(splitted[0].trim());
                    total_counter.put(splitted[0].trim(),0.0f);
                    //total_counter.add(splitted[0].trim(),0.0); // by default 0 dulu..
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void printeverything(){
        /*
        for (int i = 0; i < gold_standard.size(); i++) {
            System.out.println("Gold Standard :"+gold_standard.get(i));
        }
        
        for (int i = 0; i < result.size(); i++) {
            String get = result.get(i);
            System.out.println(get);
        }
        
        for (int i = 0; i < original.size(); i++) {
            String get = original.get(i);
            System.out.println(get);
        }
        */
        /*
        System.out.println("B Name Counter Is here : ");
        for (Map.Entry<String, Integer> entrySet : b_name_counter.entrySet()) {
            String key = entrySet.getKey();
            Integer value = entrySet.getValue();
            System.out.println(key+" : "+value);
        }
        
        System.out.println("I Name Counter is here : ");
        for (Map.Entry<String, Integer> entrySet : i_name_counter.entrySet()) {
            String key = entrySet.getKey();
            Integer value = entrySet.getValue();
            System.out.println(key+" : "+value);
        }
        
        System.out.println("B Location Counter is here : ");
        for (Map.Entry<String, Integer> entrySet : b_loc_counter.entrySet()) {
            String key = entrySet.getKey();
            Integer value = entrySet.getValue();
            System.out.println(key+" : "+value);
        }
        
        System.out.println("I Location counter is here :");
        for (Map.Entry<String, Integer> entrySet : i_loc_counter.entrySet()) {
            String key = entrySet.getKey();
            Integer value = entrySet.getValue();
            System.out.println(key+" : "+value);
        }
        
        System.out.println("B Time counter is here : ");
        for (Map.Entry<String, Integer> entrySet : b_time_counter.entrySet()) {
            String key = entrySet.getKey();
            Integer value = entrySet.getValue();
            System.out.println(key+" : "+value);
        }
        
        System.out.println("I Time counter is here :");
        for (Map.Entry<String, Integer> entrySet : i_time_counter.entrySet()) {
            String key = entrySet.getKey();
            Integer value = entrySet.getValue();
            System.out.println(key+" : "+value);
        }
        
        System.out.println("O counter is here : ");
        for (Map.Entry<String, Integer> entrySet : o_counter.entrySet()) {
            String key = entrySet.getKey();
            Integer value = entrySet.getValue();
            System.out.println(key+" : "+value);
        }
        /**/
        System.out.println("Now Gazetteer list is \n------");
        for (int i = 0; i < name_gazetter.size(); i++) {
            String elementAt = name_gazetter.get(i);
            System.out.println(elementAt);
        }
        
        System.out.println("Now Gazetteer list is \n------");
        for (int i = 0; i < loc_gazetteer.size(); i++) {
            String elementAt = loc_gazetteer.get(i);
            System.out.println(elementAt);
        }
    }
    
    public void doCounting(){
        for (int i = 0; i < result.size(); i++) {
            String label = result.get(i);
            String token = original.get(i);
            increaseCounter(label, token);
        }
    }
    
    public void doCountTotal(){
        for (Map.Entry<String, Float> entrySet : total_counter.entrySet()) {
            String key = entrySet.getKey();
            Float value = entrySet.getValue();
            
            Integer total_int = 0;
            int b_n_val = b_name_counter.containsKey(key) ? b_name_counter.get(key) : 0;
            int i_n_val = i_name_counter.containsKey(key) ? i_name_counter.get(key) : 0;
            int b_l_val = b_loc_counter.containsKey(key) ? b_loc_counter.get(key) : 0;
            int i_l_val = i_loc_counter.containsKey(key) ? i_loc_counter.get(key) : 0;
            int b_t_val = b_time_counter.containsKey(key) ? b_time_counter.get(key) : 0;
            int i_t_val = i_time_counter.containsKey(key) ? i_time_counter.get(key) : 0;
            int o_val   = o_counter.containsKey(key) ? o_counter.get(key) : 0;
            
            total_int = b_n_val + i_n_val + b_l_val + i_l_val + b_t_val + i_t_val + o_val;
            Float total_float = Float.valueOf(total_int);
            total_counter.put(key, total_float);
        }
    }
    
    private void increaseCounter(String label,String token){
        if(label.equals("B-Name")){
            Integer counter = b_name_counter.containsKey(token) ? b_name_counter.get(token) : 0;
            b_name_counter.put(token, counter+1);
        }else if(label.equals("I-Name")){
            Integer counter = i_name_counter.containsKey(token) ? i_name_counter.get(token) : 0;
            i_name_counter.put(token, counter+1);
        }else if(label.equals("B-Location")){
            Integer counter = b_loc_counter.containsKey(token) ? b_loc_counter.get(token) : 0;
            b_loc_counter.put(token, counter+1);
        }else if(label.equals("I-Location")){
            Integer counter = i_loc_counter.containsKey(token) ? i_loc_counter.get(token) : 0;
            i_loc_counter.put(token, counter+1);
        }else if(label.equals("B-Time")){
            Integer counter = b_time_counter.containsKey(token) ? b_time_counter.get(token) : 0;
            b_time_counter.put(token, counter+1);
        }else if(label.equals("I-Time")){
            Integer counter = i_time_counter.containsKey(token) ? i_time_counter.get(token) : 0;
            i_time_counter.put(token, counter+1);
        }else if(label.equals("O")){
            Integer counter = o_counter.containsKey(token) ? o_counter.get(token) : 0;
            o_counter.put(token,counter+1);
        }else{
            System.out.println("Error karena diluar label itu semua karena labellnya : "+label + " tokenya :"+token);
        }
    }
    
    public void doCountProbability(){
        for (Map.Entry<String, Integer> entrySet : b_name_counter.entrySet()) {
            String key = entrySet.getKey();
            Integer value = entrySet.getValue();
            Float probability = (float) value / (float) total_counter.get(key);
            System.out.println("Probability Name for token : "+key+ " is "+probability);
            if(probability>treshold_limit){
                name_gazetter.add(key);
            }
        }
        
        for (Map.Entry<String, Integer> entrySet : i_name_counter.entrySet()) {
            String key = entrySet.getKey();
            Integer value = entrySet.getValue();
            Float probability = (float) value / (float) total_counter.get(key);
            System.out.println("Probability Name for token : "+key+ " is "+probability);
            if(probability>treshold_limit){
                name_gazetter.add(key);
            }
        }
        
        for (Map.Entry<String, Integer> entrySet : b_loc_counter.entrySet()) {
            String key = entrySet.getKey();
            Integer value = entrySet.getValue();
            Float probability = (float) value / (float) total_counter.get(key);
            System.out.println("Probability Name for token : "+key+ " is "+probability);
            if(probability>treshold_limit){
                loc_gazetteer.add(key);
            }
        }
        
        for (Map.Entry<String, Integer> entrySet : i_loc_counter.entrySet()) {
            String key = entrySet.getKey();
            Integer value = entrySet.getValue();
            Float probability = (float) value / (float) total_counter.get(key);
            System.out.println("Probability Name for token : "+key+ " is "+probability);
            if(probability>treshold_limit){
                loc_gazetteer.add(key);
            }
        }
    }
    
    public String getGazeteerAsString(ArrayList<String> gazeteer){
        StringBuffer sb = new StringBuffer();
        sb.append("\n");
        for (int i = 0; i < gazeteer.size(); i++) {
            String name = gazeteer.get(i);
            sb.append(name+"\n");
        }
        return sb.toString();
    }
    
    public void startWriter() throws IOException{
        // Append name gazetteer
        File f1 = new File(foldername+"/incrimental_iteration_"+current_iteration+"/gazetteer_nama_event");
        FileWriter writer1 = new FileWriter(f1,true); // append...
        writer1.write(getGazeteerAsString(name_gazetter));
        writer1.close();
        
        
        File f2 = new File(foldername+"/incrimental_iteration_"+current_iteration+"/gazetteer_lokasi_event");
        FileWriter writer2 = new FileWriter(f2,true); // append...
        writer2.write(getGazeteerAsString(loc_gazetteer));
        writer2.close();
    }
    
    
    public static void main(String[] args){
        InsertIntoGazeteer inserter = new InsertIntoGazeteer();
        inserter.doReadFileGoldStandard();
        inserter.doReadFileResult();
        inserter.doReadFileUntagged();
        inserter.doCounting();
        inserter.doCountTotal();
        inserter.doCountProbability();
        inserter.printeverything();
        try {
            inserter.startWriter();
            // list b-name dan probabilitynya yang diklasifikasikan diaatas treshold..
        } catch (IOException ex) {
            Logger.getLogger(InsertIntoGazeteer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
