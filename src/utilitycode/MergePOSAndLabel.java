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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    public HashMap<String, Integer> word_counter = new HashMap<>();
    
    private FileWriter filewriter;
    private PrintWriter writer;
    
    public int total_folds = 4;
    
    String  string_pat_capital = "[A-Z]+";
    Pattern pattern_capital = Pattern.compile(string_pat_capital);
    
    ArrayList<String> gazetteer_nama = new ArrayList<>();
    ArrayList<String> gazetteer_prev_nama = new ArrayList<>();
    ArrayList<String> gazetteer_loc = new ArrayList<>();
    
    // Old features .. 
    HashMap<String,String> kateglo_postag = new HashMap<>();
    ArrayList<String> old_gazzetteer_loc = new ArrayList<>();
    
    
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
                    //System.out.println("["+i+"] "+line);
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
                    //System.out.println("["+i+"] "+line);
                    
                    String[] splitted = line.split("\t");
                    temp2.add(splitted[1]);
                    // Statisticnya didisabled dulu
                    /*
                    if(!(splitted[0].matches("[0-9]+")||splitted[0].matches(Twokenize.url)||splitted[0].matches("@[A-z0-9_.]+")||splitted[0].matches("#[A-z0-9_.]+"))){
                        String candidate = splitted[0].toLowerCase().replaceAll("[AIUEOaiueo]", "");
                        int count = word_counter.containsKey(candidate) ? word_counter.get(candidate) : 0;
                        word_counter.put(candidate, count+1);
                    }
                    */
                }
                i++;
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void doReadFileGazetteer(int iteration){
        gazetteer_nama = new ArrayList<>();
        gazetteer_loc = new ArrayList<>();
        
        System.out.println("Reading Gazetteer data for iteration "+iteration);
        
        // READ NAMA DULU
        String filename = "tested/CMUTools/gazetteer_name_"+iteration;
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine())!=null){
                if(line.equals("")){
                    
                }else{
                    String[] splitted = line.split("\t");
                    String token = splitted[0].trim();
                    gazetteer_nama.add(token);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        
        // READ Previous name gazetteer
        filename = "tested/CMUTools/gazetteer_prev_name_"+iteration;
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine())!=null){
                if(line.equals("")){
                    
                }else{
                    String[] splitted = line.split("\t");
                    String token = splitted[0].trim();
                    gazetteer_prev_nama.add(token);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        
        filename = "tested/CMUTools/gazetteer_location_"+iteration;
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine())!=null){
                if(line.equals("")){
                    
                }else{
                    String[] splitted = line.split("\t");
                    String token = splitted[0].trim();
                    gazetteer_loc.add(token);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void doReadOldPOSTAG(){
        String filename = "tested/CMUTools/kateglo_postag";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine())!=null){
                if(line.equals("")){
                    
                }else{
                    String[] splitted = line.split("\t");
                    String token = splitted[0].trim();
                    String tag = splitted[1].trim();
                    
                    // Pisahin dulu.. sesuai dengan tagnya...
                    String real_tag = "";
                    if(tag.equals("Nomina")){
                        real_tag = "N";
                    }else if(tag.equals("Verba")){
                        real_tag = "V";
                    }else if(tag.equals("Adjektiva")){
                        real_tag = "A";
                    }else if(tag.equals("Adverbia")){
                        real_tag = "R";
                    }else if(tag.equals("Preposisi")){
                        real_tag = "P";
                    }else if(tag.equals("Interjeksi")){
                        real_tag = "!";
                    }else if(tag.equals("Konjungsi")){
                        real_tag = "P"; // kebanyakan kongjungsi adalah Sub coor conjunction
                    }else if(tag.equals("Pronomina")){
                        real_tag = "O";
                    }else if(tag.equals("Lain-lain")){
                        //real_tag = "A";
                    }else if(tag.equals("Numeralia")){
                        real_tag = "$";
                    }
                    
                    if(!real_tag.equals("")){
                        kateglo_postag.put(token,real_tag);
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void doReadOldGazetteer(){
        String filename = "tested/CMUTools/kateglo_postag";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine())!=null){
                if(line.equals("")){
                    
                }else{
                    String location = line.trim();
                    old_gazzetteer_loc.add(location);
                }
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
            
            System.out.println("Writing: "+"tested/CMUTools/training_merged_"+i+".training");
            System.out.println("Start_index = "+start_index);
            System.out.println("End_index = "+end_index);
            int counter = 0;
            
            // disable dulu untuk old experiment
            doReadFileGazetteer(i);
            
            startWriter("tested/CMUTools/training_merged_"+i+".training");
            for (int k = 0; k < label.size(); k++) {
                
                // kalau k diluar itu semua baru ditulis sebagai training data..
                if (!(start_index<k && k <= end_index)){
                    counter++;
                    for (int j = 0; j < label.get(k).size(); j++) {
                        String _token = token.get(k).get(j);
                        String _postag = postag.get(k).get(j);
                        
                        String _label = label.get(k).get(j);
                        
                        StringBuffer sb = new StringBuffer();
                        // Fitur 1 : Leksikal
                        //sb.append(_token.toLowerCase().replaceAll("[AIUEOaiueo]", ""));
                        sb.append(_token);
                        
                        
                        // Fitur 1.1 : Leksikal sebelum 
                        if(j>0){
                            String _prev_token = token.get(k).get(j-1);
                            sb.append(" "+_prev_token);
                        }else{
                            sb.append(" <STARTTAG>");
                        }
                        /*
                        // Fitur 1.2 : Leksikal double sebelum 
                        if(j>1){
                            String _prev_token = token.get(k).get(j-2);
                            sb.append(" "+_prev_token);
                        }else{
                            sb.append(" <STARTTAG>");
                        }
                        /**/
                        /*
                        // Fitur 1.3 : Leksikal after
                        if(j<label.get(k).size()-1){
                            String _next_token = token.get(k).get(j+1);
                            sb.append(" "+_next_token);
                        }else{
                            sb.append(" <ENDTAG>");
                        }
                        // Fitur 1.4 : Leksikal double after
                        if(j<label.get(k).size()-2){
                            String _next_token = token.get(k).get(j+2);
                            sb.append(" "+_next_token);
                        }else{
                            sb.append(" <ENDTAG2>");
                        }
                        /**/
                        // Fitur 2 : POS_Tag
                        sb.append(" "+_postag);
                        /*
                        //Fitur 2.1 : POS_Tag Previous
                        if(j>0){
                            String _prev_tag = postag.get(k).get(j-1);
                            sb.append(" Prev"+_prev_tag);
                        }else{
                            sb.append(" <STARTTAG>");
                        }
                        
                        // Fitur 2.2: Post_Tag double previouse
                        if(j>1){
                            String _prev_tag = postag.get(k).get(j-2);
                            sb.append(" Prev2"+_prev_tag);
                        }else{
                            sb.append(" <STARTTAG2>");
                        }
                        /**/
                        /*
                        // Fitur 2.3 : Pos tag after
                        if(j<label.get(k).size()-1){
                            String _next_tag = postag.get(k).get(j+1);
                            sb.append(" Next"+_next_tag);
                        }else{
                            sb.append(" <ENDTAG>");
                        }
                        
                        // Fitur 2.4 : Pos tag double after
                        if(j<label.get(k).size()-2){
                            String _next_tag = postag.get(k).get(j+2);
                            sb.append(" Next2"+_next_tag);
                        }else{
                            sb.append(" <ENDTAG2>");
                        }
                        /**/
                        //sb.append(_postag);
                        // Fitur 3 : Kapitalisasi
                        if(_token.matches(string_pat_capital)){
                            sb.append(" IS_CAPITAL");
                        }
                        
                        // Fitur 4 : Posisi 
                        float position = (float) j / (float) label.get(k).size();
                        if(position<0.25){
                            sb.append(" FIRST_SEGMENT");
                        }else if( (position>=0.25) && (position<0.5) ){
                            sb.append(" SECOND_SEGMENT");
                        }else if( (position>=0.5) && (position<0.75) ){
                            sb.append(" THIRD_SEGMENT");
                        }else if( (position>=0.75) && (position<=1.0) ){
                            sb.append(" FOURTH_SEGMENT");
                        }
                        
                        // Fitur 5 : List Name
                        if(gazetteer_nama.contains(_token)){
                            sb.append(" NAMA_EVENT");
                        }
                        
                        // Fitur 5.1 : Previous Nama Event
                        if(gazetteer_prev_nama.contains(_token)){
                            sb.append(" PREV_NAMA_EVENT");
                        }
                        
                        if(gazetteer_loc.contains(_token)){
                            sb.append(" LOKASI_EVENT");
                        }
                        /**/
                        
                        // ----------------------------------------------
                        // OLD EXPERIMENT... 
                        
                        
                        /*
                        // Fitur 1 Old : Lookup PosTag..
                        if(kateglo_postag.containsKey(_token.toLowerCase())){
                            sb.append(" "+kateglo_postag.get(_token.toLowerCase()));
                        }
                        
                        // Fitur 2 : regex Pattern
                        if(_token.matches("\\d+")){
                            sb.append(" ISNUMBER");
                        }
                        
                        if(_token.matches("\\p{P}")){
                            sb.append(" ISPUNCTUATION");
                        }
                        
                        if(_token.matches("(di|@|d|ke|k)")){
                            sb.append(" ISPLACEDIRECTIVE");
                        }
                        
                        if(_token.matches(Twokenize.url)){
                            sb.append(" ISURL");
                        }
                        
                        if(_token.matches(Twokenize.AtMention)){
                            sb.append(" ISMENTION");
                        }
                        
                        if(_token.matches(Twokenize.Hashtag)){
                            sb.append(" ISHASHTAG");
                        }
                        
                        if(_token.matches(old_method.Twokenize.varian_bulan)){
                            sb.append(" ISMONTHNAME");
                        }
                        
                        if(j>0 && j<token.get(k).size()-1){
                            String previous_token = token.get(k).get(j-1);
                            String next_token = token.get(k).get(j+1);
                            
                            if(previous_token.matches("\\d+") && _token.matches("[/\\-]") && next_token.matches("\\d+")){
                                sb.append(" ISDATESEPARATOR");
                            }
                            
                            if(kateglo_postag.containsKey(previous_token.toLowerCase())){
                                sb.append(" Prev"+kateglo_postag.get(previous_token.toLowerCase()));
                            }
                            
                            if(kateglo_postag.containsKey(next_token.toLowerCase())){
                                sb.append(" Next"+kateglo_postag.get(next_token.toLowerCase()));
                            }
                        }
                        /**/
                        
                        
                        // Karena untuk training
                        sb.append(" "+_label);
                        sb.append("\n");
                        writer.write(sb.toString());
                        
                    }
                    writer.write("\n");
                
                }
                
            }
            closeWriter();
            System.out.println("Size:  = "+(counter));
            System.out.println("===");
        }
    }
    
    public void doWritingTesting() throws IOException{
        // aasumsi jml data ada 17 folds = 3 base = 5 start 1 0 end 1 5

        int base = label.size() / total_folds;
        for (int i = 0; i < total_folds; i++) {
            int start_index = i*base;
            int end_index = (i+1) * base-1; // karena angka selalu dimulai dari 0
            
            System.out.println("Writing: "+"tested/CMUTools/testing_merged_"+i+".untagged");
            System.out.println("Start_index = "+start_index);
            System.out.println("End_index = "+end_index);
            int counter = 0;
            
            
            // Di disable dulu untuk old experiment
            doReadFileGazetteer(i); // gazetteer
            
            startWriter("tested/CMUTools/testing_merged_"+i+".untagged");
            for (int k = 0; k < label.size(); k++) {
                
                // kalau k didalam itu semua justru malah ditulis tapi tanpa label
                if (start_index<k && k <= end_index){
                    counter++;
                    for (int j = 0; j < label.get(k).size(); j++) {
                        String _token = token.get(k).get(j);
                        String _postag = postag.get(k).get(j);
                        String _label = label.get(k).get(j);
                        
                        StringBuffer sb = new StringBuffer();
                        // Fitur 1 : Leksikal
                        //sb.append(_token.toLowerCase().replaceAll("[AIUEOaiueo]", ""));
                        sb.append(_token);
                        
                        // Fitur 1.2 : Leksikal sebelum 
                        if(j>0){
                            String _prev_token = token.get(k).get(j-1);
                            sb.append(" "+_prev_token);
                        }else{
                            sb.append(" <STARTTAG>");
                        }
                        /*
                        // Fitur 1.3 : Leksikal double sebelum 
                        if(j>1){
                            String _prev_token = token.get(k).get(j-2);
                            sb.append(" "+_prev_token);
                        }else{
                            sb.append(" <STARTTAG>");
                        }
                        /**/
                        /*
                        // Fitur 1.3 : Leksikal after
                        if(j<label.get(k).size()-1){
                            String _next_token = token.get(k).get(j+1);
                            sb.append(" "+_next_token);
                        }else{
                            sb.append(" <ENDTAG>");
                        }
                        
                        // Fitur 1.3 : Leksikal double after
                        if(j<label.get(k).size()-2){
                            String _next_token = token.get(k).get(j+2);
                            sb.append(" "+_next_token);
                        }else{
                            sb.append(" <ENDTAG2>");
                        }
                        
                        
                        // Fitur 2 : POS_Tag
                        sb.append(" "+_postag);
                        //Fitur 2.1 : POS_Tag Previous
                        /*
                        if(j>0){
                            String _prev_tag = postag.get(k).get(j-1);
                            sb.append(" Prev"+_prev_tag);
                        }else{
                            sb.append(" <STARTTAG>");
                        }
                        
                        // Fitur 2.2: Post_Tag double previouse
                        if(j>1){
                            String _prev_tag = postag.get(k).get(j-2);
                            sb.append(" Prev2"+_prev_tag);
                        }else{
                            sb.append(" <STARTTAG2>");
                        }
                        /**/
                        /*
                        // Fitur 2.3 : Pos tag after
                        if(j<label.get(k).size()-1){
                            String _next_tag = postag.get(k).get(j+1);
                            sb.append(" Next"+_next_tag);
                        }else{
                            sb.append(" <ENDTAG>");
                        }
                        
                        // Fitur 2.4 : Pos tag double after
                        if(j<label.get(k).size()-2){
                            String _next_tag = postag.get(k).get(j+2);
                            sb.append(" Next2"+_next_tag);
                        }else{
                            sb.append(" <ENDTAG2>");
                        }
                        /**/
                        //sb.append(_postag);
                        // Fitur 3 : Kapitalisasi
                        if(_token.matches(string_pat_capital)){
                            sb.append(" IS_CAPITAL");
                        }
                        
                        // Fitur 4 : Posisi Relatif
                        float position = (float) j / (float) label.get(k).size();
                        if(position<0.25){
                            sb.append(" FIRST_SEGMENT");
                        }else if( (position>=0.25) && (position<0.5) ){
                            sb.append(" SECOND_SEGMENT");
                        }else if( (position>=0.5) && (position<0.75) ){
                            sb.append(" THIRD_SEGMENT");
                        }else if( (position>=0.75) && (position<=1.0) ){
                            sb.append(" FOURTH_SEGMENT");
                        }
                        
                        // Fitur 5 : Gazetteer
                        if(gazetteer_nama.contains(_token)){
                            sb.append(" NAMA_EVENT");
                        }
                        
                        // Fitur 5.1 : Previous Nama Event
                        if(gazetteer_prev_nama.contains(_token)){
                            sb.append(" PREV_NAMA_EVENT");
                        }
                        
                        if(gazetteer_loc.contains(_token)){
                            sb.append(" LOKASI_EVENT");
                        }
                        /**/
                        
                        
                        // -----------------
                        // OLD EXPERIMENT
                        /*
                        // Fitur 1 Old : Lookup PosTag..
                        if(kateglo_postag.containsKey(_token.toLowerCase())){
                            sb.append(" "+kateglo_postag.get(_token.toLowerCase()));
                        }
                        
                        // Fitur 2 : regex Pattern
                        if(_token.matches("\\d+")){
                            sb.append(" ISNUMBER");
                        }
                        
                        if(_token.matches("\\p{P}")){
                            sb.append(" ISPUNCTUATION");
                        }
                        
                        if(_token.matches("(di|@|d|ke|k)")){
                            sb.append(" ISPLACEDIRECTIVE");
                        }
                        
                        if(_token.matches(Twokenize.url)){
                            sb.append(" ISURL");
                        }
                        
                        if(_token.matches(Twokenize.AtMention)){
                            sb.append(" ISMENTION");
                        }
                        
                        if(_token.matches(Twokenize.Hashtag)){
                            sb.append(" ISHASHTAG");
                        }
                        
                        if(_token.matches(old_method.Twokenize.varian_bulan)){
                            sb.append(" ISMONTHNAME");
                        }
                        
                        if(j>0 && j<token.get(k).size()-1){
                            String previous_token = token.get(k).get(j-1);
                            String next_token = token.get(k).get(j+1);
                            
                            if(previous_token.matches("\\d+") && _token.matches("[/\\-]") && next_token.matches("\\d+")){
                                sb.append(" ISDATESEPARATOR");
                            }
                            
                            if(kateglo_postag.containsKey(previous_token.toLowerCase())){
                                sb.append(" Prev"+kateglo_postag.get(previous_token.toLowerCase()));
                            }
                            
                            if(kateglo_postag.containsKey(next_token.toLowerCase())){
                                sb.append(" Next"+kateglo_postag.get(next_token.toLowerCase()));
                            }
                        }
                        /**/
                        // Tanpa label karena untuk testing..
                        sb.append("\n");
                        writer.write(sb.toString());
                    }
                    writer.write("\n");
                
                }
                
            }
            closeWriter();
            
            System.out.println("Size:  = "+(end_index-start_index));
            System.out.println("===");
        }
    }
    
    public void doWritingGoldStandard() throws IOException{
        
        int base = label.size() / total_folds;
        for (int i = 0; i < total_folds; i++) {
            int start_index = i*base;
            int end_index = (i+1) * base-1; // karena angka selalu dimulai dari 0
            
            System.out.println("Writing: "+"tested/CMUTools/testing_merged_"+i+".gold_standard");
            System.out.println("Start_index = "+start_index);
            System.out.println("End_index = "+end_index);
            int counter = 0;
            
            startWriter("tested/CMUTools/testing_merged_"+i+".gold_standard");
            for (int k = 0; k < label.size(); k++) {
                
                // kalau k didalam itu semua justru malah ditulis tapi tanpa label
                if (start_index<k && k <= end_index){
                    counter++;
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
            System.out.println("Size:  = "+(end_index-start_index));
            System.out.println("===");
        }
        
    }
    
    
    public void calculateStatistic() throws IOException{
        FileWriter writer = new FileWriter(new File("Statistic"));
        
        List keys = new ArrayList(word_counter.keySet());
        final Map count_to_compare = word_counter;
        Collections.sort(keys,new Comparator(){
            public int compare(Object left, Object right){
                String LeftKey = (String) left;
                String RightKey = (String) right;
                Integer LeftValue = (Integer) count_to_compare.get(LeftKey);
                Integer RightValue = (Integer) count_to_compare.get(RightKey);
                if(LeftValue.compareTo(RightValue)==0){
                    return LeftKey.compareTo(RightKey);
                }else{
                    return LeftValue.compareTo(RightValue);
                }
            }
        });
        for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
            Object next = iterator.next();
            System.out.println(next + " "+word_counter.get(next));
            writer.write(next + "\t" + word_counter.get(next) +"\n");
        }
        
        writer.close();
    }
    
    public static void main(String[] args){
        MergePOSAndLabel merger = new MergePOSAndLabel();
        merger.doReadOldPOSTAG();
        merger.doReadOldGazetteer();
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
        
        
        // Kalkulasi statistik saja..
        /*
        try{
            merger.calculateStatistic();
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("Word Counter: "+merger.word_counter.size());
        */        
    }
}
