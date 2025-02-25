import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class CoverLetterBotGUI extends Application {

    /**
     * Calls gpt.py with the user’s placeholders, captures and returns the cover letter output.
     */
    public static String generateCoverLetter(Path scriptPath, Map<String, String> placeholders) {
        // Ensure no empty placeholders
        for (String value : placeholders.values()) {
            if (Objects.equals(value, "")) {
                return "NULL_SOMEWHERE";
            }
        }

        try {
            // Build the command to run Python with our placeholders
            String[] command = new String[] {
                "python",
                scriptPath.toAbsolutePath().toString(),
                placeholders.get("name"),
                placeholders.get("manager_name"),
                placeholders.get("company_name"),
                placeholders.get("job_title"),
                placeholders.get("skills"),
                placeholders.get("company_values")
            };

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);

            Process process = pb.start();

            // Read the Python script's output
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                // If Python exited with an error, return its console output
                return "Python script exited with code " + exitCode + ". Output:\n" + output;
            }

            return output.toString().trim();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error running Python script: " + e.getMessage();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Cover Letter Bot (GPT Version)");

        // Layout
        VBox formLayout = new VBox(10);
        formLayout.setPadding(new Insets(15));

        // Fields for user input
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
                generateButton, downloadButton,
                outputArea
        );

        // Button: Generate cover letter (via gpt.py)
        generateButton.setOnAction(e -> {
            String name = nameField.getText();
            String managerName = managerField.getText();
            String companyName = companyField.getText();
            String jobTitle = jobTitleField.getText();
            String skills = skillsField.getText();
            String values = valuesField.getText();

            // Build a placeholders map
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("name", name);
            placeholders.put("manager_name", managerName);
            placeholders.put("company_name", companyName);
            placeholders.put("job_title", jobTitle);
            placeholders.put("skills", skills);
            placeholders.put("company_values", values);

            // Call gpt.py
            // Update this path to wherever your gpt.py is located:
            Path scriptPath = Paths.get("./src/main/java/gpt.py");

            String coverLetter = generateCoverLetter(scriptPath, placeholders);

            if (coverLetter.equals("NULL_SOMEWHERE")) {
                outputArea.setText("One of the parameters is empty. Please fill all fields.");
            } else if (coverLetter.startsWith("Python script exited with code")) {
                // Python script returned a non-zero exit code
                outputArea.setText(coverLetter);
            } else if (coverLetter.startsWith("Error running Python script")) {
                // Some I/O or environment error
                outputArea.setText(coverLetter);
            } else {
                // Success: show the GPT output
                outputArea.setText(coverLetter);
                downloadButton.setDisable(false);
            }
        });

        // Button: Download cover letter
        downloadButton.setOnAction(e -> {
            String coverLetter = outputArea.getText();
            if (coverLetter == null || coverLetter.isEmpty()) {
                outputArea.setText("Please generate a cover letter before downloading.");
                return;
            }

            // Let user pick a file to save
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Cover Letter");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Text Files", "*.txt"));

            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                // Write to the chosen file
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(coverLetter);
                    System.out.println("Cover letter saved to: " + file.getAbsolutePath());
                } catch (IOException ex) {
                    outputArea.setText("Error saving the file: " + ex.getMessage());
                }
            }
        });

        // Set the scene
        Scene scene = new Scene(formLayout, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}