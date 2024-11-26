package helper_functions;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class GeminiAPI {

    // API key and base URL for the Gemini API
    private static final String API_KEY = "AIzaSyAuCDNpP8dsbQXpk9HFTBtvu2F_u14Czcw";
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    // Function to generate a quiz based on given parameters
    public static String quizGenerator(String topic, String difficulty, String noOfQuestions) throws Exception {
        // Prompt defining the structure and parameters of the quiz
        String prompt = "You are a quiz generation assistant. Create a quiz based on the following parameters. Ensure the output strictly follows the JSON format provided below without any additional text or explanations.\n" +
                "Additionally, ensure the following:\n" +
                "- Every time this prompt is processed, the quiz content (questions, options, and answers) must be different from any previously generated quiz for the same parameters.\n" +
                "- Introduce randomization and variety in the content to ensure uniqueness in every response.\n" +
                "- Do not repeat questions, options, or answers previously generated even if the parameters are the same.\n" +
                "- Strictly adhere to the JSON format and structure without deviations.\n" +
                "\n" +
                "Response Format (JSON):\n" +
                "{\n" +
                "  \"Questions\": [\"q1\", \"q2\", \"q3\", ...],\n" +
                "  \"Options\": {\n" +
                "    \"o1\": [\"option1\", \"option2\", \"option3\", \"option4\"],\n" +
                "    \"o2\": [\"option1\", \"option2\", \"option3\", \"option4\"],\n" +
                "    ...\n" +
                "  },\n" +
                "  \"Answers\": [\"a1\", \"a2\", \"a3\", ...]\n" +
                "}\n" +
                "\n" +
                "Parameters:\n" +
                "- Topic: " + topic + "\n" +
                "- Difficulty: " + difficulty + "\n" +
                "- Number of Questions: " + noOfQuestions + "\n" +
                "Generate the quiz in the specified format without deviations and ensure variability in the response each time this prompt is executed.";


        OkHttpClient client = new OkHttpClient();



        // Prepare the JSON request body with temperature
        JSONObject parts = new JSONObject().put("text", prompt);
        JSONArray partsArray = new JSONArray().put(parts);

// Add temperature to generationConfig
        JSONObject generationConfig = new JSONObject()
                .put("temperature", 1.0); // Set the temperature for randomness

// Build the JSON request body
        JSONObject contents = new JSONObject().put("parts", partsArray);
        JSONObject requestBody = new JSONObject()
                .put("contents", new JSONArray().put(contents))
                .put("generationConfig", generationConfig); // Add generationConfig with temperature

// Build the HTTP POST request
        Request request = new Request.Builder()
                .url(BASE_URL + "?key=" + API_KEY)
                .post(RequestBody.create(MediaType.parse("application/json"), requestBody.toString()))
                .addHeader("Content-Type", "application/json")
                .build();


        // Send the request and handle the response
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {

                return response.body().string(); // Return the JSON response as a string
            } else {
                throw new Exception("Request failed with code: " + response.code());
            }
        }
    }

    // Function to parse the quiz JSON response into structured data

    public static List<List<List<String>>> parseQuizResponse(String jsonResponse) throws Exception {
        List<List<List<String>>> quizData = new ArrayList<>();

        // Parse the response and extract relevant data
        JSONObject responseObj = new JSONObject(jsonResponse);
        JSONArray candidatesArray = responseObj.getJSONArray("candidates");

        // Extract the first candidate's content
        JSONObject firstCandidate = candidatesArray.getJSONObject(0);
        String rawContent = firstCandidate.getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text");

        // Clean the content and convert it into a JSON object
        String cleanContent = rawContent.replaceAll("```json|```", "").trim();
        JSONObject quizObject = new JSONObject(cleanContent);

        // Extract questions
        JSONArray questionsArray = quizObject.getJSONArray("Questions");
        List<List<String>> questionsList = new ArrayList<>();
        for (int i = 0; i < questionsArray.length(); i++) {
            List<String> question = new ArrayList<>();
            question.add(questionsArray.getString(i));
            questionsList.add(question);
        }

        // Extract options
        JSONObject optionsObject = quizObject.getJSONObject("Options");
        List<List<String>> optionsList = new ArrayList<>();
        Iterator<String> keys = optionsObject.keys(); // Iterate through option keys
        while (keys.hasNext()) {
            String key = keys.next();
            JSONArray optionArray = optionsObject.getJSONArray(key);

            // Convert the JSONArray into a List<String>
            List<String> optionList = new ArrayList<>();
            for (int i = 0; i < optionArray.length(); i++) {
                optionList.add(optionArray.getString(i));
            }

            // Shuffle the options for the current question
            Collections.shuffle(optionList);
            optionsList.add(optionList); // Add the shuffled option set as a list
        }

        // Extract answers
        JSONArray answersArray = quizObject.getJSONArray("Answers");
        List<List<String>> answersList = new ArrayList<>();
        for (int i = 0; i < answersArray.length(); i++) {
            List<String> answer = new ArrayList<>();
            answer.add(answersArray.getString(i));
            answersList.add(answer);
        }

        // Add structured data to the quizData list
        quizData.add(questionsList); // Add questions
        quizData.add(optionsList);   // Add shuffled options
        quizData.add(answersList);   // Add answers

        return quizData; // Return the structured quiz data
    }




    // Main method for testing the Gemini API integration
    public static void main(String[] args) {
        try {
            String response = quizGenerator("SAAS", "Easy", "2"); // Generate a quiz
            List<List<List<String>>> quizData = parseQuizResponse(response); // Parse the quiz response
            QuizController.questions = quizData.get(0);
            QuizController.options = quizData.get(1);
            QuizController.answers = quizData.get(2);
            System.out.println(quizData);
        } catch (Exception e) {
            System.err.println("Error communicating with Gemini: " + e.getMessage()); // Handle errors
        }
    }
}
