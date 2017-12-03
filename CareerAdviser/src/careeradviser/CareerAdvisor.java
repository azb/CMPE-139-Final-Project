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
 * Career Adviser
 * @author Arthur Z. Baney
 */

public class CareerAdvisor extends Application {
    
    static long timerSeconds = 0; // = new Timer();
    static long lastTime = System.nanoTime();

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
        
        System.out.println("here");
        //FileCheckTimer();
        
        long time = System.nanoTime();
        long deltaTime = ((time - lastTime) / 1000000);
        lastTime = time;
        
        timerSeconds += deltaTime;
        
        if (timerSeconds > 3)
            {
                timerSeconds = 0;
                System.out.println("Checking for file");
            }
    }
}
