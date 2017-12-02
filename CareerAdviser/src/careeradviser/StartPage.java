
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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;

/**
 * Start Page
 * @author Arthur Z. Baney
 * @description This page lets the user enter some skills they have and then search for careers with highest incomes with those selected skills
 */

public class StartPage {
    
    WaitingPage waitingPage = new WaitingPage();
        
    public void start(Stage primaryStage){
    
    //Progressbar to show search progress (TODO: move this to the waiting screen)
    ProgressBar fileLoadingProgress = new ProgressBar(0.6);
        
    //Initialize Interface Components
    
    //Welcome label, gives user instructions for how to use program
    Label welcomeLabel = new Label();
    welcomeLabel.setText("Welcome to Career Adviser Pro 2017!\n\nTell us your skills and work experience and we will help you find a career path that will maximize your salary!");
    welcomeLabel.wrapTextProperty().set(true);
    welcomeLabel.setFont(new Font("Arial", 16));
    
    //Skills label just says "Skills:" in front of the skills input box
    Label skillsLabel = new Label();
    skillsLabel.setText("Skills:");
    
    //Results label displays the results of the search (TODO: this will be migrated to the results page , delete later)
    Label resultsLabel = new Label();
    
    //Skills input box, where user enters skills separated by commas
    TextField skillsInputBox = new TextField();

    //Message label, displays errors if the user inputs something wrong
    Label messageLabel = new Label();
    
    //Search button, clicking it will take the user to the waiting page and execute the python backend, which when done will take the user to the results page
    Button searchButton = new Button();
    searchButton.setText("Search!");
    searchButton.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            //Get the skills inputted by the user
            String skills = skillsInputBox.getText();  
            
            //Check if the user inputted anything
            if (skills.equals(""))
                {
                //If the user didn't input anything, show a message telling them to enter some skills
                messageLabel.setText("Please enter some of your skills.");
                //Set the message color to red
                messageLabel.setTextFill(Color.RED);
                }
            else
                {
                //Create an arraylist from the string of skills
                List<String> skillsList = new ArrayList<String>(Arrays.asList(skills.split(",")));
                
                //Create an array for the parameters to pass into the python backend
                String[] params = new String [1+skillsList.size()];

                //Set the first parameter to the python program to execute
                params[0] = "careerAdviserBackend.py";

                //Add skills the user entered as command line arguments for the python program
                for(int i = 0 ; i < skillsList.size(); i++ )
                {
                    params[i+1] = skillsList.get(i);
                    System.out.println(params[i+1]);
                }

                //Print the params
                
                /*
                    try {
                        Runtime.getRuntime().exec(params);
                    } catch (IOException ex) {
                        Logger.getLogger(StartPage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                        */
                waitingPage.start(primaryStage);
                }
    }});

    //Create a vertical list to contain all the interface components
    VBox root = new VBox();
    root.setSpacing(5);
    root.setPadding(new Insets(10, 10, 10, 10));
    
    //Add Interface Components to root view
    root.getChildren().add(welcomeLabel);
    root.getChildren().add(skillsLabel);
    root.getChildren().add(skillsInputBox);
    root.getChildren().add(messageLabel);
    root.getChildren().add(searchButton);
    root.getChildren().add(resultsLabel);

    //Create a scene out of the root
    Scene scene = new Scene(root, 1024, 768);

    //Set the title of the window
    primaryStage.setTitle("Career Adviser Pro 2017");
    
    //Set the primary window to display the scene next time show() is called
    primaryStage.setScene(scene);
    
    //Show the scene in the primary window
    primaryStage.show();
}
}