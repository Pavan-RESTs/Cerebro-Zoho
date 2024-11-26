package helper_functions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class MessageBuilder {
        public static Map<String, Object> buildMessage(String question, List<String> options) {

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

    }


