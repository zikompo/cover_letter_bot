import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class CoverLetterBotGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Cover Letter Bot");

        // Layout for the form
        VBox formLayout = new VBox(10);
        formLayout.setPadding(new Insets(15));

        // Inputs
        TextField nameField = new TextField();
        nameField.setPromptText("Your Name");

        TextField managerField = new TextField();
        managerField.setPromptText("Hiring Manager's Name");

        TextField companyField = new TextField();
        companyField.setPromptText("Company Name");

        TextField jobTitleField = new TextField();
        jobTitleField.setPromptText("Job Title");

        TextField skillsField = new TextField();
        skillsField.setPromptText("Key Skills/Experience");

        TextField valuesField = new TextField();
        valuesField.setPromptText("Company Values/Mission");

        // Template selection
        Label templateLabel = new Label("Select Template:");
        ComboBox<String> templateComboBox = new ComboBox<>();
        templateComboBox.setPromptText("Choose a Template");
        templateComboBox.getItems().addAll(listTemplates("templates")); // Load templates dynamically

        // Buttons
        Button generateButton = new Button("Generate Cover Letter");
        Button downloadButton = new Button("Download Cover Letter");
        downloadButton.setDisable(true);
        // Output area
        TextArea outputArea = new TextArea();
        outputArea.setPromptText("Your generated cover letter will appear here...");
        outputArea.setWrapText(true);
        outputArea.setEditable(false);

        // Add components to the layout
        formLayout.getChildren().addAll(
                new Label("Your Name:"), nameField,
                new Label("Hiring Manager's Name:"), managerField,
                new Label("Company Name:"), companyField,
                new Label("Job Title:"), jobTitleField,
                new Label("Key Skills/Experience:"), skillsField,
                new Label("Company Values/Mission:"), valuesField,
                templateLabel, templateComboBox,
                generateButton, downloadButton, outputArea
        );

        // Button click event for generating the cover letter
        generateButton.setOnAction(e -> {
            String name = nameField.getText();
            String managerName = managerField.getText();
            String companyName = companyField.getText();
            String jobTitle = jobTitleField.getText();
            String skills = skillsField.getText();
            String values = valuesField.getText();
            String selectedTemplate = templateComboBox.getValue();

            if (selectedTemplate == null || selectedTemplate.isEmpty()) {
                outputArea.setText("Please select a template.");
                return;
            }

            // Placeholder replacements
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("name", name);
            placeholders.put("manager_name", managerName);
            placeholders.put("company_name", companyName);
            placeholders.put("job_title", jobTitle);
            placeholders.put("skills", skills);
            placeholders.put("company_values", values);

            // Generate cover letter
            Path templatePath = Paths.get("templates", selectedTemplate);
            String coverLetter = generateCoverLetter(templatePath, placeholders);

            if (!coverLetter.isEmpty() && coverLetter != "NULL_SOMEWHERE") {
                outputArea.setText(coverLetter);
                downloadButton.setDisable(false);
            }
            else if (coverLetter == "NULL_SOMEWHERE"){
                outputArea.setText("One of the parameters is null");
            }
            else {
                outputArea.setText("Error generating the cover letter. Please check the template.");
            }
        });

        // Button click event for downloading the cover letter
        downloadButton.setOnAction(e -> {
            String coverLetter = outputArea.getText();
            if (coverLetter == null || coverLetter.isEmpty()) {
                outputArea.setText("Please generate a cover letter before downloading.");
                return;
            }

            // Open a FileChooser dialog to save the file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Cover Letter");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                saveToFile(file, coverLetter);
            }
        });

        // Set the scene and show
        Scene scene = new Scene(formLayout, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to list templates in a folder
    public static List<String> listTemplates(String folderPath) {
        List<String> templates = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(folderPath))) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry) && entry.toString().endsWith(".txt")) {
                    templates.add(entry.getFileName().toString());
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading templates: " + e.getMessage());
        }
        return templates;
    }

    // Method to generate the cover letter using regex
    public static String generateCoverLetter(Path templatePath, Map<String, String> placeholders) {
        try {
            for (String placeholder: placeholders.values()){
                if (Objects.equals(placeholder, "")){
                    return "NULL_SOMEWHERE";
                }
            }
            String template = Files.readString(templatePath);
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                String placeholder = "\\{" + entry.getKey() + "\\}";
                template = template.replaceAll(placeholder, Matcher.quoteReplacement(entry.getValue()));
            }
            return template;
        } catch (IOException e) {
            System.out.println("Error reading template: " + e.getMessage());
            return "";
        }
    }

    // Method to save content to a file
    public static void saveToFile(File file, String content) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            System.out.println("Cover letter saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error saving the file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}