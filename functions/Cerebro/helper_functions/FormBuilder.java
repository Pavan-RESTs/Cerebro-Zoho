package helper_functions;

import java.util.HashMap;
import java.util.Map;

public class FormBuilder {
    public static Map<String, Object> buildForm() {
        // Create the main response map
        Map<String, Object> resp = new HashMap<String, Object>();

        // Set the type and title
        resp.put("type", "form");
        resp.put("title", "Quiz Registration");
        resp.put("name", "quiz");
        resp.put("button_label", "Submit");

        // Create the inputs list for different fields
        Map<String, Object> topicInput = new HashMap<>();
        topicInput.put("label", "Topic");
        topicInput.put("name", "topic");
        topicInput.put("placeholder", "AI, Blockchain, Cybersecurity, etc...");
        topicInput.put("min_length", "0");
        topicInput.put("max_length", "25");
        topicInput.put("mandatory", true);
        topicInput.put("type", "text");

        Map<String, Object> difficultyInput = new HashMap<>();
        difficultyInput.put("label", "Difficulty");
        difficultyInput.put("name", "difficulty");
        difficultyInput.put("mandatory", true);
        difficultyInput.put("type", "radio");

        // Create options for difficulty
        Map<String, Object> optionEasy = new HashMap<>();
        optionEasy.put("value", "easy");
        optionEasy.put("label", "Easy");

        Map<String, Object> optionMedium = new HashMap<>();
        optionMedium.put("value", "medium");
        optionMedium.put("label", "Medium");

        Map<String, Object> optionHard = new HashMap<>();
        optionHard.put("value", "hard");
        optionHard.put("label", "Hard");

        difficultyInput.put("options", new Object[] { optionEasy, optionMedium, optionHard });

        Map<String, Object> numberInput = new HashMap<>();
        numberInput.put("label", "No of questions");
        numberInput.put("name", "number");
        numberInput.put("placeholder", "Max 8 due to api charges");
        numberInput.put("min", "0");
        numberInput.put("max", "8");
        numberInput.put("mandatory", true);
        numberInput.put("type", "number");

        Map<String, Object> languageInput = new HashMap<>();
        languageInput.put("label", "Language");
        languageInput.put("name", "language");
        languageInput.put("placeholder", "Tamil, Hindi, English, etc...");
        languageInput.put("multiple", false);
        languageInput.put("mandatory", true);
        languageInput.put("type", "dynamic_select");

        // Create options for language selection
        Map<String, Object> optionTamil = new HashMap<>();
        optionTamil.put("value", "tamil");
        optionTamil.put("label", "Tamil");

        Map<String, Object> optionEnglish = new HashMap<>();
        optionEnglish.put("value", "english");
        optionEnglish.put("label", "English");

        Map<String, Object> optionTelugu = new HashMap<>();
        optionTelugu.put("value", "telugu");
        optionTelugu.put("label", "Telugu");

        Map<String, Object> optionHindi = new HashMap<>();
        optionHindi.put("value", "hindi");
        optionHindi.put("label", "Hindi");

        languageInput.put("options", new Object[] { optionTamil, optionEnglish, optionTelugu, optionHindi });

        // Add all inputs to the form
        resp.put("inputs", new Object[] { topicInput, difficultyInput, numberInput, languageInput });

        // Create the action map
        Map<String, Object> action = new HashMap<>();
        action.put("type", "invoke.function");
        action.put("name", "quiz");

        // Add the action to the response map
        resp.put("action", action);

        // Output the map for verification
        return resp;
    }
}
