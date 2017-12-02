/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package careeradviser;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 *startPage
 * @author Arthur
 */
public class WaitingPage {
        
    public void start(Stage primaryStage){
        
        StartPage startPage = new StartPage();
        
        VBox root = new VBox();
        root.setSpacing(5);
        root.setPadding(new Insets(10, 10, 10, 10));
        
        Label loadingLabel = new Label();
        loadingLabel.setText("Waiting for Python Data Mining Application");
        
        Button backButton = new Button();
        backButton.setText("Back");
        backButton.setOnAction(new EventHandler<ActionEvent>() {
    
            @Override
            public void handle(ActionEvent event) {
                //System.out.println("Search Results:");
                
                startPage.start(primaryStage);
                
            }
        });
        
        root.getChildren().add(loadingLabel);
        root.getChildren().add(backButton);
        
        Scene scene = new Scene(root, 1024, 768);
        
        primaryStage.setTitle("Career Adviser Pro 2017");
        primaryStage.setScene(scene);
        primaryStage.show();
}
}

