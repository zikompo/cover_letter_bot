import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class CoverLetterBot {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // List available templates
        List<String> templates = listTemplates("templates");
        if (templates.isEmpty()) {
            System.out.println("No templates found in the 'templates' folder.");
            return;
        }

        // Display templates for selection
        System.out.println("Available Templates:");
        for (int i = 0; i < templates.size(); i++) {
            System.out.println((i + 1) + ". " + templates.get(i));
        }

        // Get user choice
        System.out.print("Enter the number of the template you want to use: ");
        int choice;
        while (true) {
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice >= 1 && choice <= templates.size()) {
                    break;
                }
                System.out.print("Invalid choice. Please enter a valid number: ");
            } catch (NumberFormatException e) {
                System.out.print("Please enter a number: ");
            }
        }

        // Selected template
        String selectedTemplate = templates.get(choice - 1);
        Path templatePath = Paths.get("templates", selectedTemplate);

        // Collect user inputs
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("Enter the hiring manager's name: ");
        String managerName = scanner.nextLine();

        System.out.print("Enter the company name: ");
        String companyName = scanner.nextLine();

        System.out.print("Enter the job title: ");
        String jobTitle = scanner.nextLine();

        System.out.print("Enter your key skills/experience: ");
        String skills = scanner.nextLine();

        System.out.print("Enter the company's values/mission: ");
        String companyValues = scanner.nextLine();

        // Create a map of placeholders and their replacements
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", name);
        placeholders.put("manager_name", managerName);
        placeholders.put("company_name", companyName);
        placeholders.put("job_title", jobTitle);
        placeholders.put("skills", skills);
        placeholders.put("company_values", companyValues);

        // Generate the cover letter
        String coverLetter = generateCoverLetter(templatePath, placeholders);

        // Save the cover letter to a file
        saveToFile("cover_letter.txt", coverLetter);

        System.out.println("\nCover letter generated and saved as 'cover_letter.txt'");
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
            System.out.println("An error occurred while listing templates: " + e.getMessage());
        }
        return templates;
    }

    // Method to generate the cover letter using regex
    public static String generateCoverLetter(Path templatePath, Map<String, String> placeholders) {
        try {


            // Read the template file
            String template = Files.readString(templatePath);

            // Replace placeholders using regex
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                String placeholder = "\\{" + entry.getKey() + "\\}"; // Match {key}
                template = template.replaceAll(placeholder, Matcher.quoteReplacement(entry.getValue()));
            }

            return template;
        } catch (IOException e) {
            System.out.println("An error occurred while reading the template file: " + e.getMessage());
            return "";
        }
    }

    // Method to save the cover letter to a file
    public static void saveToFile(String fileName, String content) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(content);
        } catch (IOException e) {
            System.out.println("An error occurred while saving the file: " + e.getMessage());
        }
    }
}