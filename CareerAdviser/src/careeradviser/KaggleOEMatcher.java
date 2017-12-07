/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package careeradviser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Arthur
 */
public class KaggleOEMatcher {
    
    String workingDir = System.getProperty("user.dir");
    String kaggleFileName, oeFileName, ukSalaryPredictions;
    
    public void start(Stage primaryStage) {
        
    kaggleFileName = "C:\\Users\\Arthur\\Desktop\\Fall 2017 SJSU\\CMPE139\\FinalProject\\kaggle_online_job_posts\\data job posts.csv";
    oeFileName = "C:\\Users\\Arthur\\Desktop\\Fall 2017 SJSU\\CMPE139\\FinalProject\\multifile_dataset\\oe.occupation.txt";
    ukSalaryPredictions = "C:\\Users\\Arthur\\Desktop\\Fall 2017 SJSU\\CMPE139\\FinalProject\\uk_salary_predictions\\Book1.csv"; //Train_rev1
    
    boolean readKaggleData, readOEData, readUKData;
    readKaggleData = false;
    readOEData = false;
    readUKData = true;
    
    ArrayList<String[]> kaggleData = new ArrayList<String[]>();
    ArrayList<String[]> oeFileData = new ArrayList<String[]>();
    ArrayList<String[]> ukSalaryPredictionsData = new ArrayList<String[]>();
    
    if (readKaggleData)
        kaggleData = readCSV(kaggleFileName);
    
    if (readOEData)
        oeFileData = readCSV(oeFileName);
    
    if (readUKData)
        ukSalaryPredictionsData = readCSV(ukSalaryPredictions);
    
    System.out.println("kaggleData.length: " + kaggleData.size());
    
    Label dataTitleLabel = new Label();
    dataTitleLabel.setText("DATA:");
    
    Label dataLabel = new Label();
    
    String dataStr = "";
    
    ArrayList<String[]> arrayToDisplay = ukSalaryPredictionsData;
    
    //Display first 20 rows of kaggle dataset
    
    for(int i = 0 ; i < Math.min(20, arrayToDisplay.size()-1); i++){
        for(int j = 0 ; j < Math.min(20, arrayToDisplay.get(i).length-1); j++){
            dataStr += arrayToDisplay.get(i)[j] + "\n";
        }
        }
    
    dataLabel.setText(dataStr);
    
        VBox root = new VBox();
        root.setSpacing(5);
        root.setPadding(new Insets(10, 10, 10, 10));
        
        root.getChildren().add(dataTitleLabel);
        root.getChildren().add(dataLabel);
        
        Scene scene = new Scene(root, 1024, 768);
        
        primaryStage.setTitle("Career Advisor Pro 2017 - Kaggle OE Data");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public ArrayList<String[]> readCSV(String fileName)
    {
    ArrayList<String[]> results = new ArrayList<String[]>();
    
    String csvFile = fileName;
        BufferedReader br;
        String line = "";
        //String[] results = new String[0];
        
        int lineNum = 0;
        try {
        br = new BufferedReader(new FileReader(csvFile));
        while ( (line = br.readLine()) != null && lineNum < 1000 ) {
            results.add(line.split("\t"));
            lineNum++;
        }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        return results;
    }
    
    String copyStringUntilSubstring( String str, String subStr )
    {
    String retStr = "";
    
    for(int i = 0 ; i < str.length() ; i++)
    {
        
    }
    
    return retStr;
    }
    
}
