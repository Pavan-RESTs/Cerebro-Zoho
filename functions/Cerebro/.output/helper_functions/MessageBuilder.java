package helper_functions;

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MessageBuilder {

    public static Map<String, Object> quizCard(String question, List<String> options) {

        // Create the main response map
        Map<String, Object> resp = new HashMap<>();

        // Set the text content for the quiz
        resp.put("text", question + "\nA)   " + options.get(0) + "\nB)   " + options.get(1) +
                "\nC)   " + options.get(2) + "\nD)   " + options.get(3));

        // Create the bot information
        Map<String, Object> bot = new HashMap<>();
        bot.put("name", "Cerebro");
        resp.put("bot", bot);

        // Create the card information
        Map<String, Object> card = new HashMap<>();
        int ind = QuizController.qno + 1;
        card.put("title", "Question " + ind + " :");
        resp.put("card", card);

        // Create buttons for options A, B, C, D
        String[] optionNames = {"optionA", "optionB", "optionC", "optionD"};
        String[] labels = {"A", "B", "C", "D"};
        Map<String, Object>[] buttons = new Map[4];

        for (int i = 0; i < 4; i++) {
            Map<String, Object> button = new HashMap<>();
            button.put("type", "+");
            button.put("label", labels[i]);
            Map<String, Object> action = new HashMap<>();
            action.put("type", "invoke.function");
            Map<String, Object> data = new HashMap<>();
            data.put("name", optionNames[i]);
            action.put("data", data);
            button.put("action", action);
            buttons[i] = button;
        }

        // Add buttons to the response
        resp.put("buttons", buttons);

        return resp;
    }

    public static Map<String, Object> quizReviewCard(int score) {
        // Record quiz end time and gather user data
        QuizController.endTime = LocalTime.now();
        String userId = QuizController.userId;
        String quizId = QuizController.quizId;
        String quizTopic = QuizController.quizTopic;
        String timeTaken = String.valueOf(Duration.between(QuizController.startTime, QuizController.endTime).toSeconds());
        String quizDate = QuizController.date;
        double percentage = (score * 1.0 / QuizController.questions.size()) * 100;

//        // Insert quiz result into the database
//        String jsonPayload1 = String.format(
//                "[{\"UserId\":\"%s\",\"QuizId\":\"%s\",\"QuizTopic\":\"%s\",\"TimeTaken\":\"%ss\"," +
//                        "\"QuizDate\":\"%s\",\"Score\":\"%d\"}]",
//                userId, quizId, quizTopic, timeTaken, quizDate, score
//        );
//        CatalystDatabase.insertData("4548000000082389", jsonPayload1);
//
//        List<HashMap<String,String>> leaderboard = CatalystDatabase.fetchData("4548000000089204","");
//        String totalQuiz = leaderboard.get(1).get("TotalQuiz");
//        System.out.println(totalQuiz);
//        int total = Integer.parseInt(totalQuiz);
//        System.out.println(totalQuiz);
//        String timeSpent = leaderboard.get(1).get("TimeSpent");
//        int time = Integer.parseInt(timeSpent);
//        String average = leaderboard.get(1).get("Average");
//        int avg = Integer.parseInt(average);
//        int finalScore = percentage;
//        time = Integer.parseInt(timeTaken) + time;
//        String rowId = "";
//        for (int i=0; i<leaderboard.size(); i++){
//            HashMap<String,String> row = leaderboard.get(i);
//            if (Objects.equals(row.get("UserId"), userId)){
//                rowId = row.get("ROWID");
//            }
//        }
//
//        String jsonPayload2 = String.format("[{\"ROWID\":\"%s\",\"Average\":\"%s\"," +
//                        "\"TimeSpent\":\"%s\",\"TotalBadges\":\"%s\",\"TotalQuiz\":\"%s\"}]",
//                rowId, finalScore, time, 0, total+1);
//
//        CatalystDatabase.updateData("4548000000089204",jsonPayload2);

        // Get review statements based on score
        List<List<String>> statement = InAppData.giveReview();
        String title = "", description = "";

        if (percentage >= 90) {
            title = statement.get(0).get(0);
            description = statement.get(0).get(1);
        } else if (percentage >= 80) {
            title = statement.get(1).get(0);
            description = statement.get(1).get(1);
        } else if (percentage >= 50) {
            title = statement.get(2).get(0);
            description = statement.get(2).get(1);
        } else if (percentage >= 30) {
            title = statement.get(3).get(0);
            description = statement.get(3).get(1);
        } else {
            title = statement.get(4).get(0);
            description = statement.get(4).get(1);
        }
        // Build quiz review card response
        Map<String, Object> resp = new HashMap<>();
        resp.put("text", "Score: " + (int)percentage + "/100"+"\n" + description + "\n\nWould you like to have another quiz?");
        resp.put("bot", Map.of("name", "Cerebro"));
        resp.put("card", Map.of("title", title));

        // Create buttons for review options
        Map<String, Object>[] buttons = new Map[2];
        buttons[0] = Map.of(
                "label", "Yes",
                "type", "+",
                "action", Map.of(
                        "type", "invoke.function",
                        "data", Map.of("name", "continue")
                )
        );
        buttons[1] = Map.of(
                "label", "No",
                "type", "-",
                "action", Map.of(
                        "type", "invoke.function",
                        "data", Map.of("name", "dontContinue")
                )
        );
        resp.put("buttons", buttons);

        QuizController.reset();
        return resp;
    }
}
