/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package careeradviser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Arthur Z. Baney
 */
public class ResultsPage {
    public void start(Stage primaryStage) {
        
        String workingDir = System.getProperty("user.dir");
        String csvFile = workingDir+"\\results.csv";
        BufferedReader br;
        String line = "";
        String[] results = new String[0];
        
        try {
        br = new BufferedReader(new FileReader(csvFile));
        while ( (line = br.readLine()) != null ) {
            results = line.split(",");
        }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        StartPage startPage = new StartPage();
        
        Label resultsTitle = new Label();
        
        Label resultsLabel = new Label();
        
        String resultsString = "";
        for(int i = 0 ; i < results.length ; i++)
        {
            resultsString += results[i]+", ";
        }
        
        resultsLabel.setText(resultsString);
        
        resultsTitle.setText("Results Page");
        
        Button backButton = new Button();
        backButton.setText("Back");
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                //System.out.println("Search Results:");
                
                startPage.start(primaryStage);
                
            }
        });
        
        VBox root = new VBox();
        root.setSpacing(5);
        root.setPadding(new Insets(10, 10, 10, 10));
        
        root.getChildren().add(resultsTitle);
        root.getChildren().add(resultsLabel);
        root.getChildren().add(backButton);
        
        Scene scene = new Scene(root, 1024, 768);
        
        primaryStage.setTitle("Career Adviser Pro 2017 - Search Results");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
