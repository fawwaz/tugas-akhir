/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilitycode;

import cc.mallet.fst.CRF;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Asus
 */
public class PrintCRFModel {
    String folder_name = "experiment_8";
    int num_folds = 4;
    public void doReadModel(int iteration) throws IOException, ClassNotFoundException{
        String filename = folder_name+"/iteration_"+iteration+".model";
        String output_filename = folder_name+"/print_crf_iteration_"+iteration;
        CRF crf = null;
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
        crf = (CRF) ois.readObject();
        ois.close();
        
        FileWriter writer = new FileWriter(new File(output_filename));
        //writer.write(crf.toString());
        crf.print(new PrintWriter(writer));
        writer.close();
        crf.print();
        
    }
    
    public void doPrinting() throws IOException, ClassNotFoundException{
        for (int i = 0; i < num_folds; i++) {
            doReadModel(i);
        }
    }
    
    public static void main(String[] args){
        PrintCRFModel printer = new PrintCRFModel();
        try {
            printer.doPrinting();
        } catch (IOException ex) {
            Logger.getLogger(PrintCRFModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PrintCRFModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
