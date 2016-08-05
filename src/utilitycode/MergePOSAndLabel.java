/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilitycode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Asus
 */
public class MergePOSAndLabel {
    
    ArrayList<ArrayList<String>> token    = new ArrayList<>();
    ArrayList<ArrayList<String>> label    = new ArrayList<>();
    ArrayList<ArrayList<String>> postag   = new ArrayList<>();
    
    private FileWriter filewriter;
    private PrintWriter writer;
    
    public int total_folds = 2;
    
    public void doReadFile1(){
        String filename = "tested/CMUTools/NER_gold_standard";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            ArrayList<String> temp1 = new ArrayList<>();
            ArrayList<String> temp2 = new ArrayList<>();
            ArrayList<String> temp3 = new ArrayList<>();
            int i =0;
            while((line = br.readLine())!=null){
                if(line.equals("")){
                    token.add(temp1);
                    label.add(temp3);
                    
                    
                    temp1 = new ArrayList<>();
                    temp2 = new ArrayList<>();
                    temp3 = new ArrayList<>();
                }else{
                    System.out.println("["+i+"] "+line);
                    String[] splitted = line.split("\t");
                    temp1.add(splitted[0]);
                    temp2.add(splitted[1]);
                    
                    // Disini kalau bukan other.., cek apakah previousnya adalah sama ? kalau sama berarti I kalau beda berarti b..
                    if(!splitted[1].equals("O")){
                        if(temp2.size()==1){ // kalau perama pasti begin..
                            temp3.add("B-"+getBetterLabel(splitted[1]));
                        }else{
                            // ambil previous nya ...
                            String last_label = temp2.get(temp2.size()-2); // selalu dimulai dari 0
                            
                            if(last_label.equals(splitted[1])){
                                temp3.add("I-"+getBetterLabel(splitted[1]));
                            }else{
                                temp3.add("B-"+getBetterLabel(splitted[1]));
                            }
                        }
                    }else{
                        temp3.add("O");
                    }
                    
                }
                i++;
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    private String getBetterLabel(String oldlabel){
        if(oldlabel.equals("N")){
            return "Name";
        }else if(oldlabel.equals("T")){
            return "Time";
        }else if(oldlabel.equals("L")){
            return "Location";
        }else{
            return "Other>>>>ERRROR<<<<< ";
        }
    }
    
    public void doReadFile2(){
        System.out.println("Reading Gold Standard pos tag...");
        String filename = "tested/CMUTools/POSTagged_gold_standard";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            ArrayList<String> temp2 = new ArrayList<>();
            int i =0;
            while((line = br.readLine())!=null){
                if(line.equals("")){
                    postag.add(temp2);
                    
                    temp2 = new ArrayList<>();
                }else{
                    System.out.println("["+i+"] "+line);
                    String[] splitted = line.split("\t");
                    temp2.add(splitted[1]);
                }
                i++;
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    private void startWriter(String filename) throws IOException{
        filewriter = new FileWriter(filename);
        writer = new PrintWriter(filewriter);
    }
    
    private void closeWriter() throws IOException{
        writer.flush();
        writer.close();
        filewriter.close();
    }
    
    public void doWriting() throws IOException{
        startWriter("tested/CMUTools/merged");
        for (int i = 0; i < label.size(); i++) {
            for (int j = 0; j < label.get(i).size(); j++) {
                String _token = token.get(i).get(j);
                String _postag = postag.get(i).get(j);
                String _label = label.get(i).get(j);
                writer.write(_token+"\t"+_postag+"\t"+_label+"\n"); // gak sesuai format mallet, harus dipisah spasi
            }
            writer.write("\n");
        }
        closeWriter();
    }
    
    public void doWritingTraining() throws IOException{
        // aasumsi jml data ada 17 folds = 3 base = 5 start 1 0 end 1 5
        
        int base = label.size() / total_folds;
        for (int i = 0; i < total_folds; i++) {
            int start_index = i*base;
            int end_index = (i+1) * base-1; // karena angka selalu dimulai dari 0
            
            startWriter("tested/CMUTools/training_merged_"+i+".training");
            for (int k = 0; k < label.size(); k++) {
                
                // kalau k diluar itu semua baru ditulis sebagai training data..
                if (!(start_index<k && k <= end_index)){
                    
                    for (int j = 0; j < label.get(k).size(); j++) {
                        String _token = token.get(k).get(j);
                        String _postag = postag.get(k).get(j);
                        String _label = label.get(k).get(j);
                        writer.write(_token + " " + _postag + " " + _label + "\n");
                    }
                    writer.write("\n");
                
                }
                
            }
            closeWriter();
        }
    }
    
    public void doWritingTesting() throws IOException{
        // aasumsi jml data ada 17 folds = 3 base = 5 start 1 0 end 1 5

        int base = label.size() / total_folds;
        for (int i = 0; i < total_folds; i++) {
            int start_index = i*base;
            int end_index = (i+1) * base-1; // karena angka selalu dimulai dari 0
            
            startWriter("tested/CMUTools/testing_merged_"+i+".untagged");
            for (int k = 0; k < label.size(); k++) {
                
                // kalau k didalam itu semua justru malah ditulis tapi tanpa label
                if (start_index<k && k <= end_index){
                    
                    for (int j = 0; j < label.get(k).size(); j++) {
                        String _token = token.get(k).get(j);
                        String _postag = postag.get(k).get(j);
                        String _label = label.get(k).get(j);
                        writer.write(_token + " " + _postag + "\n");
                    }
                    writer.write("\n");
                
                }
                
            }
            closeWriter();
        }
    }
    
    public void doWritingGoldStandard() throws IOException{
        
        int base = label.size() / total_folds;
        for (int i = 0; i < total_folds; i++) {
            int start_index = i*base;
            int end_index = (i+1) * base-1; // karena angka selalu dimulai dari 0
            
            startWriter("tested/CMUTools/testing_merged_"+i+".gold_standard");
            for (int k = 0; k < label.size(); k++) {
                
                // kalau k didalam itu semua justru malah ditulis tapi tanpa label
                if (start_index<k && k <= end_index){
                    
                    for (int j = 0; j < label.get(k).size(); j++) {
                        String _token = token.get(k).get(j);
                        String _postag = postag.get(k).get(j);
                        String _label = label.get(k).get(j);
                        writer.write(_label + "\n");
                    }
                    writer.write("\n");
                
                }
                
            }
            closeWriter();
        }
        
    }
    
    public static void main(String[] args){
        MergePOSAndLabel merger = new MergePOSAndLabel();
        merger.doReadFile1();
        merger.doReadFile2();
        try {
            //merger.doWriting();
            merger.doWritingTraining();
            merger.doWritingTesting();
            merger.doWritingGoldStandard();
        } catch (IOException ex) {
            Logger.getLogger(MergePOSAndLabel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
