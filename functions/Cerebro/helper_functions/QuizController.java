package helper_functions;

import java.util.ArrayList;
import java.util.List;

public class QuizController {

    public static int score = 0;

    public static int qno = 0;

    public static List<List<String>> questions = new ArrayList<>();
    public static List<List<String>> options = new ArrayList<>();
    public static List<List<String>> answers = new ArrayList<>();

    // Reset method to clear out quiz data
    public static void reset(){
        qno = 0;
        score = 0;
        questions.clear();  // Clears the list instead of reinitializing (use clear to avoid potential memory overhead)
        options.clear();    // Same for options
        answers.clear();    // Same for answers
    }
}
