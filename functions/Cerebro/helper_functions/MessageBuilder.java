package helper_functions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class MessageBuilder {
        public static Map<String, Object> quizCard(String question, List<String> options) {

            // Create the main response map
            Map<String, Object> resp = new HashMap<String, Object>();

            // Set the text content for the quiz
//            resp.put("text", "What does HTML stand for?\nA) Hyper Text Markup Language\nB) High Text Markup Language\nC) Hyperlinks and Text Markup Language\nD) Home Tool Markup Language");
            resp.put("text", question+"\nA)   "+ options.get(0)+ "\nB)   " + options.get(1)+ "\nC)   " + options.get(2)+ "\nD)   " + options.get(3));

            // Create the bot information
            Map<String, Object> bot = new HashMap<>();
            bot.put("name", "Cerebro");
            resp.put("bot", bot);

            // Create the card information
            Map<String, Object> card = new HashMap<>();
            int ind = QuizController.qno+1;
            card.put("title", "Question "+ ind +" :");
            resp.put("card", card);

            // Create the buttons
            Map<String, Object> buttonA = new HashMap<>();
            buttonA.put("type","+");
            buttonA.put("label", "A");
            Map<String, Object> actionA = new HashMap<>();
            actionA.put("type", "invoke.function");
            Map<String, Object> dataA = new HashMap<>();
            dataA.put("name", "optionA");
            actionA.put("data", dataA);
            buttonA.put("action", actionA);

            Map<String, Object> buttonB = new HashMap<>();
            buttonB.put("type","+");
            buttonB.put("label", "B");
            Map<String, Object> actionB = new HashMap<>();
            actionB.put("type", "invoke.function");
            Map<String, Object> dataB = new HashMap<>();
            dataB.put("name", "optionB");
            actionB.put("data", dataB);
            buttonB.put("action", actionB);

            Map<String, Object> buttonC = new HashMap<>();
            buttonC.put("type","+");
            buttonC.put("label", "C");
            Map<String, Object> actionC = new HashMap<>();
            actionC.put("type", "invoke.function");
            Map<String, Object> dataC = new HashMap<>();
            dataC.put("name", "optionC");
            actionC.put("data", dataC);
            buttonC.put("action", actionC);

            Map<String, Object> buttonD = new HashMap<>();
            buttonD.put("type","+");
            buttonD.put("label", "D");
            Map<String, Object> actionD = new HashMap<>();
            actionD.put("type", "invoke.function");
            Map<String, Object> dataD = new HashMap<>();
            dataD.put("name", "optionD");
            actionD.put("data", dataD);
            buttonD.put("action", actionD);

            // Add all buttons to the buttons array
            Map<String, Object>[] buttons = new Map[]{buttonA, buttonB, buttonC, buttonD};
            resp.put("buttons", buttons);

            // Return the response map
            return resp;
        }

    public static Map<String, Object> quizReviewCard(int score) {

        List<List<String>> statement = InAppData.giveReview();
        String title = "";
        String description = "";
        if (score>=9){
            title = statement.get(0).get(0);
            description = statement.get(0).get(1);

        }
        else if (score>=8 && score<9){
            title = statement.get(1).get(0);
            description = statement.get(1).get(1);

        }else if (score>=5 && score<8){
            title = statement.get(2).get(0);
            description = statement.get(2).get(1);

        }else if (score>=3 && score<5){
            title = statement.get(3).get(0);
            description = statement.get(3).get(1);

        }else {
            title = statement.get(4).get(0);
            description = statement.get(4).get(1);

        }
        // Create the main response map
        Map<String, Object> resp = new HashMap<>();

        // Set the text content with the provided scoreText
        resp.put("text", "Score: "+score+"/100\n"+description+"\n\nWould you like to have another quiz?");

        // Create the bot information
        Map<String, Object> bot = new HashMap<>();
        bot.put("name", "Cerebro");
        resp.put("bot", bot);

        // Create the card information
        Map<String, Object> card = new HashMap<>();
        card.put("title", title);
        resp.put("card", card);

        // Create the buttons
        Map<String, Object> buttonYes = new HashMap<>();
        buttonYes.put("label", "Yes");
        buttonYes.put("type","+");
        Map<String, Object> actionYes = new HashMap<>();
        actionYes.put("type", "invoke.function");
        Map<String, Object> dataYes = new HashMap<>();
        dataYes.put("name", "continue");
        actionYes.put("data", dataYes);
        buttonYes.put("action", actionYes);

        Map<String, Object> buttonNo = new HashMap<>();
        buttonNo.put("label", "No");
        buttonNo.put("type","-");
        Map<String, Object> actionNo = new HashMap<>();
        actionNo.put("type", "invoke.function");
        Map<String, Object> dataNo = new HashMap<>();
        dataNo.put("name", "dontContinue");
        actionNo.put("data", dataNo);
        buttonNo.put("action", actionNo);

        // Add the buttons to an array
        Map<String, Object>[] buttons = new Map[]{buttonYes, buttonNo};
        resp.put("buttons", buttons);

        // Return the response map
        return resp;
    }

    }


