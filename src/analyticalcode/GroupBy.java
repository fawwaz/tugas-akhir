/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyticalcode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import utilitycode.MapUtil;

/**
 *
 * @author Asus
 */
public class GroupBy {
    String foldername = "experiment_5";
    HashMap<String,Stats> token_profile = new HashMap();
    SortedSet<String> sorted_key;
    Pattern pattern_capital = Pattern.compile("[A-Z]");
    Pattern pattern_number = Pattern.compile("[0-9]");
    
    double total_length_word = 0.0;
    double total_capital = 0.0;
    double total_number = 0.0;
    
    public void doReadTraining(int iteration){
        System.out.println("Reading Training data for iteration "+iteration);
        String filename = foldername+"/training_merged_"+iteration+".training";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine())!=null){
                if(line.equals("")){
                    
                }else{
                    String[] splitted = line.split(" ");
                    AddToTokenProfile(splitted);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    private void AddToTokenProfile(String[] splitted){
        String token    = splitted[0];
        String postag   = splitted[1];
        String label    = splitted[splitted.length-1];
        
        if(token_profile.containsKey(token)){
            Stats s = token_profile.get(token);
            
            if(s.postag_distribution.containsKey(postag)){
                int counter = s.postag_distribution.get(postag);
                s.postag_distribution.put(postag, counter+1);
            }else{
                s.postag_distribution.put(postag,1); // 1 untuk pertamakali..
            }
            
            if(s.label_distribution.containsKey(label)){
                int counter = s.label_distribution.get(label);
                s.label_distribution.put(label,counter+1);
            }else{
                s.label_distribution.put(label, 1); // 1 untuk pertamakali....
            }
            
            token_profile.put(token, s);
        }else{
            Stats s = new Stats();
            s.postag_distribution.put(postag,1); // 1 untuk pertamakali..
            s.label_distribution.put(label, 1); // 1 untuk pertamakali....
            s.word_length = token.length();
            s.counterCapital = countCapitalLetter(token);
            s.counterNumber = countNumberCharacter(token);
            token_profile.put(token, s);
        }
    }
    
    private Integer countCapitalLetter(String token){
        Integer counter = 0;
        Matcher m_capital = pattern_capital.matcher(token);
        while(m_capital.find()){
            counter++;
        }
        return counter;
    }
    
    
    private Integer countNumberCharacter(String token){
        Integer counter = 0;
        Matcher m_capital = pattern_number.matcher(token);
        while(m_capital.find()){
            counter++;
        }
        return counter;
    }
    
    public void sortEverything(){
        for (Map.Entry<String, Stats> entrySet : token_profile.entrySet()) {
            String key = entrySet.getKey();
            Stats s = entrySet.getValue();
            
            s.label_distribution = (HashMap<String, Integer>) MapUtil.sortByValue(s.label_distribution);
            s.postag_distribution = (HashMap<String, Integer>) MapUtil.sortByValue(s.postag_distribution);
            token_profile.put(key, s);
        }
        
        sorted_key = new TreeSet<String>(token_profile.keySet());
    }
    
    private String getStringtoWrite(String key){
        StringBuffer sb = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        StringBuffer sb3 = new StringBuffer();
        
        sb.append(key);
        int jml_tab;
        jml_tab = 6 - (key.length() / 4);
        for (int i = 0; i < jml_tab; i++){sb.append("\t");}
        
        Stats s = token_profile.get(key);
        
        sb.append(s.word_length);
        sb.append("\t");
        sb.append(s.counterCapital);
        sb.append("\t");
        sb.append(s.counterNumber);
        sb.append("\t");
        
        total_length_word = (double) (total_length_word + s.word_length);
        total_capital = (double) (total_capital + s.counterCapital);
        total_number = (double) (total_number + s.counterNumber);
        
        for (Map.Entry<String, Integer> entrySet : s.postag_distribution.entrySet()) {
            String key1 = entrySet.getKey();
            Integer value = entrySet.getValue();
            sb2.append(" "+key1+" "+value);
        }
        
        sb.append(sb2.toString());
        jml_tab = 6 - (sb2.toString().length() / 4);
        for (int i = 0; i < jml_tab; i++){sb.append("\t");}
        
        for (Map.Entry<String, Integer> entrySet : s.label_distribution.entrySet()) {
            String key1 = entrySet.getKey();
            Integer value = entrySet.getValue();
            sb3.append(" "+key1+" "+value);
        }
        
        sb.append(sb3.toString());
        sb.append("\n");
        
        return sb.toString();
    }
    
    public void writeToFile(int iteration) throws IOException{
        String filename = foldername + "/statistik_iterasi_"+iteration;
        FileWriter writer = new FileWriter(new File(filename));
        
        for (String key : sorted_key) {
            writer.write(getStringtoWrite(key));
        }
        total_capital = total_capital / (double) token_profile.size();
        total_length_word = total_length_word / (double) token_profile.size();
        total_number = total_number / (double) token_profile.size();
        
        System.out.println("Average length word :"+total_length_word);
        System.out.println("Average capital :"+total_capital);
        System.out.println("Average number :"+total_number);
        writer.close();
    }
    
    private class Stats{
        public HashMap<String,Integer> label_distribution = new HashMap<String,Integer>();
        public HashMap<String,Integer> postag_distribution = new HashMap<String,Integer>();
        Integer word_length;
        Integer counterCapital;
        Integer counterNumber;
    }
    
    public static void main(String[] args){
        GroupBy grouper = new GroupBy();
        grouper.doReadTraining(0);
        grouper.sortEverything();
        try {
            grouper.writeToFile(0);
        } catch (IOException ex) {
            Logger.getLogger(GroupBy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
