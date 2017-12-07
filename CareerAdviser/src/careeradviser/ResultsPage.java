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
import java.text.NumberFormat;
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

        StartPage startPage = new StartPage();
        String workingDir = System.getProperty("user.dir");
        String skillsFile = workingDir + "\\skills.tsv";
        String jobsFile = workingDir + "\\jobs.tsv";
        //String[] skillsResults = new String[0];
        String[] jobsResults = new String[0];

        //Create array list of jobs where each element is a string array with title, description, salary
        ArrayList<String[]> jobs = new ArrayList<String[]>();
        ArrayList<String[]> skills = new ArrayList<String[]>();
        
        //Read data from jobsFile and skillsFile
        
        jobs = readTSVToArrayList(jobsFile);
        skills = readTSVToArrayList(skillsFile);

        //Title Label for Results
        Label jobsResultsTitle = new Label();
        jobsResultsTitle.setFont(new Font("Arial", 48));
        jobsResultsTitle.setText("Top paying jobs with your skills:");
        
        Label skillsResultsTitle = new Label();
        skillsResultsTitle.setFont(new Font("Arial", 48));
        skillsResultsTitle.setText("Worth of your skills:");

        Button backButton = new Button();
        backButton.setText("New Search");
        backButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                //System.out.println("Search Results:");

                startPage.start(primaryStage);
            }
        });

        ScrollPane scrollPane = new ScrollPane();
        VBox root = new VBox();

        //Add results Title
        root.getChildren().add(jobsResultsTitle);

        double maxSalary = Double.parseDouble(jobs.get(0)[2]);
        
        NumberFormat f = NumberFormat.getCurrencyInstance();
        
        ScrollPane jobPane = new ScrollPane();
        VBox jobsPaneVBox = new VBox();
        jobPane.setContent(jobsPaneVBox);
        
        //Add jobs labels
        for (int i = 0; i < jobs.size(); i += 1) {
            String salaryStr = f.format(Double.parseDouble(jobs.get(i)[2]));
            double curSalary = Double.parseDouble(jobs.get(i)[2]);
            //Add title, salary, and description text
            Label titleLabel = new Label(jobs.get(i)[0]);
            Label salaryLabel = new Label("Annual Income: "+salaryStr);
            
            titleLabel.setFont(new Font("Arial", 26));
            salaryLabel.setFont(new Font("Arial", 18));
            //Label descriptionLabel = new Label("Description: "+jobs.get(i)[1]);
            jobsPaneVBox.getChildren().add(titleLabel);
            jobsPaneVBox.getChildren().add(salaryLabel);
            //root.getChildren().add(descriptionLabel);
            
            //Add progress bars
            ProgressBar newPbar = new ProgressBar();
            newPbar.setProgress(curSalary / maxSalary);
            jobsPaneVBox.getChildren().add(newPbar);
        }
        root.getChildren().add(jobPane);
        
        root.getChildren().add(skillsResultsTitle);
        
        ScrollPane skillPane = new ScrollPane();
        VBox skillPaneVBox = new VBox();
        skillPane.setContent(skillPaneVBox);
        
        maxSalary = Double.parseDouble(skills.get(0)[1]);
        
        //Add skills labels
        for (int i = 0; i < skills.size(); i += 1) {
            String salaryStr = f.format(Double.parseDouble(skills.get(i)[1]));
            double curSalary = Double.parseDouble(skills.get(i)[1]);
            //Add title, salary, and description text
            Label titleLabel = new Label(skills.get(i)[0]);
            Label valueLabel = new Label("Value: "+salaryStr);
            
            titleLabel.setFont(new Font("Arial", 26));
            valueLabel.setFont(new Font("Arial", 18));
            //Label descriptionLabel = new Label("Description: "+jobs.get(i)[1]);
            skillPaneVBox.getChildren().add(titleLabel);
            skillPaneVBox.getChildren().add(valueLabel);
            
            //Add progress bars
            ProgressBar newPbar = new ProgressBar();
            newPbar.setProgress(curSalary / maxSalary);
            skillPaneVBox.getChildren().add(newPbar);
        }
        

        root.getChildren().add(skillPane);

        scrollPane.setContent(root);
        root.setSpacing(5);
        root.setPadding(new Insets(10, 10, 10, 10));

        /*
        Label matchedSkillsTitle = new Label();
        matchedSkillsTitle.setText("Highest paying jobs matching your skills:");
        matchedSkillsTitle.setFont(new Font("Arial", 26));
        root.getChildren().add(matchedSkillsTitle);

        Label averageSalaryTitle = new Label();
        averageSalaryTitle.setText("Average salary for jobs with your skills:");
        averageSalaryTitle.setFont(new Font("Arial", 26));
        root.getChildren().add(averageSalaryTitle);
        */
        
        //For all the skills, make a label and progressbar showing the value of that skill
        /*for (int i = 0; i < skills.size(); i++) {
            Label newLabel = new Label();
            newLabel.setFont(new Font("Arial", 20));
            newLabel.setText(skills.get(i));
            ProgressBar newPbar = new ProgressBar();
            newPbar.setProgress(1 - ((float) i) / 10f); //
            skillLabels.add(newLabel);
            skillValueBars.add(newPbar);
            root.getChildren().add(newLabel);
            root.getChildren().add(newPbar);
        }*/
        
        //root.getChildren().add(backButton);

        Scene scene = new Scene(scrollPane, 1024, 768);

        primaryStage.setTitle("Career Advisor Pro 2017 - Search Results");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    ArrayList<String[]> readTSVToArrayList(String tsvFileName)
    {
        String line = "";
        BufferedReader br;
        ArrayList<String[]> retArrayList = new ArrayList<String[]>();
    try {
            br = new BufferedReader(new FileReader(tsvFileName));
            br.readLine();
            while ((line = br.readLine()) != null) {
                retArrayList.add(line.split("\t"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    
    return retArrayList;
    }

}
