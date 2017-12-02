/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package careeradviser;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.scene.control.ProgressBar;

/**
 *
 * @author Arthur
 */
public class CareerAdviser extends Application {
    
        public enum ProgramState {
            AWAITING_INPUT,
            LOADING_FILES,
            DISPLAYING_RESULTS
        }
        
    @Override
    public void start(Stage primaryStage) {
        
        ProgramState programState = ProgramState.AWAITING_INPUT;
        
        StartPage startPage = new StartPage();
        startPage.start(primaryStage);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
