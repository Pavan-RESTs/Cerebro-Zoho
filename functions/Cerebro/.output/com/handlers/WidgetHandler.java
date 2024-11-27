//$Id$
package com.handlers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.zc.cliq.enums.ACTION_TYPE;
import com.zc.cliq.enums.SYSTEM_API_ACTION;
import com.zc.cliq.enums.WIDGET_DATATYPE;
import com.zc.cliq.enums.WIDGET_ELEMENT_TYPE;
import com.zc.cliq.enums.WIDGET_EVENT;
import com.zc.cliq.enums.WIDGET_NAVIGATION;
import com.zc.cliq.enums.WIDGET_TYPE;
import com.zc.cliq.objects.WidgetButton;
import com.zc.cliq.objects.WidgetElement;
import com.zc.cliq.objects.WidgetFooter;
import com.zc.cliq.objects.WidgetHeader;
import com.zc.cliq.objects.WidgetInfo;
import com.zc.cliq.objects.WidgetResponse;
import com.zc.cliq.objects.WidgetSection;
import com.zc.cliq.objects.WidgetTab;
import com.zc.cliq.requests.WidgetExecutionHandlerRequest;

import helper_functions.CatalystDatabase;

public class WidgetHandler implements com.zc.cliq.interfaces.WidgetHandler {
	Logger LOGGER = Logger.getLogger(WidgetHandler.class.getName());
	// Helper method to get rank emoji
	private String getRankEmoji(int rank) {
		return switch (rank) {
			case 1 -> "\uD83E\uDD47";
			case 2 -> "\uD83E\uDD48";
			case 3 -> "\uD83E\uDD49";
			default -> "";
		};
	}
	@Override
	public WidgetResponse viewHandler(WidgetExecutionHandlerRequest req) throws Exception {

		WidgetResponse widgetResp = WidgetResponse.getInstance();
		widgetResp.setType(WIDGET_TYPE.APPLET);

		// Define Tabs
		WidgetTab leaderboardTab = WidgetTab.getInstance("leaderboardTab", "Leaderboard 🏆");
		WidgetTab history = WidgetTab.getInstance("history", "Quiz History");
		WidgetTab topPicksForYou = WidgetTab.getInstance("topPicksForYou", "Top picks for you");
		WidgetTab instructionAndAbout = WidgetTab.getInstance("instructionAndAbout", "Instruction & About");

		widgetResp.addTabs(leaderboardTab, history, topPicksForYou, instructionAndAbout);
		widgetResp.setActiveTab(leaderboardTab);

		if (req.getEvent().equals(WIDGET_EVENT.LOAD) || (req.getEvent().equals(WIDGET_EVENT.TAB_CLICK) && req.getTarget().getId().equals("leaderboardTab"))) {

			// Creating table for leaderboard
			WidgetElement tableTitle = WidgetElement.getInstance(WIDGET_ELEMENT_TYPE.TITLE);
			tableTitle.setText("Leaderboard");

			WidgetElement table = WidgetElement.getInstance(WIDGET_ELEMENT_TYPE.TABLE);
			List<HashMap<String, String>> dataL = CatalystDatabase.fetchData("4548000000089204","");

			// Table headers
			table.setHeaders(Arrays.asList("Username", "Level","Location", "Time Spent", "Average",
					 "Total Badges", "Rank"));

// Create a new list for the transformed rows
			List<HashMap<String, String>> transformedDataL = new ArrayList<>();

// Iterate through the original data
			for (int i = 0; i < dataL.size(); i++) {
				HashMap<String, String> originalRow = dataL.get(i);

				// Check if the row contains valid keys before processing
				if (originalRow.containsKey("UserName") && originalRow.containsKey("Level")) {
					HashMap<String, String> row = new HashMap<>();
					row.put("Username", originalRow.get("UserName"));
					row.put("Level", originalRow.get("Level"));
					row.put("Time Spent", originalRow.getOrDefault("TimeSpent", "0")); // Default to "0" if key is missing
					row.put("Average", originalRow.getOrDefault("Average", "0"));     // Default to "0" if key is missing
					row.put("Location", originalRow.getOrDefault("Location", "Unknown")); // Default to "Unknown" if key is missing
					row.put("Total Badges", originalRow.getOrDefault("TotalBadges", "0"));
					row.put("Rank", String.valueOf(i + 1)); // Rank starts from 1

					// Add the transformed row to the new list
					transformedDataL.add(row);
				}
			}

// Set the transformed rows in the table
			table.setRows(transformedDataL);






			// Add the table to the leaderboard section
			WidgetSection leaderboardSection = WidgetSection.getInstance();
			leaderboardSection.addElements(tableTitle, table);
			leaderboardSection.setId("7");
			widgetResp.addSection(leaderboardSection);

		}  else if (req.getEvent().equals(WIDGET_EVENT.REFRESH) || req.getEvent().equals(WIDGET_EVENT.TAB_CLICK)) {
			String target = req.getTarget().getId();

			widgetResp.setActiveTab(target);

			if (target.equals("history")) {
				// Quiz History Section with Table Only
				WidgetElement tableTitle = WidgetElement.getInstance(WIDGET_ELEMENT_TYPE.TITLE);
				tableTitle.setText("Quiz History");

				WidgetElement historyTable = WidgetElement.getInstance(WIDGET_ELEMENT_TYPE.TABLE);
				List<HashMap<String, String>> dataH = CatalystDatabase.fetchData("4548000000082389","");

				// Table headers
				historyTable.setHeaders(Arrays.asList("QuizId", "Topic", "Date", "Score", "Time Taken"));

// Create a new list for the transformed rows
				List<HashMap<String, String>> transformedDataH = new ArrayList<>();

// Iterate through the original data
				for (int i = 0; i < dataH.size(); i++) {
					HashMap<String, String> originalRow = dataH.get(i);

					// Check if the row contains valid keys before processing
					if (originalRow.containsKey("QuizId") && originalRow.containsKey("Score")) {
						HashMap<String, String> row = new HashMap<>();
						row.put("QuizId", originalRow.get("QuizId"));
						row.put("Topic", originalRow.get("QuizTopic"));
						row.put("Date", originalRow.get("QuizDate")); // Default to "0" if key is missing
						row.put("Score", originalRow.get("Score"));     // Default to "0" if key is missing
						row.put("Time Taken", originalRow.get("TimeTaken")); // Default to "Unknown" if key is missing


						// Add the transformed row to the new list
						transformedDataH.add(row);
					}
				}

// Set the transformed rows in the table
				historyTable.setRows(transformedDataH);

				// Add the table to the history section
				WidgetSection historySection = WidgetSection.getInstance();
				historySection.addElements(tableTitle, historyTable);
				historySection.setId("historySection");
				widgetResp.addSection(historySection);

			} else if (target.equals("topPicksForYou")) {

				WidgetElement divider = WidgetElement.getInstance(WIDGET_ELEMENT_TYPE.DIVIDER);

				// Bot
				WidgetElement botTitle = WidgetElement.getInstance(WIDGET_ELEMENT_TYPE.TITLE);
				WidgetElement botText = WidgetElement.getInstance(WIDGET_ELEMENT_TYPE.TEXT);
				botTitle.setText("Bot");
				botText.setText("Bot is your system powered contact or your colleague with which you can interact with as you do with any other person. The bot can be programmed to respond to your queries, to perform action on your behalf and to notify you for any important event.");
				WidgetSection botSection = WidgetSection.getInstance();
				botSection.addElements(botTitle, botText, divider);
				botSection.setId("4");
				widgetResp.addSection(botSection);

				// Widgets
				WidgetElement widgetTitle = WidgetElement.getInstance(WIDGET_ELEMENT_TYPE.TITLE);
				WidgetElement widgetText = WidgetElement.getInstance(WIDGET_ELEMENT_TYPE.TEXT);
				widgetTitle.setText("Widgets");
				widgetText.setText("Widgets are a great way to customize your Cliq home screen. Imagine having a custom view of all the important data and functionality from the different apps that you use every day.");
				WidgetSection widgetsSection = WidgetSection.getInstance();
				widgetsSection.addElements(widgetTitle, widgetText, divider);
				widgetsSection.setId("5");
				widgetResp.addSection(widgetsSection);

				// Connections
				WidgetElement connTitle = WidgetElement.getInstance(WIDGET_ELEMENT_TYPE.TITLE);
				WidgetElement connText = WidgetElement.getInstance(WIDGET_ELEMENT_TYPE.TEXT);
				connTitle.setText("Connections");
				connText.setText("Connections is an interface to integrate third party services with your Zoho Service, in this case, Cliq. These connections are used in an URL invocation task to access authenticated data. To establish a connection, it is necessary to provide a Connection Name, Authentication Type amongst other details.");
				WidgetSection connectionsSection = WidgetSection.getInstance();
				connectionsSection.addElements(connTitle, connText);
				connectionsSection.setId("6");
				widgetResp.addSection(connectionsSection);

			} else if (target.equals("instructionAndAbout")) {

				// Time
				WidgetElement time = WidgetElement.getInstance(WIDGET_ELEMENT_TYPE.SUBTEXT);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				time.setText("Target:buttons\nTime : " + sdf.format(new Date()));
				WidgetSection titleSection = WidgetSection.getInstance();
				titleSection.setId("100");
				titleSection.addElement(time);
				widgetResp.addSection(titleSection);

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
				button2.setLabel("Applet Button");
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

				widgetResp.addSection(buttonSection);
			}

		}

		WidgetButton fistNav = new WidgetButton();
		fistNav.setLabel("Page : 1");
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
		header.setTitle("Header 1");
		header.setNavigation(WIDGET_NAVIGATION.NEW);
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

		return widgetResp;
	}
}
