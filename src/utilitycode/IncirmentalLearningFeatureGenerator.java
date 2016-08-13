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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author Asus
 */
public class IncirmentalLearningFeatureGenerator {

    ArrayList<ArrayList<String>> token = new ArrayList<>();
    ArrayList<ArrayList<String>> label = new ArrayList<>();
    ArrayList<ArrayList<String>> postag = new ArrayList<>();
    ArrayList<String> event_name_gazetteer = new ArrayList<>();
    ArrayList<String> event_location_gazetteer = new ArrayList<>();

    private FileWriter filewriter;
    private PrintWriter writer;

    public int total_folds = 4;
    public int current_sub_iteration = 2;
    public int base_training = 700;
    public int base_testing = 200;

    String string_pat_capital = "[A-Z]+";
    Pattern pattern_capital = Pattern.compile(string_pat_capital);

    public void doReadFile1() {
        String filename = "tested/CMUTools/NER_gold_standard";
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            ArrayList<String> temp1 = new ArrayList<>();
            ArrayList<String> temp2 = new ArrayList<>();
            ArrayList<String> temp3 = new ArrayList<>();
            int i = 0;
            while ((line = br.readLine()) != null) {
                if (line.equals("")) {
                    token.add(temp1);
                    label.add(temp3);

                    temp1 = new ArrayList<>();
                    temp2 = new ArrayList<>();
                    temp3 = new ArrayList<>();
                } else {
                    //System.out.println("[" + i + "] " + line);
                    String[] splitted = line.split("\t");
                    temp1.add(splitted[0]);
                    temp2.add(splitted[1]);

                    // Disini kalau bukan other.., cek apakah previousnya adalah sama ? kalau sama berarti I kalau beda berarti b..
                    if (!splitted[1].equals("O")) {
                        if (temp2.size() == 1) { // kalau perama pasti begin..
                            temp3.add("B-" + getBetterLabel(splitted[1]));
                        } else {
                            // ambil previous nya ...
                            String last_label = temp2.get(temp2.size() - 2); // selalu dimulai dari 0
                            if (last_label.equals(splitted[1])) {
                                temp3.add("I-" + getBetterLabel(splitted[1]));
                            } else {
                                temp3.add("B-" + getBetterLabel(splitted[1]));
                            }
                        }
                    } else {
                        temp3.add("O");
                    }

                }
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doReadFile3(int i) {
        event_name_gazetteer = new ArrayList<>();
        String filename = "incrimental_learning_1/incrimental_iteration_" + i + "/gazetteer_nama_event";
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.equals("")) {
                    event_name_gazetteer.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doReadFile4(int i) {
        event_location_gazetteer = new ArrayList<>();
        String filename = "incrimental_learning_1/incrimental_iteration_" + i + "/gazetteer_lokasi_event";
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.equals("")) {
                    event_location_gazetteer.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getBetterLabel(String oldlabel) {
        if (oldlabel.equals("N")) {
            return "Name";
        } else if (oldlabel.equals("T")) {
            return "Time";
        } else if (oldlabel.equals("L")) {
            return "Location";
        } else {
            return "Other>>>>ERRROR<<<<< ";
        }
    }

    public void doReadFile2() {
        System.out.println("Reading Gold Standard pos tag...");
        String filename = "tested/CMUTools/POSTagged_gold_standard";
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            ArrayList<String> temp2 = new ArrayList<>();
            int i = 0;
            while ((line = br.readLine()) != null) {
                if (line.equals("")) {
                    postag.add(temp2);

                    temp2 = new ArrayList<>();
                } else {
                    //System.out.println("[" + i + "] " + line);

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startWriter(String filename) throws IOException {
        filewriter = new FileWriter(filename);
        writer = new PrintWriter(filewriter);
    }

    private void closeWriter() throws IOException {
        writer.flush();
        writer.close();
        filewriter.close();
    }

    // Tahapan besar
    // Buat Folder untuk setiap iterasi
    //  Untuk setiap folder, generate training Base Fixed 500 offset = 200*nomor iterasi
    //  Create model dari 500 file ini..
    //  Coba Classify Name & Location dari 500 file ini, simpan di dalam sebuah file katakan : event_name_gazetteer pastikan bawha yang masuk hanya yang probabilitynya daiats 0.8
    //  
    //  Repeat until.. 4 kali (200 * 4 = 800 + 500 = 1300)
    //      Generate FileFeature/untagged (200 data) --> dengan fitur memanfaatkan file bernama event_name_gazetteer dan event_location_gazetter
    //      Generate FileGoldStandard (untuk dibandingkan) (200 data)
    //      Jalankan Classifier dengan model yangsama tapi fitur bertambah..(append file)
    public void doWriting() throws IOException {
        startWriter("tested/CMUTools/merged");
        for (int i = 0; i < label.size(); i++) {
            for (int j = 0; j < label.get(i).size(); j++) {
                String _token = token.get(i).get(j);
                String _postag = postag.get(i).get(j);
                String _label = label.get(i).get(j);
                writer.write(_token + "\t" + _postag + "\t" + _label + "\n"); // gak sesuai format mallet, harus dipisah spasi
            }
            writer.write("\n");
        }
        closeWriter();
    }
    
    // Cuma dijalanin sekali bahkan...
    public void doWritingTraining() throws IOException {
        // aasumsi jml data ada 17 folds = 3 base = 5 start 1 0 end 1 5

        int base = label.size() / total_folds;
        for (int i = 0; i < total_folds; i++) {
            int start_index = i * base_testing;
            int end_index = start_index + base_training;

            doReadFile3(i); // baca gazetteer nama event
            doReadFile4(i); // baca gazetteer lokasi event
            System.out.println("Writing training data : "+"incrimental_learning_1/incrimental_iteration_" + i + "/training_merged_" + i + ".training");
            System.out.println("Fold Number \t: "+i);
            System.out.println("Start index \t: "+start_index);
            System.out.println("End index \t: "+end_index);
            writeTrainingData(start_index, end_index, i);
            
            System.out.println("---");
            System.out.println("Writing Testing data : "+"incrimental_learning_1/incrimental_iteration_" + i + "/testing_merged_sub_iteration_" + current_sub_iteration + ".untagged");
            writeTestingData(start_index, end_index, i);
            System.out.println("---");
            System.out.println("incrimental_learning_1/incrimental_iteration_" + i + "/testing_merged_sub_iteration_" + current_sub_iteration + ".gold_standard");
            writeGoldStandard(start_index, end_index, i);
            System.out.println("===DONE==");
            /**/
        }
    }

    private void writeTrainingData(int start_index, int end_index, int i) throws IOException {
        //startWriter("incrimental_learning_1/incrimental_iteration_" + i + "/training_merged_" + i + ".training");
        startWriter("incrimental_learning_1/incrimental_iteration_" + i + "/training_merged_sub_iteration_" + current_sub_iteration + ".training");
        for (int k = 0; k < label.size(); k++) {

            // kalau k diluar itu semua baru ditulis sebagai training data..
            if ((start_index < k && k <= end_index)) {

                for (int j = 0; j < label.get(k).size(); j++) {
                    String _token = token.get(k).get(j);
                    String _postag = postag.get(k).get(j);
                    String _label = label.get(k).get(j);

                    StringBuffer sb = new StringBuffer();
                    // Fitur 1 : Leksikal
                    //sb.append(_token.toLowerCase().replaceAll("[AIUEOaiueo]", ""));
                    sb.append(_token);

                    // Fitur 2 : POS_Tag
                    sb.append(" " + _postag);
                    // Fitur 3 : Kapitalisasi
                    if (_token.matches(string_pat_capital)) {
                        sb.append(" IS_CAPITAL");
                    }

                    // Fitur 4 : Posisi 
                    float position = (float) j / (float) label.get(k).size();
                    if (position < 0.25) {
                        sb.append(" FIRST_SEGMENT");
                    } else if ((position >= 0.25) && (position < 0.5)) {
                        sb.append(" SECOND_SEGMENT");
                    } else if ((position >= 0.5) && (position < 0.75)) {
                        sb.append(" THIRD_SEGMENT");
                    } else if ((position >= 0.75) && (position <= 1.0)) {
                        sb.append(" FOURTH_SEGMENT");
                    }

                    if (isNamaEvent(_token)) {
                        sb.append(" NAMA_EVENT");
                    }

                    if (isLokasiEvent(_token)) {
                        sb.append(" LOKASI_EVENT");
                    }

                    // Karena untuk training
                    sb.append(" " + _label);
                    sb.append("\n");
                    writer.write(sb.toString());

                }
                writer.write("\n");

            }

        }

        closeWriter();
    }

    public void doWritingTesting() throws IOException {
        // aasumsi jml data ada 17 folds = 3 base = 5 start 1 0 end 1 5

        int base = label.size() / total_folds;
        for (int i = 0; i < total_folds; i++) {

            doReadFile3(i); // nama event
            doReadFile4(i); // lokasi event

            int start_index, end_index;

            if (current_sub_iteration < i) {
                start_index = current_sub_iteration * base_testing;
                end_index = start_index + base_testing;
            } else {
                start_index = (current_sub_iteration * base_testing) + base_training;
                end_index = start_index + base_testing;
            }
            
            
            System.out.println("Writing training data : "+"incrimental_learning_1/incrimental_iteration_" + i + "/training_merged_" + i + ".training");
            System.out.println("Fold Number \t: "+i);
            System.out.println("Start index \t: "+start_index);
            System.out.println("End index \t: "+end_index);
            System.out.println("---");
            System.out.println("Writing Testing data : "+"incrimental_learning_1/incrimental_iteration_" + i + "/testing_merged_sub_iteration_" + current_sub_iteration + ".untagged");
            writeTestingData(start_index, end_index, i);
            System.out.println("---");
            System.out.println("Writing gold standard file : incrimental_learning_1/incrimental_iteration_" + i + "/testing_merged_sub_iteration_" + current_sub_iteration + ".gold_standard");
            writeGoldStandard(start_index, end_index, i);
            System.out.println("===DONE==");
            writeTrainingData(start_index, end_index, i);
        }
    }

    private void writeTestingData(int start_index, int end_index, int i) throws IOException {
        startWriter("incrimental_learning_1/incrimental_iteration_" + i + "/testing_merged_sub_iteration_" + current_sub_iteration + ".untagged");
        for (int k = 0; k < label.size(); k++) {

            // kalau k didalam itu semua justru malah ditulis tapi tanpa label
            if (start_index < k && k <= end_index) {

                for (int j = 0; j < label.get(k).size(); j++) {
                    String _token = token.get(k).get(j);
                    String _postag = postag.get(k).get(j);
                    String _label = label.get(k).get(j);

                    StringBuffer sb = new StringBuffer();
                    // Fitur 1 : Leksikal
                    //sb.append(_token.toLowerCase().replaceAll("[AIUEOaiueo]", ""));
                    sb.append(_token);
                    // Fitur 2 : POS_Tag
                    sb.append(" " + _postag);
                    // Fitur 3 : Kapitalisasi
                    if (_token.matches(string_pat_capital)) {
                        sb.append(" IS_CAPITAL");
                    }

                    // Fitur 4 : Posisi Relatif
                    float position = (float) j / (float) label.get(k).size();
                    if (position < 0.25) {
                        sb.append(" FIRST_SEGMENT");
                    } else if ((position >= 0.25) && (position < 0.5)) {
                        sb.append(" SECOND_SEGMENT");
                    } else if ((position >= 0.5) && (position < 0.75)) {
                        sb.append(" THIRD_SEGMENT");
                    } else if ((position >= 0.75) && (position <= 1.0)) {
                        sb.append(" FOURTH_SEGMENT");
                    }

                    if (isNamaEvent(_token)) {
                        sb.append(" NAMA_EVENT");
                    }

                    if (isLokasiEvent(_token)) {
                        sb.append(" LOKASI_EVENT");
                    }

                    // Tanpa label karena untuk testing..
                    sb.append("\n");
                    writer.write(sb.toString());
                }
                writer.write("\n");

            }

        }
        closeWriter();
    }

    public void writeGoldStandard(int start_index,int end_index,int i) throws IOException {
        startWriter("incrimental_learning_1/incrimental_iteration_" + i + "/testing_merged_sub_iteration_" + current_sub_iteration + ".gold_standard");
        for (int k = 0; k < label.size(); k++) {

            // kalau k didalam itu semua justru malah ditulis tapi tanpa label
            if (start_index < k && k <= end_index) {

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

    private boolean isNamaEvent(String token) {
        return event_name_gazetteer.contains(token);
    }

    private boolean isLokasiEvent(String token) {
        return event_location_gazetteer.contains(token);
    }

    public static void main(String[] args) {
        IncirmentalLearningFeatureGenerator generator = new IncirmentalLearningFeatureGenerator();
        generator.doReadFile1();
        generator.doReadFile2();
        try {
            //generator.doWritingTraining(); // disabled dulu karena dijalanin sekali
            generator.doWritingTesting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
