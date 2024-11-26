//$Id$
package com.handlers;

import static helper_functions.GeminiAPI.parseQuizResponse;
import static helper_functions.GeminiAPI.quizGenerator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.zc.cliq.enums.ACTION_TYPE;
import com.zc.cliq.enums.BANNER_STATUS;
import com.zc.cliq.enums.BUTTON_TYPE;
import com.zc.cliq.enums.FORM_FIELD_TYPE;
import com.zc.cliq.enums.FORM_MODIFICATION_ACTION_TYPE;
import com.zc.cliq.enums.SYSTEM_API_ACTION;
import com.zc.cliq.enums.WIDGET_ELEMENT_TYPE;
import com.zc.cliq.enums.WIDGET_NAVIGATION;
import com.zc.cliq.enums.WIDGET_TYPE;
import com.zc.cliq.objects.Action;
import com.zc.cliq.objects.ActionData;
import com.zc.cliq.objects.ButtonObject;
import com.zc.cliq.objects.Form;
import com.zc.cliq.objects.FormAction;
import com.zc.cliq.objects.FormChangeResponse;
import com.zc.cliq.objects.FormDynamicFieldResponse;
import com.zc.cliq.objects.FormInput;
import com.zc.cliq.objects.FormModificationAction;
import com.zc.cliq.objects.FormTarget;
import com.zc.cliq.objects.FormValue;
import com.zc.cliq.objects.Message;
import com.zc.cliq.objects.WidgetButton;
import com.zc.cliq.objects.WidgetElement;
import com.zc.cliq.objects.WidgetFooter;
import com.zc.cliq.objects.WidgetHeader;
import com.zc.cliq.objects.WidgetResponse;
import com.zc.cliq.objects.WidgetSection;
import com.zc.cliq.requests.ButtonFunctionRequest;
import com.zc.cliq.requests.FormFunctionRequest;
import com.zc.cliq.requests.WidgetFunctionRequest;
import com.zc.cliq.util.ZCCliqUtil;

import helper_functions.FormBuilder;
import helper_functions.LanguageTranslation;
import helper_functions.MessageAPI;
import helper_functions.MessageBuilder;
import helper_functions.QuizController;

public class FunctionHandler implements com.zc.cliq.interfaces.FunctionHandler {

	Logger LOGGER = Logger.getLogger(FunctionHandler.class.getName());

	@Override
	public Map<String, Object> buttonFunctionHandler(ButtonFunctionRequest req) throws Exception {
		Message msg = Message.getInstance("Button function executed");
		String buttonName = req.getName();
		String messageId = req.getMessage().getId();
		String chatId = req.getChat().getId();
		MessageAPI.deleteMessage(chatId,messageId);

		Map<String, Object> resp = new HashMap<>();
		String text = "";
		if (buttonName.equalsIgnoreCase(("yesButton"))){
			// Start the first question
			if (QuizController.questions.isEmpty()) {
				text = "No quiz data found. Please try again.";
				resp.put("text", text);
				return resp;
			}

			// Build and send the first question
			int ind = QuizController.qno;
			return MessageBuilder.quizCard(
					QuizController.questions.get(ind).get(0),
					QuizController.options.get(ind)
			);
		} else if (buttonName.equalsIgnoreCase(("continue"))){
			return FormBuilder.buildForm();
		}else if (buttonName.equalsIgnoreCase(("dontContinue"))){
			msg = Message.getInstance("All right see you later!");
		}

		else if (buttonName.equalsIgnoreCase("optionA") || buttonName.equalsIgnoreCase("optionB") ||
		buttonName.equalsIgnoreCase("optionC") || buttonName.equalsIgnoreCase("optionD")) {
			int currentInd = QuizController.qno;
			String ans = "";
			if (buttonName.equalsIgnoreCase("optionA")){
				ans = QuizController.options.get(currentInd).get(0);
			} else if (buttonName.equalsIgnoreCase("optionB")){
				ans = QuizController.options.get(currentInd).get(1);
			}else if (buttonName.equalsIgnoreCase("optionC")){
				ans = QuizController.options.get(currentInd).get(2);
			}else if (buttonName.equalsIgnoreCase("optionD")){
				ans = QuizController.options.get(currentInd).get(3);
			}

			if (Objects.equals(ans, QuizController.answers.get(currentInd).get(0))){
				QuizController.score = QuizController.score + 1;
			}
			if (QuizController.qno < QuizController.questions.size()-1) {
				// Send the next question
				int nextInd = ++QuizController.qno;
				return MessageBuilder.quizCard(
						QuizController.questions.get(nextInd).get(0),
						QuizController.options.get(nextInd)
				);
			}
			else {

				int percentage = (QuizController.score/QuizController.questions.size())*100;
				QuizController.reset();
				return MessageBuilder.quizReviewCard(percentage);
			}
		}
		else if (buttonName.equalsIgnoreCase(("noButton"))){
			msg = Message.getInstance("Quiz has been revoked");
		}
		return ZCCliqUtil.toMap(msg);
	}

	@Override
	public Map<String, Object> formSubmitHandler(FormFunctionRequest req) throws Exception {
		// Retrieve submitted form values
		JSONObject values = req.getForm().getValues();

		// Access specific form fields using their "name" property
		String topic = values.optString("topic", ""); // Retrieves the topic
		String difficulty = values.optString("difficulty", ""); // Retrieves the difficulty level

		if (difficulty.contains("Easy")) {
			difficulty = "Easy";
		} else if (difficulty.contains("Medium")) {
			difficulty = "Medium";
		} else if (difficulty.contains("Hard")) {
			difficulty = "Hard";
		}

		String number = values.optString("number", ""); // Retrieves the number of questions
		String language = values.optString("language", ""); // Retrieves the language

		if (language.contains("Tamil")) {
			language = "Tamil";
		} else if (language.contains("English")) {
			language = "English";
		} else if (language.contains("Telugu")) {
			language = "Telugu";
		} else if (language.contains("Hindi")) {
			language = "Hindi";
		}

		String response = quizGenerator(topic,difficulty,number);
		List<List<List<String>>> quizData = parseQuizResponse(response);
		if (Objects.equals(language,"Tamil")){
			quizData = LanguageTranslation.translateNestedList(quizData,"ta");
		}
		else if (Objects.equals(language,"Telugu")){
			quizData = LanguageTranslation.translateNestedList(quizData,"te");
		}
		else if (Objects.equals(language,"Hindi")){
			quizData = LanguageTranslation.translateNestedList(quizData,"hi");
		}
		QuizController.questions = quizData.get(0);
		QuizController.options = quizData.get(1);
		QuizController.answers = quizData.get(2);



		// Create a response message
		Message msg = Message.getInstance("Quiz generated successfully.\nWould you like to start the quiz?");
		ButtonObject yesButton = new ButtonObject();
		yesButton.setType(BUTTON_TYPE.GREEN_OUTLINE);
		yesButton.setLabel("Yes");
		Action action = new Action();
		action.setType(ACTION_TYPE.INVOKE_FUNCTION);
		ActionData actionData = new ActionData();
		actionData.setName("yesButton");
		action.setData(actionData);
		yesButton.setAction(action);
		msg.addButton(yesButton);

		ButtonObject noButton = new ButtonObject();
		noButton.setType(BUTTON_TYPE.RED_OUTLINE);
		noButton.setLabel("No");
		Action action1 = new Action();
		action1.setType(ACTION_TYPE.INVOKE_FUNCTION);
		ActionData actionData1 = new ActionData();
		actionData1.setName("noButton");
		action1.setData(actionData1);
		noButton.setAction(action1);
		msg.addButton(noButton);





		// Return the response
		return ZCCliqUtil.toMap(msg);
	}



	@Override
	public FormChangeResponse formChangeHandler(FormFunctionRequest req) throws Exception {
		FormChangeResponse resp = FormChangeResponse.getInstance();
		String target = req.getTarget().getName();
		JSONObject values = req.getForm().getValues();
		String fieldValue = ((JSONObject) values.get("asset-type")).get("value").toString();
		if (target.equalsIgnoreCase("asset-type")) {

			if (fieldValue.equals("laptop")) {
				FormModificationAction selectBoxAction = FormModificationAction.getInstance();
				selectBoxAction.setType(FORM_MODIFICATION_ACTION_TYPE.ADD_AFTER);
				selectBoxAction.setName("asset-type");
				FormInput OS = FormInput.getIntance();
				OS.setTriggerOnChange(true);
				OS.setType(FORM_FIELD_TYPE.SELECT);
				OS.setName("os-type");
				OS.setLabel("Laptop Type");
				OS.setHint("Choose your preferred OS type");
				OS.setPlaceholder("Ubuntu");
				OS.setMandatory(true);
				FormValue mac = new FormValue();
				mac.setLabel("Mac OS X");
				mac.setValue("mac");
				FormValue windows = new FormValue();
				windows.setLabel("Windows");
				windows.setValue("windows");
				FormValue ubuntu = new FormValue();
				ubuntu.setLabel("Ubuntu");
				ubuntu.setValue("ubuntu");
				OS.addOption(mac);
				OS.addOption(windows);
				OS.addOption(ubuntu);
				selectBoxAction.setInput(OS);

				FormModificationAction removeMobileOSAction = FormModificationAction.getInstance();
				removeMobileOSAction.setType(FORM_MODIFICATION_ACTION_TYPE.REMOVE);
				removeMobileOSAction.setName("mobile-os");

				FormModificationAction removeMobileListAction = FormModificationAction.getInstance();
				removeMobileListAction.setType(FORM_MODIFICATION_ACTION_TYPE.REMOVE);
				removeMobileListAction.setName("mobile-list");

				resp.addAction(selectBoxAction);
				resp.addAction(removeMobileOSAction);
				resp.addAction(removeMobileListAction);
			} else if (fieldValue.equals("mobile")) {
				FormModificationAction selectBoxAction = FormModificationAction.getInstance();
				selectBoxAction.setType(FORM_MODIFICATION_ACTION_TYPE.ADD_AFTER);
				selectBoxAction.setName("asset-type");
				FormInput OS = FormInput.getIntance();
				OS.setTriggerOnChange(true);
				OS.setType(FORM_FIELD_TYPE.SELECT);
				OS.setName("mobile-os");
				OS.setLabel("Mobile OS");
				OS.setHint("Choose your preferred mobile OS");
				OS.setPlaceholder("Android");
				OS.setMandatory(true);
				FormValue android = new FormValue();
				android.setLabel("Android");
				android.setValue("android");
				FormValue ios = new FormValue();
				ios.setLabel("iOS");
				ios.setValue("ios");
				OS.addOption(android);
				OS.addOption(ios);
				selectBoxAction.setInput(OS);

				FormModificationAction removeOSTypeAction = FormModificationAction.getInstance();
				removeOSTypeAction.setType(FORM_MODIFICATION_ACTION_TYPE.REMOVE);
				removeOSTypeAction.setName("os-type");

				resp.addAction(selectBoxAction);
				resp.addAction(removeOSTypeAction);
			}

		} else if (target.equalsIgnoreCase("mobile-os")) {

			if (fieldValue != null) {
				FormModificationAction mobileListAction = FormModificationAction.getInstance();
				mobileListAction.setType(FORM_MODIFICATION_ACTION_TYPE.ADD_AFTER);
				mobileListAction.setName("mobile-os");
				FormInput listInput = FormInput.getIntance();
				listInput.setType(FORM_FIELD_TYPE.DYNAMIC_SELECT);
				listInput.setName("mobile-list");
				listInput.setLabel("Mobile Device");
				listInput.setPlaceholder("Choose your preferred mobile device");
				listInput.setMandatory(true);
				mobileListAction.setInput(listInput);

				resp.addAction(mobileListAction);
			} else {
				FormModificationAction removeMobileListAction = FormModificationAction.getInstance();
				removeMobileListAction.setType(FORM_MODIFICATION_ACTION_TYPE.REMOVE);
				removeMobileListAction.setName("mobile-list");
				resp.addAction(removeMobileListAction);
			}
		}
		return resp;
	}

	@Override
	public FormDynamicFieldResponse formDynamicFieldHandler(FormFunctionRequest req) throws Exception {
		FormDynamicFieldResponse resp = FormDynamicFieldResponse.getInstance();
		FormTarget target = req.getTarget();
		String query = target.getQuery();
		JSONObject values = req.getForm().getValues();
		if (target.getName().equals("mobile-list") && !values.get("mobile-os").toString().isEmpty()) {
			String device = values.getJSONObject("mobile-os").getString("value");
			if (device.equals("android")) {
				Arrays.stream(new String[] { "One Plus 6T", "One Plus 6", "Google Pixel 3", "Google Pixel 2XL" }).filter(phone -> phone.contains(query)).forEach(phone -> resp.addOption(new FormValue(phone, phone.toLowerCase().replace(" ", "_"))));
			} else if (device.equals("ios")) {
				Arrays.stream(new String[] { "IPhone XR", "IPhone XS", "IPhone X", "Iphone 8 Plus" }).filter(phone -> phone.contains(query)).forEach(phone -> resp.addOption(new FormValue(phone, phone.toLowerCase().replace(" ", "_"))));
			}
		}
		return resp;
	}

	@Override
	public Map<String, Object> widgetButtonHandler(WidgetFunctionRequest req) throws Exception {
		ButtonObject target = req.getTarget();
		String id = target.getId();
		switch (id) {
		case "tab": {

			WidgetResponse widgetResp = WidgetResponse.getInstance();
			widgetResp.setType(WIDGET_TYPE.APPLET);

			// Time
			WidgetElement time = WidgetElement.getInstance(WIDGET_ELEMENT_TYPE.SUBTEXT);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			time.setText("Target:buttons\nTime : " + sdf.format(new Date()));
			WidgetSection titleSection = WidgetSection.getInstance();
			titleSection.addElement(time);
			titleSection.setId("100");
			widgetResp.addSection(titleSection);

			WidgetSection buttonSection = getButtonsSection();

			widgetResp.addSection(buttonSection);
			return ZCCliqUtil.toMap(widgetResp);
		}

		case "section": {
			WidgetSection section = WidgetSection.getInstance();
			section.setId("102");
			section.setType("section");
			WidgetElement element = WidgetElement.getInstance(WIDGET_ELEMENT_TYPE.TITLE);
			element.setText("Edited :wink:");
			section.addElement(element);
			return ZCCliqUtil.toMap(section);
		}

		case "formtab":
		case "formsection": {
			Form form = Form.getInstance();
			form.setTitle("Zylker Annual Marathon");
			form.setName("a");
			form.setHint("Register yourself for the Zylker Annual Marathon!");
			form.setButtonLabel("Submit");
			FormInput input1 = FormInput.getIntance();
			input1.setType(FORM_FIELD_TYPE.TEXT);
			input1.setName("text");
			input1.setLabel("Name");
			input1.setPlaceholder("Scott Fischer");
			input1.setMinLength("0");
			input1.setMaxLength("25");
			input1.setMandatory(true);

			FormInput input2 = FormInput.getIntance();
			input2.setType(FORM_FIELD_TYPE.HIDDEN);
			input2.setName("type");
			input2.setValue(id);

			form.addFormInput(input1);
			form.addFormInput(input2);
			form.setAction(FormAction.getInstance("appletForm"));// ** ENTER YOUR FORM FUNCTION NAME HERE **
			return ZCCliqUtil.toMap(form);
		}

		case "breadcrumbs":

			Integer page = Integer.parseInt(target.getLabel().split("Page : ")[1].trim()) + 1;
			WidgetResponse widgetResp = WidgetResponse.getInstance();
			widgetResp.setType(WIDGET_TYPE.APPLET);
			WidgetElement elem = WidgetElement.getInstance(WIDGET_ELEMENT_TYPE.SUBTEXT);
			elem.setText("Page " + page);
			WidgetSection titleSection = WidgetSection.getInstance();
			titleSection.addElement(elem);
			titleSection.setId("12345");
			widgetResp.addSection(titleSection);

			WidgetButton fistNav = new WidgetButton();
			fistNav.setLabel("Page : " + page);
			fistNav.setType(ACTION_TYPE.INVOKE_FUNCTION);
			fistNav.setName("appletFunction");
			fistNav.setId("breadcrumbs");

			WidgetButton linkButton = new WidgetButton();
			linkButton.setLabel("Link");
			linkButton.setType(ACTION_TYPE.OPEN_URL);
			linkButton.setUrl("https://www.zoho.com");

			WidgetButton bannerBtn = new WidgetButton();
			bannerBtn.setLabel("Banner");
			bannerBtn.setType(ACTION_TYPE.INVOKE_FUNCTION);
			bannerBtn.setName("appletFunction");
			bannerBtn.setId("banner");

			WidgetHeader header = WidgetHeader.getInstance();
			header.setTitle("Header " + page);
			header.setNavigation(WIDGET_NAVIGATION.CONTINUE);
			List<WidgetButton> headerButtons = new ArrayList<WidgetButton>();
			headerButtons.add(fistNav);
			headerButtons.add(bannerBtn);
			headerButtons.add(linkButton);
			header.setButtons(headerButtons);
			widgetResp.setHeader(header);

			WidgetFooter footer = WidgetFooter.getInstance();
			footer.setText("Footer Text");
			List<WidgetButton> footerButtons = new ArrayList<WidgetButton>();
			footerButtons.add(bannerBtn);
			footerButtons.add(linkButton);
			footer.setButtons(footerButtons);
			widgetResp.setFooter(footer);

			return ZCCliqUtil.toMap(widgetResp);

		case "banner":
		default: {
			Message msg = Message.getInstance("Applet Button executed successfully");
			msg.setBannerResponse(BANNER_STATUS.SUCCESS);
			return ZCCliqUtil.toMap(msg);
		}
		}
	}

	private WidgetSection getButtonsSection() {
		WidgetSection buttonSection = WidgetSection.getInstance();

		// Buttons - Row1
		WidgetElement title = WidgetElement.getInstance(WIDGET_ELEMENT_TYPE.TITLE);
		title.setText("Buttons");

		WidgetElement buttonElement1 = WidgetElement.getInstance(WIDGET_ELEMENT_TYPE.BUTTONS);
		List<WidgetButton> buttonsList1 = new ArrayList<WidgetButton>();
		WidgetButton button1 = new WidgetButton();
		button1.setLabel("Link");
		button1.setType(ACTION_TYPE.OPEN_URL);
		button1.setUrl("https://www.zoho.com");

		WidgetButton button2 = new WidgetButton();
		button2.setLabel("Banner");
		button2.setType(ACTION_TYPE.INVOKE_FUNCTION);
		button2.setName("appletFunction");
		button2.setId("banner");

		WidgetButton button3 = new WidgetButton();
		button3.setLabel("Open Channel");
		button3.setType(ACTION_TYPE.SYSTEM_API);
		button3.setApi(SYSTEM_API_ACTION.JOIN_CHANNEL, "CD_1283959962893705602_14598233");// ** ENTER YOUR CHANNEL ID HERE **

		WidgetButton button4 = new WidgetButton();
		button4.setLabel("Preview");
		button4.setType(ACTION_TYPE.PREVIEW_URL);
		button4.setUrl("https://www.zoho.com/catalyst/features.html");

		buttonsList1.add(button1);
		buttonsList1.add(button2);
		buttonsList1.add(button3);
		buttonsList1.add(button4);

		buttonElement1.setButtons(buttonsList1);

		// Buttons - Row2
		WidgetElement buttonElement2 = WidgetElement.getInstance(WIDGET_ELEMENT_TYPE.BUTTONS);
		List<WidgetButton> buttonsList2 = new ArrayList<WidgetButton>();
		WidgetButton button5 = new WidgetButton();
		button5.setLabel("Edit Section");
		button5.setType(ACTION_TYPE.INVOKE_FUNCTION);
		button5.setName("appletFunction");
		button5.setId("section");

		WidgetButton button6 = new WidgetButton();
		button6.setLabel("Form Edit Section");
		button6.setType(ACTION_TYPE.INVOKE_FUNCTION);
		button6.setName("appletFunction");
		button6.setId("formsection");

		WidgetButton button7 = new WidgetButton();
		button7.setLabel("Banner");
		button7.setType(ACTION_TYPE.INVOKE_FUNCTION);
		button7.setName("appletFunction");
		button7.setId("banner");

		WidgetButton button8 = new WidgetButton();
		button8.setLabel("Edit Whole Tab");
		button8.setType(ACTION_TYPE.INVOKE_FUNCTION);
		button8.setName("appletFunction");
		button8.setId("tab");

		WidgetButton button9 = new WidgetButton();
		button9.setLabel("Form Edit Tab");
		button9.setType(ACTION_TYPE.INVOKE_FUNCTION);
		button9.setName("appletFunction");
		button9.setId("formtab");

		buttonsList2.add(button5);
		buttonsList2.add(button6);
		buttonsList2.add(button7);
		buttonsList2.add(button8);
		buttonsList2.add(button9);

		buttonElement2.setButtons(buttonsList2);

		buttonSection.addElement(title);
		buttonSection.addElement(buttonElement1);
		buttonSection.addElement(buttonElement2);
		buttonSection.setId("101");

		return buttonSection;
	}
}
