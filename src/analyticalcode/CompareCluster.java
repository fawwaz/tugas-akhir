/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyticalcode;

import cmu.Twokenize;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilitycode.MapUtil;

/**
 *
 * @author Asus
 */
public class CompareCluster {
    HashMap<String, String> token_to_cluster_mapping = new HashMap<String, String>();
    HashMap<String, String> cluster_to_number_mapping = new HashMap<String, String>();
    int counter_not_found = 0;
    int longest_lengt=0;
    public void doReadTraining(int iteration){
        System.out.println("Reading Training data for iteration "+iteration);
        String filename = "tested/CMUTools/NER_gold_standard";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine())!=null){
                if(line.equals("")){
                    
                }else{
                    String[] splitted = line.split("\t");
                    if(isWord(splitted[0])){
                        token_to_cluster_mapping.put(splitted[0].trim(),"");
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void doReadCluster(){
        //String filename = "tested/paths_minimum_5";
        String filename = "tested/paths_no_minimum";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine())!=null){
                if(line.equals("")){
                    
                }else{
                    String[] splitted = line.split("\\s");
                    cluster_to_number_mapping.put(splitted[1].trim(),splitted[0].trim());
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    private String getContent(String token,String cluster){
        StringBuffer sb = new StringBuffer();
        
        sb.append(token);
        
        int jml_tab = 7 - (token.length() / 4);
        for (int i = 0; i < jml_tab; i++) {sb.append("\t");}
        sb.append("\t");
        sb.append(cluster+"\n");
        
        return sb.toString();
    }
    
    public void findCluster(int iteration){
        counter_not_found = 0;
        for (Map.Entry<String, String> entrySet : token_to_cluster_mapping.entrySet()) {
            String key = entrySet.getKey();
            if(cluster_to_number_mapping.containsKey(key)){
                String cluster_binary = cluster_to_number_mapping.get(key);
                if(cluster_binary.length()>longest_lengt){
                    longest_lengt = cluster_binary.length();
                }
                token_to_cluster_mapping.put(key, cluster_binary);
                //writer.write(getContent(key, cluster_binary));
            }else{
                token_to_cluster_mapping.put(key, "--NOTFOUND--");
                counter_not_found++;
                //writer.write(getContent(key, "--NOT FOUND --"));
            }
        }
    }
    
    public void write(int iteration) throws IOException{
        FileWriter writer = new FileWriter(new File("tested/Cluster_After_Mapping_"+iteration));
        writer.write("OOV Counter : "+counter_not_found);
        
        token_to_cluster_mapping = (HashMap<String, String>) MapUtil.sortByValue(token_to_cluster_mapping);
        for (Map.Entry<String, String> entrySet : token_to_cluster_mapping.entrySet()) {
            String key = entrySet.getKey();
            String value = entrySet.getValue();
            writer.write(getContent(key, value));
        }
        writer.close();
        System.out.println("Longest length is "+longest_lengt);
    }
    
    private boolean isWord(String token){
        if(token.matches(Twokenize.url)
                || token.matches(Twokenize.AtMention)
                || token.matches(Twokenize.Hashtag)
                || token.matches(Twokenize.emoticon)
        ){
            return false;
        }else{
            return true;
        }
    }
    
    
    
    public static void main(String[] args){
        CompareCluster comparer = new CompareCluster();
        comparer.doReadTraining(0);
        comparer.doReadCluster();
        try {
            comparer.findCluster(0);
            comparer.write(0);
        } catch (IOException ex) {
            Logger.getLogger(CompareCluster.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
