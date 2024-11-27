package helper_functions;

import static helper_functions.GeminiAPI.parseQuizResponse;
import static helper_functions.GeminiAPI.quizGenerator;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONObject;

public class LanguageTranslation {
    // API Endpoint and Key
    private static final String TRANSLATE_API_URL = "https://translation.googleapis.com/language/translate/v2";
    private static final String API_KEY = "AIzaSyBS9PEVv3lECya-jiP91kSzXFh3KRQZDX0";

    /**
     * Translate a nested list structure
     *
     * @param inputList Input nested list to translate
     * @param targetLanguage Target language code
     * @return Translated nested list
     */
    public static List<List<List<String>>> translateNestedList(
            List<List<List<String>>> inputList,
            String targetLanguage
    ) {
        // Create a result list with the same structure
        List<List<List<String>>> translatedList = new ArrayList<>();

        // Create an executor service for concurrent translations
        ExecutorService executorService = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()
        );

        try {
            // Iterate through the nested structure
            for (List<List<String>> outerList : inputList) {
                List<List<String>> translatedOuterList = new ArrayList<>();

                for (List<String> middleList : outerList) {
                    List<String> translatedMiddleList = new ArrayList<>();

                    // Create futures for concurrent translation
                    List<CompletableFuture<String>> futures = new ArrayList<>();
                    for (String str : middleList) {
                        CompletableFuture<String> future = CompletableFuture.supplyAsync(
                                () -> translateString(str, targetLanguage),
                                executorService
                        );
                        futures.add(future);
                    }

                    // Wait and collect translations
                    for (CompletableFuture<String> future : futures) {
                        translatedMiddleList.add(future.join());
                    }

                    translatedOuterList.add(translatedMiddleList);
                }

                translatedList.add(translatedOuterList);
            }

            return translatedList;
        } finally {
            // Shutdown the executor service
            executorService.shutdown();
        }
    }

    /**
     * Translate a single string
     *
     * @param text Text to translate
     * @param targetLanguage Target language code
     * @return Translated text
     */
    private static String translateString(String text, String targetLanguage) {
        try {
            // Encode the text
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);

            // Construct the full URL
            String fullUrl = String.format(
                    "%s?q=%s&target=%s&key=%s",
                    TRANSLATE_API_URL,
                    encodedText,
                    targetLanguage,
                    API_KEY
            );

            // Create HTTP Client
            HttpClient client = HttpClient.newHttpClient();

            // Create HTTP Request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fullUrl))
                    .GET()
                    .build();

            // Send request and get response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the JSON response
            return parseTranslationResponse(response.body());
        } catch (Exception e) {
            System.err.println("Translation error for text '" + text + "': " + e.getMessage());
            return text; // Return original text if translation fails
        }
    }

    /**
     * Parse the JSON response from Google Translate API
     *
     * @param responseBody JSON response body
     * @return Translated text
     */
    private static String parseTranslationResponse(String responseBody) {
        try {
            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(responseBody);

            return jsonResponse
                    .getJSONObject("data")
                    .getJSONArray("translations")
                    .getJSONObject(0)
                    .getString("translatedText");
        } catch (Exception e) {
            System.err.println("Error parsing translation response: " + e.getMessage());
            return null;
        }
    }

    /**
     * Main method to demonstrate usage
     */
    public static void main(String[] args) throws Exception {

    }
}