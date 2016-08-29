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
public class CalculateMatrixPOStag {
    ArrayList<String> gold_standard = new ArrayList<String>();
    ArrayList<String> tagged = new ArrayList<String>();
    HashMap<String, Integer> tag_to_id_mapping = new HashMap<String, Integer>();
    HashMap<Integer, String> id_to_tag_mapping = new HashMap<Integer,String>();
    Integer[][] confussionmatrix;
    Integer[] false_positive;
    Integer[] false_negative;
    Double[] recall;
    Double[] precission;
    
    public CalculateMatrixPOStag(){
        tag_to_id_mapping.put("N",0);
        tag_to_id_mapping.put("O",1);
        tag_to_id_mapping.put("S",2);
        tag_to_id_mapping.put("^",3);
        tag_to_id_mapping.put("Z",4);
        tag_to_id_mapping.put("L",5);
        tag_to_id_mapping.put("M",6);
        tag_to_id_mapping.put("V",7);
        tag_to_id_mapping.put("A",8);
        tag_to_id_mapping.put("R",9);
        tag_to_id_mapping.put("!",10);
        tag_to_id_mapping.put("D",11);
        tag_to_id_mapping.put("P",12);
        tag_to_id_mapping.put("&",13);
        tag_to_id_mapping.put("T",14);
        tag_to_id_mapping.put("X",15);
        tag_to_id_mapping.put("Y",16);
        tag_to_id_mapping.put("#",17);
        tag_to_id_mapping.put("@",18);
        tag_to_id_mapping.put("~",19);
        tag_to_id_mapping.put("U",20);
        tag_to_id_mapping.put("E",21);
        tag_to_id_mapping.put("$",22);
        tag_to_id_mapping.put(",",23);
        tag_to_id_mapping.put("G",24);
        
        id_to_tag_mapping.put(0, "N");
        id_to_tag_mapping.put(1, "O");
        id_to_tag_mapping.put(2, "S");
        id_to_tag_mapping.put(3, "^");
        id_to_tag_mapping.put(4, "Z");
        id_to_tag_mapping.put(5, "L");
        id_to_tag_mapping.put(6, "M");
        id_to_tag_mapping.put(7, "V");
        id_to_tag_mapping.put(8, "A");
        id_to_tag_mapping.put(9, "R");
        id_to_tag_mapping.put(10, "!");
        id_to_tag_mapping.put(11, "D");
        id_to_tag_mapping.put(12, "P");
        id_to_tag_mapping.put(13, "&");
        id_to_tag_mapping.put(14, "T");
        id_to_tag_mapping.put(15, "X");
        id_to_tag_mapping.put(16, "Y");
        id_to_tag_mapping.put(17, "#");
        id_to_tag_mapping.put(18, "@");
        id_to_tag_mapping.put(19, "~");
        id_to_tag_mapping.put(20, "U");
        id_to_tag_mapping.put(21, "E");
        id_to_tag_mapping.put(22, "$");
        id_to_tag_mapping.put(23, ",");
        id_to_tag_mapping.put(24, "G");
        
        confussionmatrix = new Integer[tag_to_id_mapping.size()][tag_to_id_mapping.size()];
        false_negative = new Integer[tag_to_id_mapping.size()];
        false_positive = new Integer[tag_to_id_mapping.size()];
        
        recall = new Double[tag_to_id_mapping.size()];
        precission = new Double[tag_to_id_mapping.size()];
    }
    
    
    public void doReadFileGoldStandard(){
        System.out.println("Reading Gold Standard POSTagging");
        String filename = "tested/CMUTools/POSTagged_gold_standard";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine())!=null){
                if(line.equals("")){
                    
                }else{
                    String[] splitted = line.split("\\s");
                    gold_standard.add(splitted[1]);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void doReadFileCMUPoStagged(){
        System.out.println("Reading Gold CMU");
        String filename = "tested/CMUTools/POSTagged";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine())!=null){
                if(line.equals("")){
                    
                }else{
                    String[] splitted = line.split("\\s");
                    tagged.add(splitted[1]);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void doReadFileInaNLPPoStagged(){
        System.out.println("Reading Gold CMU");
        String filename = "tested/InaNlp/POSTagged_InaNlp";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            int i =0;
            while((line = br.readLine())!=null){
                i++;
                if(line.equals("")){
                    
                }else{
                    System.out.println("line : "+i);
                    String[] splitted = line.split("\\s");
                    String converted_label;
                    if(splitted.length==1){
                        converted_label = "U";
                    }else{
                        converted_label = TranslateInaNLPToCMU(splitted[1]);
                    }
                    
                    tagged.add(converted_label);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void doReadFileOldMethod(){
        System.out.println("Reading Old Method");
        String filename = "tested/old_method/POSTagged_old_method";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            int i =0;
            while((line = br.readLine())!=null){
                i++;
                if(line.equals("")){
                    
                }else{
                    System.out.println("line : "+i);
                    String[] splitted = line.split("\\s");
                    tagged.add(splitted[1]);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void countConfussionMatrix(){
        // Initialize array confusion matrix with 0
        for (int i = 0; i < confussionmatrix.length; i++) {
            for (int j = 0; j < confussionmatrix[i].length; j++) {
                confussionmatrix[i][j] = 0;
            }
        }
        
        
        for (int i = 0; i < gold_standard.size(); i++) {
            if(i==2396){
                System.out.println("Break point loh");
            }
            String _gold_standard = gold_standard.get(i);
            String _tagged        = tagged.get(i);
            
            Integer id_gold_standard = tag_to_id_mapping.get(_gold_standard);
            Integer id_tagged        = tag_to_id_mapping.get(_tagged);
            
            confussionmatrix[id_gold_standard][id_tagged]++;
        }
    }
    
    private void CalculateFalsePositiveAndNegative(){
        // Initiate first
        for (int i = 0; i < false_negative.length; i++) {
            false_negative[i] = 0;
            false_positive[i] = 0;
        }
        
        // Calculate false positive
        for (int i = 0; i < confussionmatrix.length; i++) {
            for (int j = 0; j < confussionmatrix[i].length; j++) {
                false_positive[i] = false_positive[i] + confussionmatrix[i][j];
                false_negative[j] = false_negative[j] + confussionmatrix[i][j];
            }
        }
    }
    
    public void CalculatePrecissionAndRecall(){
        CalculateFalsePositiveAndNegative();
        for (int i = 0; i < recall.length; i++) {
            if(false_positive[i]!=0){
                recall[i] = Double.valueOf(confussionmatrix[i][i]) / Double.valueOf(false_positive[i]);
            }else{
                recall[i] = 0.0;
            }
            if(false_negative[i]!=0){
                precission[i] = Double.valueOf(confussionmatrix[i][i]) / Double.valueOf(false_negative[i]);
            }else{
                precission[i] = 0.0;
            }
        }
    }
    
    public void printEverything() throws IOException{
        StringBuffer sb = new StringBuffer();
        
        sb.append("\t\t");
        // Create header..
        for (int i = 0; i < confussionmatrix.length; i++) {
            sb.append(id_to_tag_mapping.get(i)+"\t");
        }
        sb.append("\n");
        
        
        for (int i = 0; i < confussionmatrix.length; i++) {
            // print left column
            sb.append(id_to_tag_mapping.get(i)+"\t\t");
                    
            // print main confussion matrix
            Integer[] confussionmatrix1 = confussionmatrix[i];
            for (int j = 0; j < confussionmatrix1.length; j++) {
                Integer confussionmatrix11 = confussionmatrix1[j];
                sb.append(confussionmatrix11+"\t");
            }
            sb.append("\n");
        }
        
        System.out.println(sb.toString());
        
        StringBuffer sb2 = new StringBuffer();
        sb2.append("\tRecall\tPrecission\n");
        for (int i = 0; i < precission.length; i++) {
            String tag = id_to_tag_mapping.get(i);
            String _recall = String.format("%.2f", recall[i]);
            String _precission = String.format("%.2f", precission[i]);
            Double __fmeasure;
            if((precission[i]!=0) && (recall[i]!=0)){
                __fmeasure = 2*recall[i]*precission[i]/(precission[i]+recall[i]);
            }else{
                __fmeasure = 0.0;
            }
            String _fmeasure = String.format("%.2f", __fmeasure);
            sb2.append(tag+"\t"+_recall+"\t"+_precission+"\t"+_fmeasure+"\n");
        }
        System.out.println(sb2.toString());
        
        
        StringBuffer sb3 = new StringBuffer();
        sb3.append("Overall: \n");
        System.out.println("Overall :");
        Double total_recall = 0.0;
        Double total_precission = 0.0;
        for (int i = 0; i < recall.length; i++) {
            total_recall = total_recall + recall[i];
            total_precission = total_precission + precission[i];
        }
        total_recall = total_recall / recall.length;
        total_precission = total_precission / precission.length;
        sb3.append(total_recall+"\t"+total_precission+"\n");
        System.out.println(total_recall+"\t"+total_precission);
        
        FileWriter writer = new FileWriter(new File("tested/CMUTools/Confussion_POSTag"));
        writer.write(sb.toString());
        writer.write(sb2.toString());
        writer.write(sb3.toString());
        writer.close();
    }
    
    public String TranslateInaNLPToCMU(String tag_inanlp){
        String retval = null;
        if(tag_inanlp.equals("NNP")){
            retval = "^";
        }else if (tag_inanlp.equals("NN")){
            retval = "N";
        }else if(tag_inanlp.equals("CDP")){
            retval = "$";
        }else if(tag_inanlp.equals("JJ")){
            retval = "A";
        }else if(tag_inanlp.equals("RB")){
            retval = "R";
        }else if(tag_inanlp.equals("UH")){
            retval = "!";
        }else if(tag_inanlp.equals("WP")){
            retval = "O";
        }else if(tag_inanlp.equals("PRP")){
            retval = "O";
        }else if(tag_inanlp.equals("NEG")){
            retval = "T";
        }else if(tag_inanlp.equals("PRL")){
            retval = "X";
        }else if(tag_inanlp.equals("DT")){
            retval = "D";
        }else if(tag_inanlp.equals("IN")){
            retval = "P";
        }else if(tag_inanlp.equals("RP")){
            retval = "T";
        }else if(tag_inanlp.equals("CDI")){
            retval = "$";
        }else if(tag_inanlp.equals("CDO")){
            retval = "$";
        }else if(tag_inanlp.equals("CDC")){
            retval = "$";
        }else if(tag_inanlp.equals("PRN")){
            retval = "$";
        }else if(tag_inanlp.equals("SC")){
            retval = "P";
        }else if(tag_inanlp.equals("CC")){
            retval = "&";
        }else if(tag_inanlp.equals("HASHTAG")){
            retval = "#";
        }else if(tag_inanlp.equals("MENTION")){
            retval = "@";
        }else if(tag_inanlp.equals("URL")){
            retval = "U";
        }else if(tag_inanlp.equals(".")){
            retval = ",";
        }else if(tag_inanlp.equals(":")){
            retval = ",";
        }else if(tag_inanlp.equals("OP")){
            retval = ",";
        }else if(tag_inanlp.equals("CP")){
            retval = ",";
        }else if(tag_inanlp.equals("-")){
            retval = ",";
        }else{
            retval = "G";
        }
        return retval;
    }
    
    public static void main(String[] args){
        CalculateMatrixPOStag matrix_postag = new CalculateMatrixPOStag();
        matrix_postag.doReadFileGoldStandard();
        matrix_postag.doReadFileCMUPoStagged();
        //matrix_postag.doReadFileInaNLPPoStagged();
        //matrix_postag.doReadFileOldMethod();
        matrix_postag.countConfussionMatrix();
        matrix_postag.CalculatePrecissionAndRecall();
        try {
            matrix_postag.printEverything();
        } catch (IOException ex) {
            Logger.getLogger(CalculateMatrixPOStag.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
