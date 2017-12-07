
package careeradviser;
import java.io.FileNotFoundException;
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
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Pos;
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
    KaggleOEMatcher kaggleOEMatcher = new KaggleOEMatcher();

    public void start(Stage primaryStage){
        
        String title = "";

        //for(String arg : launchArgs)
        //    title += arg+", ";
            //System.out.println(arg);
        
        //Progressbar to show search progress (TODO: move this to the waiting screen)
        ProgressBar fileLoadingProgress = new ProgressBar(0.6);

        //Initialize Interface Components

        //Welcome label, gives user instructions for how to use program
        Label welcomeLabel = new Label();
        welcomeLabel.setText("Welcome to Career Adviser Pro 2017!\n\nTell us your skills and work experience and we will help you find a career path that will maximize your salary!");
        welcomeLabel.setText(title);
        welcomeLabel.wrapTextProperty().set(true);
        welcomeLabel.setFont(new Font("Arial", 26));

        //Skills label just says "Skills:" in front of the skills input box
        Label skillsLabel = new Label();
        skillsLabel.setText("Skills:");
        skillsLabel.setFont(new Font("Arial", 26));

        //Past Jobs label just says "Past Jobs:" in front of the past jobs input box
        Label pastJobsLabel = new Label();
        pastJobsLabel.setText("Past Jobs:");
        pastJobsLabel.setFont(new Font("Arial", 26));

        //Results label displays the results of the search (TODO: this will be migrated to the results page , delete later)
        Label resultsLabel = new Label();
        resultsLabel.setFont(new Font("Arial", 26));

        //Skills input box, where user enters skills separated by commas
        TextField skillsInputBox = new TextField();
        skillsInputBox.setFont(new Font("Arial", 20));
        skillsInputBox.setText("c++, c#, java, python, data mining");

        TextField pastJobsInputBox = new TextField();
        pastJobsInputBox.setFont(new Font("Arial", 20));
        pastJobsInputBox.setText("software engineer, librarian, burger flipper, car driver, airplane mechanic");

        //Message label, displays errors if the user inputs something wrong
        Label messageLabel = new Label();
        messageLabel.setFont(new Font("Arial", 26));

        Button kaggleOEButton = new Button();
        kaggleOEButton.setText("View KaggleOE");
        kaggleOEButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                    kaggleOEMatcher.start(primaryStage);
            }
        });

        //Search button, clicking it will take the user to the waiting page and execute the python backend, which when done will take the user to the results page
        Button searchButton = new Button();
        searchButton.setText("Search!");
        searchButton.setFont(new Font("Arial", 30));
        searchButton.setAlignment(Pos.CENTER);
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                //Get the skills inputted by the user
                String skills = skillsInputBox.getText();  
                String pastJobs = pastJobsInputBox.getText();  

                //Check if the user inputted anything
                if (skills.equals(""))
                    {
                    //If the user didn't input anything, show a message telling them to enter some skills
                    messageLabel.setText("Please enter some of your skills.");
                    //Set the message color to red
                    messageLabel.setTextFill(Color.RED);
                    }
                else
                if (pastJobs.equals(""))
                    {
                    //If the user didn't input anything, show a message telling them to enter some skills
                    messageLabel.setText("Please enter some of your past jobs.");
                    //Set the message color to red
                    messageLabel.setTextFill(Color.RED);
                    }
                else
                    {
                    //Create an arraylist from the string of skills
                    List<String> skillsList = new ArrayList<String>(Arrays.asList(skills.split(",")));
                    List<String> pastJobsList = new ArrayList<String>(Arrays.asList(pastJobs.split(",")));

                    //Create an array for the parameters to pass into the python backend
                    //String[] params = new String [1]; //+skillsList.size()];

                    //Set the first parameter to the python program to execute

                    String workingDir = System.getProperty("user.dir");
                    //params[0] = "C:\\Python27\\python.exe \""+workingDir+"\\dummy.py\""; //careerAdviserBackend

                    String argStr = ""; //\"";
                    //Add User's Inputted Skills to Arguments for Python program
                    for(int i = 0 ; i < skillsList.size() ; i++)
                    {
                        if (skillsList.get(i).charAt(0) == ' ')
                            skillsList.set(i, skillsList.get(i).substring(1));
                        argStr += skillsList.get(i);
                        if (i < skillsList.size()-1)
                            argStr += "|";
                    }
                    //argStr+="\n"; //="\" \"";

                    String argStr2 = "";
                    //Add User's Inputted Past Jobs to Arguments for Python program
                    for(int i = 0 ; i < pastJobsList.size() ; i++)
                    {
                        if (pastJobsList.get(i).charAt(0) == ' ')
                            pastJobsList.set(i, pastJobsList.get(i).substring(1));
                        argStr2 += pastJobsList.get(i);
                        if (i < pastJobsList.size()-1)
                            argStr2 += "|";
                    }
                    //argStr += "\"";

                    String pythonCommand = "\""+workingDir+"\\careerAdviserBackend.py\" "+argStr; //

                    System.out.println(argStr);

                    PrintWriter writer;
                    try {
                        writer = new PrintWriter("params.txt", "UTF-8");
                        writer.println(argStr);
                        writer.println(argStr2);
                        writer.close();

                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(StartPage.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (UnsupportedEncodingException ex) {
                        Logger.getLogger(StartPage.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    /*
                    ProcessBuilder pb = new ProcessBuilder("C:\\Python27\\python.exe", pythonCommand);
                    try {
                        Process p = pb.start();
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
        root.getChildren().add(pastJobsLabel);
        root.getChildren().add(pastJobsInputBox);
        root.getChildren().add(messageLabel);
        root.getChildren().add(searchButton);
        //root.getChildren().add(kaggleOEButton);
        root.getChildren().add(resultsLabel);

        //Create a scene out of the root
        Scene scene = new Scene(root, 800, 600);

        //Set the title of the window
        primaryStage.setTitle("Career Advisor Pro 2017 - Search");

        //Set the primary window to display the scene next time show() is called
        primaryStage.setScene(scene);

        //Show the scene in the primary window
        primaryStage.show();
    }
    }