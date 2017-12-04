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
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
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
        String[] results2 = new String[0];
        
        //Read data from results.csv (KNN Results)
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
        
        //Read data from results2.csv (Regression Analysis Results)
        csvFile = workingDir+"\\results2.csv";
        try {
        br = new BufferedReader(new FileReader(csvFile));
        while ( (line = br.readLine()) != null ) {
            results2 = line.split(",");
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
        resultsTitle.setFont(new Font("Arial", 48));
        
        Label resultsLabel = new Label();
        
        String resultsString = "";
        for(int i = 0 ; i < results.length ; i++)
        {
            resultsString += results[i]+", ";
        }
        
        resultsLabel.setText(resultsString);
        
        resultsTitle.setText("Results");
        
        Button backButton = new Button();
        backButton.setText("New Search");
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                //System.out.println("Search Results:");
                
                startPage.start(primaryStage);
                
            }
        });
        
        ArrayList<ProgressBar> skillValueBars = new ArrayList<ProgressBar>();
        ArrayList<Label> skillLabels = new ArrayList<Label>();
        ArrayList<ProgressBar> jobValueBars = new ArrayList<ProgressBar>();
        ArrayList<Label> jobLabels = new ArrayList<Label>();
        
        ArrayList<String> jobs = new ArrayList<String>();
        ArrayList<Integer> jobsSalaries = new ArrayList<Integer>();
        int highestJobSalary = 0;
        
        for(int i = 0 ; i < results.length ; i+=2)
        {
        jobs.add(results[i]+" ($"+results[i+1]+")");
        jobsSalaries.add(Integer.parseInt(results[i+1]));
        if (jobsSalaries.get(jobsSalaries.size()-1) > highestJobSalary)
            {
            highestJobSalary = jobsSalaries.get(i);
            }
        }
        /*
        jobs.add("Senior Software Engineer at Google ($175,000)");
        jobs.add("Lead Systems Engineer at Lockheed Martin ($170,000)");
        jobs.add("Senior Software Engineer at Apple ($165,000)");
        jobs.add("Content Manager at Microsoft ($155,000)");
        jobs.add("Senior Hacker at CIA ($145,000)");
        */
        ArrayList<String> skills = new ArrayList<String>();
        ArrayList<Integer> skillsSalaries = new ArrayList<Integer>();
        int highestSkillSalary = 0;
        
        for(int i = 0 ; i < results2.length ; i+=2)
        {
        skills.add(results2[i]+" ($"+results2[i+1]+")");
        skillsSalaries.add(Integer.parseInt(results2[i+1]));
        if (skillsSalaries.get(skillsSalaries.size()-1) > highestSkillSalary)
            {
            highestSkillSalary = skillsSalaries.get(i);
            }
        }
        
        
        /*
        skills.add("C++ ($75,000)");
        skills.add("Python ($74,000)");
        skills.add("C# ($73,000)");
        skills.add("Data Mining ($72,000)");
        skills.add("Java ($71,000)");
        skills.add("Airplane Mechanic ($44,000)");
        skills.add("Car Driver ($25,000)");
        */
        
        ScrollPane scrollPane = new ScrollPane();
        VBox root = new VBox();
        
        scrollPane.setContent(root);
        root.setSpacing(5);
        root.setPadding(new Insets(10, 10, 10, 10));
        
        root.getChildren().add(resultsTitle);
        
        root.getChildren().add(resultsLabel);
        
        Label matchedSkillsTitle = new Label();
        matchedSkillsTitle.setText("Highest paying jobs matching your skills:");
        matchedSkillsTitle.setFont(new Font("Arial", 26));
        root.getChildren().add(matchedSkillsTitle);
        
        //For all the jobs, make a label and progressbar showing the value of that job
        for(int i = 0 ; i < jobs.size() ; i++)
        {
        Label newLabel = new Label();
        newLabel.setFont(new Font("Arial", 20));
        newLabel.setText(jobs.get(i));
        ProgressBar newPbar = new ProgressBar();
        newPbar.setProgress(1 -((float)i) / 10f);
        jobLabels.add(newLabel);
        jobValueBars.add(newPbar);
        root.getChildren().add(newLabel);
        root.getChildren().add(newPbar);
        }
        
        Label averageSalaryTitle = new Label();
        averageSalaryTitle.setText("Average salary for jobs with your skills:");
        averageSalaryTitle.setFont(new Font("Arial", 26));
        root.getChildren().add(averageSalaryTitle);
        
        //For all the skills, make a label and progressbar showing the value of that skill
        for(int i = 0 ; i < skills.size() ; i++)
        {
        Label newLabel = new Label();
        newLabel.setFont(new Font("Arial", 20));
        newLabel.setText(skills.get(i));
        ProgressBar newPbar = new ProgressBar();
        newPbar.setProgress(1 -((float)i) / 10f);
        skillLabels.add(newLabel);
        skillValueBars.add(newPbar);
        root.getChildren().add(newLabel);
        root.getChildren().add(newPbar);
        }
        
        root.getChildren().add(backButton);
        
        Scene scene = new Scene(scrollPane, 1024, 768);
        
        primaryStage.setTitle("Career Adviser Pro 2017 - Search Results");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    
}
