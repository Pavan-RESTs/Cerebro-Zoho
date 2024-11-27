package com.handlers;

import static helper_functions.GeminiAPI.parseQuizResponse;
import static helper_functions.GeminiAPI.quizGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.zc.cliq.enums.ACTION_TYPE;
import com.zc.cliq.enums.BUTTON_TYPE;
import com.zc.cliq.enums.CHANNEL_OPERATION;
import com.zc.cliq.enums.SLIDE_TYPE;
import com.zc.cliq.objects.Action;
import com.zc.cliq.objects.ActionData;
import com.zc.cliq.objects.BotContext;
import com.zc.cliq.objects.BotContextParam;
import com.zc.cliq.objects.BotSuggestion;
import com.zc.cliq.objects.ButtonObject;
import com.zc.cliq.objects.CardDetails;
import com.zc.cliq.objects.Message;
import com.zc.cliq.objects.Slide;
import com.zc.cliq.requests.BotContextHandlerRequest;
import com.zc.cliq.requests.BotMentionHandlerRequest;
import com.zc.cliq.requests.BotMenuActionHandlerRequest;
import com.zc.cliq.requests.BotMessageHandlerRequest;
import com.zc.cliq.requests.BotParticipationHandlerRequest;
import com.zc.cliq.requests.BotWebhookHandlerRequest;
import com.zc.cliq.requests.BotWelcomeHandlerRequest;
import com.zc.cliq.util.ZCCliqUtil;
import com.zc.component.cache.ZCCache;

import helper_functions.CatalystDatabase;
import helper_functions.FormBuilder;
import helper_functions.InAppData;
import helper_functions.MessageBuilder;
import helper_functions.QuizController;

public class BotHandler implements com.zc.cliq.interfaces.BotHandler {
	Logger LOGGER = Logger.getLogger(BotHandler.class.getName());

	@Override
	public Map<String, Object> welcomeHandler(BotWelcomeHandlerRequest req) {
		String userId = req.getUser().getId();
		String userName = req.getUser().getFirstName() + " " + req.getUser().getLastName();
		String userEmail = req.getUser().getEmail();
		String userLocation = InAppData.returnCountryName(req.getUser().getCountry());

		LOGGER.info("Welcome handler triggered for user: " + userName + " (" + userEmail + ")");

		String jsonPayload = String.format("[{\"UserId\":\"%s\",\"UserName\":\"%s\"," +
				"\"UserEmail\":\"%s\",\"UserLocation\":\"%s\"}]", userId, userName, userEmail, userLocation);

		CatalystDatabase.insertData("4548000000087860", jsonPayload);
		LOGGER.info("Inserted user data into database with payload: " + jsonPayload);

		jsonPayload = String.format("[{\"UserId\":\"%s\",\"UserName\":\"%s\"," +
				"\"Location\":\"%s\"}]", userId, userName, userLocation);

		CatalystDatabase.insertData("4548000000089204", jsonPayload);
		LOGGER.info("Inserted user location into database with payload: " + jsonPayload);

		String uName = req.getUser() != null ? req.getUser().getFirstName() : "user";
		String text = "Hello " + uName + ". Thank you for subscribing :smile:\n";
		Message msg = Message.getInstance(text);
		LOGGER.info("Sending welcome message: " + text);
		return ZCCliqUtil.toMap(msg);
	}

	@Override
	public Map<String, Object> messageHandler(BotMessageHandlerRequest req) throws Exception {
		try {
			String message = req.getMessage();
			Map<String, Object> resp = new HashMap<>();
			String text = "";

			LOGGER.info("Received message: " + message);

			if (message == null) {
				text = "Please enable 'Message' in bot settings";
			} else if (message.equalsIgnoreCase("hi") || message.equalsIgnoreCase("hey")) {
				text = "Hi " + req.getUser().getFirstName() + " :smile: How are you doing?";
				BotSuggestion suggestion = BotSuggestion.getInstance();
				suggestion.addSuggestion("Good");
				suggestion.addSuggestion("Not bad");
				suggestion.addSuggestion("Meh");
				suggestion.addSuggestion("Worst");
				resp.put("suggestions", suggestion);
			} else if (message.equalsIgnoreCase("quiz")) {
				LOGGER.info("Quiz generation has started!");
				return FormBuilder.buildForm();
			}else if (message.equalsIgnoreCase("api")) {
				List<HashMap<String,String>> leaderboard = CatalystDatabase.fetchData("4548000000089204", "");

				String totalQuiz = leaderboard.get(1).get("TotalQuiz");
				LOGGER.info("API request received. Total quizzes: " + totalQuiz);
				resp.put("text", totalQuiz + "sf");
				return resp;
			}else {
				text = "Sorry, I'm not programmed yet to do this :sad:";
			}

			resp.put("text", text);
			LOGGER.info("Sending response: " + text);
			return resp;

		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, "Exception in message handler. ", ex);
			throw ex;
		}
	}



	@Override
	public Map<String, Object> mentionHandler(BotMentionHandlerRequest req) {

		String text = "Hey *" + req.getUser().getFirstName() + "*, thanks for mentioning me here. I'm from Catalyst city";
		Map<String, Object> resp = new HashMap<String, Object>();
		resp.put("text", text);

		LOGGER.info("Mention received from user: " + req.getUser().getFirstName());
		return resp;
	}

	@Override
	public Map<String, Object> contextHandler(BotContextHandlerRequest botContextHandlerRequest) throws Exception {
		return Map.of();
	}

	@Override
	public Map<String, Object> webhookHandler(BotWebhookHandlerRequest botWebhookHandlerRequest) throws Exception {
		return Map.of();
	}

	@Override
	public Map<String, Object> menuActionHandler(BotMenuActionHandlerRequest req) {
		Map<String, Object> resp = new HashMap<String, Object>();
		String text;
		if (req.getActionName().equals("Say Hi")) {
			text = "Hi";
		} else if (req.getActionName().equals("Look Angry")) {
			text = ":angry:";
		} else {
			text = "Menu action triggered :fist:";
		}
		resp.put("text", text);

		LOGGER.info("Menu action received: " + req.getActionName());
		return resp;
	}


	@Override
	public Map<String, Object> participationHandler(BotParticipationHandlerRequest req) throws Exception {
		String text;
		if (req.getOperation().equals(CHANNEL_OPERATION.ADDED)) {
			text = "Hi. Thanks for adding me to the channel :smile:";
		} else if (req.getOperation().equals(CHANNEL_OPERATION.REMOVED)) {
			text = "Bye-Bye :bye-bye:";
		} else {
			text = "I'm too a participant of this chat :wink:";
		}
		Message msg = Message.getInstance(text);

		LOGGER.info("Participation handler triggered with operation: " + req.getOperation());
		return ZCCliqUtil.toMap(msg);
	}
}
