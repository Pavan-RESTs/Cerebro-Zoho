package helper_functions;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class QuizController {


    public static LocalTime startTime;
    public static LocalTime endTime;

    public static String userId;
    public static String quizId;
    public static String quizTopic;
    public static String quizDifficulty;
    public static String date;



    public static double score = 0.0;

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
