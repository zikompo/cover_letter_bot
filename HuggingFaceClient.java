import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class HuggingFaceClient {
    private static final String API_URL = "https://api-inference.huggingface.co/models/";
    private static final String API_TOKEN = "";

    public static String generateText(String modelId, String prompt) {
        try {
            // Prepare the JSON payload
            JSONObject payload = new JSONObject();
            payload.put("inputs", prompt);

            // Create the HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + modelId))
                    .header("Authorization", "Bearer " + API_TOKEN)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            // Send the request and get the response
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the response
            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());
                return jsonResponse.getJSONArray("generated_text").getString(0);
            } else {
                return "Error: " + response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating text.";
        }
    }

    public static void main(String[] args) {
        String modelId = "meta-llama/LLaMA-2-7b-hf"; // Example model ID
        String prompt = "Write a professional cover letter for a software engineer intern.";
        String result = generateText(modelId, prompt);
        System.out.println("Generated Text:\n" + result);
    }
}
