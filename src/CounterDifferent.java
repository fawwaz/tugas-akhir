
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Asus
 */
public class CounterDifferent {
    ArrayList<String> left = new ArrayList<>();
    ArrayList<String> right = new ArrayList<>();
    
    public void doReadFile_left(){
        String filename = "tested/CMUTools/POSTagged_gold_standard";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine())!=null){
                left.add(line);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void doReadFile_right(){
        String filename = "tested/CMUTools/POSTagged";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine())!=null){
                right.add(line);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void countDifference(){
        int diff_counter = 0;
        for (int i = 0; i < left.size(); i++) {
            if(!left.get(i).equals(right.get(i))){
                diff_counter++;
            }
        }
        System.out.println("Found Diff : "+diff_counter);
    }
    
    public static void main(String[] args){
        CounterDifferent counter = new CounterDifferent();
        counter.doReadFile_left();
        counter.doReadFile_right();
        counter.countDifference();
    }
}
