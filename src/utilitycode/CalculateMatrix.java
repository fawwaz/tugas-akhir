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
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author Asus
 */
public class CalculateMatrix {
    ArrayList<String> label_standard;
    ArrayList<ArrayList<String>> tagged = new ArrayList<>();
    ArrayList<ArrayList<String>> gold_standard = new ArrayList<>();
    ArrayList<Pair> original_pair = new ArrayList<>();
    ArrayList<String> original_sentence = new ArrayList<>();
    ArrayList<String> original_token = new ArrayList<>();
    public String foldername = "experiment_1";
    //public int sub_iteration = 2; // 0 1 2 saja .
    //public int current_urutan = 3;
    // public String foldername = "incrimental_learning_1/";
    public int total_folds = 4;
    
    public CalculateMatrix(){
        label_standard = new ArrayList<>();
        label_standard.add("B-Name");
        label_standard.add("I-Name");
        label_standard.add("B-Time");
        label_standard.add("I-Time");
        label_standard.add("B-Location");
        label_standard.add("I-Location");
        label_standard.add("O");
    }
    
    public void doReadFile_result(int urutan){
        System.out.println("Reading Tagged Result");
        String filename = foldername+"/testing_merged_"+urutan+".result";
        //String filename = foldername+"incrimental_iteration_"+urutan+"/testing_merged_sub_iteration_"+sub_iteration+".result";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            ArrayList<String> temp2 = new ArrayList<>();
            int i =0;
            while((line = br.readLine())!=null){
                if(line.equals("")){
                    tagged.add(temp2);
                    temp2 = new ArrayList<>();
                }else{
                    //System.out.println(line);
                    temp2.add(line);
                }
                i++;
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    
    public void doReadFile_gold_standard(int urutan){
        System.out.println("Reading Tagged Gold Standard");
        String filename = foldername+"/testing_merged_"+urutan+".gold_standard";
        //String filename = foldername+"incrimental_iteration_"+urutan+"/testing_merged_sub_iteration_"+sub_iteration+".gold_standard";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            ArrayList<String> temp2 = new ArrayList<>();
            int i =0;
            while((line = br.readLine())!=null){
                if(line.equals("")){
                    gold_standard.add(temp2);
                    temp2 = new ArrayList<>();
                }else{
                    //System.out.println(line);
                    temp2.add(line);
                }
                i++;
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    // Untuk bikin pair.. sekaligus original wordnya sih.. token apa gitu..
    public void doReadFile_original(int urutan){
        System.out.println("Reading Tagged Original file");
        String filename = foldername+"/testing_merged_"+urutan+".untagged";
        //String filename = foldername+"incrimental_iteration_"+urutan+"/testing_merged_sub_iteration_"+sub_iteration+".untagged";
        Pair p = new Pair();
        p.start_index = 0;
        StringBuffer sb = new StringBuffer();
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            int i =0;
            while((line = br.readLine())!=null){
                String[] splitted = line.split(" ");
                if(line.equals("")){
                    original_sentence.add(sb.toString());
                    sb = new StringBuffer();
                    original_pair.add(p);
                    p = new Pair();
                    p.start_index = i+1;
                }else{
                    p.end_index = i;
                    sb.append(splitted[0]+" "); // 0 artinya token..
                }
                original_token.add(splitted[0]);
                i++;
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public int getLabelIndex(String label){
        return label_standard.indexOf(label);
    }
    
    // actual di kiri predicted di atas .
    private int[][] calculateMatrixSingle(ArrayList<String> _standard,ArrayList<String> _tagged){
        int[][] retval = new int[label_standard.size()][label_standard.size()];
        for (int i = 0; i < _tagged.size(); i++) {
            int standard_index = getLabelIndex(_standard.get(i));
            int tagged_index = getLabelIndex(_tagged.get(i));
            retval[standard_index][tagged_index]++;
        }
        return retval;
    }
    
    private int[] calculateFalseNegative(int[][] conf_matrix){
        int[] retval = new int[label_standard.size()];
        for (int i = 0; i < conf_matrix.length; i++) {
            for (int j = 0; j < conf_matrix[i].length; j++) {
                retval[i] = retval[i] + conf_matrix[i][j];
            }
        }
        return retval;
    }
    
    private int[] calculateFalsePositive(int[][] conf_matrix){
        int[] retval = new int[label_standard.size()];
        for (int i = 0; i < conf_matrix.length; i++) {
            for (int j = 0; j < conf_matrix[i].length; j++) {
                retval[j] = retval[j] + conf_matrix[i][j];
            }
        }
        return retval;
    }
    
    private float[] calculateRecall(int[][] conf_matrix,int[] false_negative){
        float[] retval = new float[label_standard.size()];
        for (int i = 0; i < retval.length; i++) {
            retval[i] = (float)conf_matrix[i][i] / (float)false_negative[i];
        }
        return retval;
    }
    
    private float[] calculatePrecission(int[][] conf_matrix,int[] false_positive){
        float[] retval = new float[label_standard.size()];
        for (int i = 0; i < retval.length; i++) {
            retval[i] = (float)conf_matrix[i][i] / (float)false_positive[i];
        }
        return retval;
    }
    
    private int[][] calculateMatrix(ArrayList<ArrayList<String>> _standard, ArrayList<ArrayList<String>> _tagged) {
        int[][] retval = new int[label_standard.size()][label_standard.size()];
        for (int i = 0; i < _tagged.size(); i++) {
            for (int j = 0; j < _tagged.get(i).size(); j++) {
                //System.out.print("Standard label is : "+_standard.get(i).get(j)+"\t");
                //System.out.println("Tagged label is : "+_tagged.get(i).get(j));
                int standard_index = getLabelIndex(_standard.get(i).get(j).trim());
                int tagged_index = getLabelIndex(_tagged.get(i).get(j).trim());
                
                retval[standard_index][tagged_index]++;
            }
        }
        return retval;
    }
    
    private float[] calculateAverageOverallMetric(ArrayList<float[]> overall_array){
        float[] retval = new float[label_standard.size()];
        for (int i = 0; i < overall_array.size(); i++) {
            for (int j = 0; j < overall_array.get(i).length; j++) {
                retval[j] = (float) retval[j] + (float) overall_array.get(i)[j];
            }
        }
        
        for (int i = 0; i < label_standard.size(); i++) {
            retval[i] = (float) retval[i] / (float)overall_array.size();
        }
        
        return retval;
    }
    
    private int findInBetween(int nomor_line){
        for (int i = 0; i < original_pair.size(); i++) {
            Pair p = original_pair.get(i);
//            System.out.print("Nomor line is : "+nomor_line+"\t");
//            System.out.print(p.start_index+"\t");
//            System.out.println(p.end_index);
            if((p.start_index<=nomor_line)&&(nomor_line<=p.end_index)){
                return i;
            }
        }
        return 0; // by default kalau gak ada sama sekali..
    }
    
    
    public String getTidyMatrix(int[][] conf_matrix, int[] false_positive, int[] false_negative){
        StringBuffer sb = new StringBuffer();
        
        // print header
        sb.append("\t\t\t");
        for (int i = 0; i < label_standard.size(); i++) {
            sb.append(label_standard.get(i)+"\t");
        }
        sb.append("<-- Tagged As ..");
        sb.append("\n");
        
        for (int i = 0; i < conf_matrix.length; i++) {
            if(i==4||i==5){
                sb.append(label_standard.get(i)+"\t"); // cuma biar enak dilihat mata doang
            }else if(i==6){
                sb.append(label_standard.get(i)+"\t\t\t"); 
            }else{
                sb.append(label_standard.get(i)+"\t\t");
            }
            for (int j = 0; j < conf_matrix[i].length; j++) {
                if(j==4||j==5){
                    sb.append(conf_matrix[i][j]+"\t\t\t");
                }else{
                   sb.append(conf_matrix[i][j]+"\t\t"); 
                }
            }
            sb.append(false_negative[i]);
            sb.append("\n");
        }
        
        
        // Print false negative 
        sb.append("\t\t\t");
        for (int i = 0; i < label_standard.size(); i++) {
            if(i==4||i==5||i==1){
                sb.append(false_positive[i]+"\t");
            }else{
                sb.append(false_positive[i]+"\t\t");
            }
        }
        sb.append("\n");
        
        sb.append("   ^\n");
        sb.append("   |\n");
        sb.append("standard\n");
        return sb.toString();
    }
    
    public String getTidyMetric(float[] accuracy,float[] precission){
        StringBuffer sb = new StringBuffer();
        sb.append("\t\t\t\t"+"Recall"+"\t\t"+"Precission"+"\t"+"F-Measure(F-1)\n");
        for (int i = 0; i < label_standard.size(); i++) {
            float fmeasure = 2*(accuracy[i]*precission[i]) / (accuracy[i]+precission[i]);
            if(i==4||i==5){
                sb.append(label_standard.get(i) + "\t\t" + accuracy[i]+"\t"+precission[i]+"\t"+fmeasure+ "\n");
            }else if(i==6){
                sb.append(label_standard.get(i) + "\t\t\t\t" + accuracy[i]+"\t"+precission[i]+"\t"+fmeasure+ "\n");
            }else{
                sb.append(label_standard.get(i) + "\t\t\t" + accuracy[i]+"\t"+precission[i]+"\t"+fmeasure+ "\n");
            }
        }
        return sb.toString();
    }
    
    
    
    public void doCalculateMatrix() throws IOException{
        FileWriter writer = new FileWriter(new File(foldername+"/rekap_"+foldername));
        FileWriter writer2 = new FileWriter(new File(foldername+"/rekap_failed_to_recognize_"+foldername));
        // int i = current_urutan;
        // int urutan = i;
        // FileWriter writer = new FileWriter(new File(foldername+"incrimental_iteration_"+urutan+"/rekap_sub_iteration_"+sub_iteration));
        // FileWriter writer2 = new FileWriter(new File(foldername+"incrimental_iteration_"+urutan+"/rekap_failed_to_recognize_sub_iteration_"+sub_iteration));
        ArrayList<float[]> OverallAccuracy = new ArrayList<>();
        ArrayList<float[]> OverallPrecission = new ArrayList<>();
        
        for (int i = 0; i < total_folds; i++) {
        //int i =1; // urutan
            tagged = new ArrayList<>();
            gold_standard = new ArrayList<>();
            original_pair = new ArrayList<>();
            original_token = new ArrayList<>();
            original_sentence = new ArrayList<>();
        
            doReadFile_gold_standard(i);
            doReadFile_result(i);
            doReadFile_original(i);
            //PrintOriginalDatas();
        
            int[][] hasil = calculateMatrix(gold_standard, tagged);
            int[] false_postive = calculateFalsePositive(hasil);
            int[] false_negative = calculateFalseNegative(hasil);
            float[] accuracy = calculateRecall(hasil, false_negative);
            float[] precission = calculatePrecission(hasil, false_postive);
            
            OverallAccuracy.add(accuracy);
            OverallPrecission.add(precission);
            
            System.out.println("Confusion matrix is : ");
            System.out.println(getTidyMatrix(hasil,false_postive,false_negative));
            System.out.println(getTidyMetric(accuracy, precission));
            
            writer.write("\n\n\n -------- Confusion matrix for fold : "+i+" --------");
            writer.write("\n");
            writer.write(getTidyMatrix(hasil,false_postive,false_negative));
            writer.write("\n");
            writer.write(getTidyMetric(accuracy, precission));
            writer.write("\n");
            
            System.out.println("Finding case .. ");
            writer2.write("============= Iteration : "+i+ "=============\n");
            for (int j = 0; j < label_standard.size(); j++) {
                for (int k = 0; k < label_standard.size(); k++) {
                    String Supposed_lbel = label_standard.get(j);
                    String System_lbel = label_standard.get(k);
                    ArrayList<Integer> cases = FindCase(Supposed_lbel, System_lbel);
                    writer2.write("Supposed(Tagged As) : "+Supposed_lbel+"("+System_lbel+")\n\n");
                    
                    writeDataFailedToRekap(writer2, cases);
                    /*
                    for (int l = 0; l < cases.size(); l++) {
                        writer2.write(cases.get(l)+"\n");
                    }
                    */
                }
            }
            /*
            ArrayList<Integer> cases = FindCase("B-Name", "O");
            System.out.println("Finished finding ..case .. ");
            for (int j = 0; j < cases.size(); j++) {
                System.out.println(cases.get(j));
            }
            /**/
        }
        float[] summary_accuracy = calculateAverageOverallMetric(OverallAccuracy);
        float[] summary_precission = calculateAverageOverallMetric(OverallPrecission);
        System.out.println("==== Overall System Performance ====");
        System.out.println(getTidyMetric(summary_accuracy, summary_precission));
        
        writer.write("\n\n\n ====================================\n");
        writer.write(" ==== Overall System Performance ====\n");
        writer.write(" ====================================\n");
        writer.write(getTidyMetric(summary_accuracy, summary_precission));
        writer.write("\n");
        
        writer.close();
        writer2.close();
    }
    
    public void PrintOriginalDatas(){
        for(int i=0; i<original_pair.size(); i++){
            System.out.println("["+i+"]"+original_pair.get(i).start_index+" "+original_pair.get(i).end_index+"\t"+original_sentence.get(i));
        }
    }
    
    private void writeDataFailedToRekap(FileWriter writer,ArrayList<Integer> cases) throws IOException{
        writer.write("\n");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < cases.size(); i++) {
            int nomor_urut = findInBetween(cases.get(i));
            String kata = original_token.get(cases.get(i)-1);
            sb.append(kata);
            
            // panjang kata.. biar enak diprint aja..
            int tab_repeat = 5 - (kata.length() / 4);
            for (int j = 0; j < tab_repeat; j++) {
                sb.append("\t");
            }
            sb.append("\t\t\t");
            sb.append(original_sentence.get(nomor_urut)+"\n");
            writer.write(sb.toString());
            sb.delete(0,sb.length());
        }
        writer.write("\n\n");
    }
    
    public ArrayList<Integer> FindCase(String label_standard, String label_tagged){
        ArrayList<Integer> retval = new ArrayList<>();
        int line_counter = 0;
        for (int i = 0; i < tagged.size(); i++) {
            for (int j = 0; j < tagged.get(i).size(); j++) {
                line_counter++;
                //System.out.println(tagged.get(i).get(j) + " <-- tagged | original --> " + gold_standard.get(i).get(j));
                if(tagged.get(i).get(j).trim().equals(label_tagged) && gold_standard.get(i).get(j).trim().equals(label_standard)){
                    int current_line = line_counter+i;
                    retval.add(current_line);
                }
            }
        }
        return retval;
    }
    
    private class Pair{
        public int start_index;
        public int end_index;
    }
    
    public static void main(String[] args){
        CalculateMatrix calculator = new CalculateMatrix();
        try{
            calculator.doCalculateMatrix();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        /*
        Process p;
        try{
            p = Runtime.getRuntime().exec("ping -n 3 https://google.com");
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            
            while((line = reader.readLine())!=null){
                System.out.println(line);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
                */
    }

}

