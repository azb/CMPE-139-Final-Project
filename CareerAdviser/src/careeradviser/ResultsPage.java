/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package careeradviser;

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
        
        StartPage startPage = new StartPage();
        
        Label resultsTitle = new Label();
        
        Label results = new Label();
        
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
        root.getChildren().add(results);
        root.getChildren().add(backButton);
        
        Scene scene = new Scene(root, 1024, 768);
        
        primaryStage.setTitle("Career Adviser Pro 2017 - Search Results");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
