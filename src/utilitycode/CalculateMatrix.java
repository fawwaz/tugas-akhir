/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilitycode;

import java.io.BufferedReader;
import java.io.FileReader;
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
        String filename = "experiment_sandbox/testing_merged_"+urutan+".result";
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
        String filename = "experiment_sandbox/testing_merged_"+urutan+".gold_standard";
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
    
    
    public String getTidyMatrix(int[][] conf_matrix, int[] false_positive, int[] false_negative){
        StringBuffer sb = new StringBuffer();
        
        // print header
        sb.append("\t\t");
        for (int i = 0; i < label_standard.size(); i++) {
            sb.append(label_standard.get(i)+"\t");
        }
        sb.append("<-- Tagged As ..");
        sb.append("\n");
        
        for (int i = 0; i < conf_matrix.length; i++) {
            if(i<4||i>5){
                sb.append(label_standard.get(i)+"\t\t"); // cuma biar enak dilihat mata doang
            }else{
                sb.append(label_standard.get(i)+"\t");
            }
            for (int j = 0; j < conf_matrix[i].length; j++) {
                if(j<4||j>5){
                    sb.append(conf_matrix[i][j]+"\t");
                }else{
                    sb.append(conf_matrix[i][j]+"\t\t");
                }
            }
            sb.append(false_negative[i]);
            sb.append("\n");
        }
        
        
        // Print false negative 
        sb.append("\t\t");
        for (int i = 0; i < label_standard.size(); i++) {
            if(i<4||i>5){
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
        sb.append("\t\t"+"Recall"+"\t\t"+"Precission"+"\t"+"F-Measure(F-1)\n");
        for (int i = 0; i < label_standard.size(); i++) {
            float fmeasure = 2*(accuracy[i]*precission[i]) / (accuracy[i]+precission[i]);
            if(i<4||i>5){
                sb.append(label_standard.get(i) + "\t\t" + accuracy[i]+"\t"+precission[i]+"\t"+fmeasure+ "\n");
            }else{
                sb.append(label_standard.get(i) + "\t" + accuracy[i]+"\t"+precission[i]+"\t"+fmeasure+ "\n");
            }
        }
        return sb.toString();
    }
    
    
    
    public void doCalculateMatrix(){
        int total_folds = 2;
        ArrayList<float[]> OverallAccuracy = new ArrayList<>();
        ArrayList<float[]> OverallPrecission = new ArrayList<>();
        
        //for (int i = 0; i < total_folds; i++) {
        int i =1;
            tagged = new ArrayList<>();
            gold_standard = new ArrayList<>();
        
            doReadFile_gold_standard(i);
            doReadFile_result(i);
        
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
            
            System.out.println("Finding case .. ");
            ArrayList<Integer> cases = FindCase("B-Name", "O");
            System.out.println("Finished finding ..case .. ");
            for (int j = 0; j < cases.size(); j++) {
                System.out.println(cases.get(j));
            }
        //}
        float[] summary_accuracy = calculateAverageOverallMetric(OverallAccuracy);
        float[] summary_precission = calculateAverageOverallMetric(OverallPrecission);
        System.out.println("==== Overall System Performance ====");
        System.out.println(getTidyMetric(summary_accuracy, summary_precission));
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
    
    public static void main(String[] args){
        CalculateMatrix calculator = new CalculateMatrix();
        calculator.doCalculateMatrix();
        
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
