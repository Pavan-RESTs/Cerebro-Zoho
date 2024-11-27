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

		// Define Tabs, only including Leaderboard and Quiz History
		WidgetTab leaderboardTab = WidgetTab.getInstance("leaderboardTab", "Leaderboard 🏆");
		WidgetTab history = WidgetTab.getInstance("history", "Quiz History");
		WidgetTab instructions = WidgetTab.getInstance("instructions", "Instructions");

		widgetResp.addTabs(leaderboardTab, history);
		widgetResp.setActiveTab(leaderboardTab);

		if (req.getEvent().equals(WIDGET_EVENT.LOAD) || req.getEvent().equals(WIDGET_EVENT.REFRESH) || (req.getEvent().equals(WIDGET_EVENT.TAB_CLICK) && req.getTarget().getId().equals("leaderboardTab"))) {

			LOGGER.info("Event triggered: " + req.getEvent() + ". Checking if it's LOAD or TAB_CLICK for 'leaderboardTab'.");

			// Creating table for leaderboard
			LOGGER.info("Creating leaderboard table...");

			WidgetElement tableTitle = WidgetElement.getInstance(WIDGET_ELEMENT_TYPE.TITLE);
			tableTitle.setText("Leaderboard");

			WidgetElement table = WidgetElement.getInstance(WIDGET_ELEMENT_TYPE.TABLE);
			LOGGER.info("Fetching data for leaderboard...");
			List<HashMap<String, String>> dataL = CatalystDatabase.fetchData("4548000000089204", "");
			LOGGER.info("Fetched " + dataL.size() + " rows for leaderboard.");

			// Table headers
			table.setHeaders(Arrays.asList("Username", "Level", "Location", "Time Spent", "Average", "Total Badges", "Rank"));

			LOGGER.info("Transforming data for leaderboard and sorting by 'Average'...");

// Sort the dataL list by "Average" in descending order
			dataL.sort((o1, o2) -> {
				try {
					// Parse "Average" as double for comparison, default to 0 if parsing fails
					double avg1 = Double.parseDouble(o1.getOrDefault("Average", "0"));
					double avg2 = Double.parseDouble(o2.getOrDefault("Average", "0"));
					return Double.compare(avg2, avg1); // Descending order
				} catch (NumberFormatException e) {
					LOGGER.warning("Error parsing 'Average' values for sorting: " + e.getMessage());
					return 0; // Treat as equal if parsing fails
				}
			});

// Create a new list for the transformed rows
			List<HashMap<String, String>> transformedDataL = new ArrayList<>();

// Iterate through the sorted data
			LOGGER.info("Transforming and ranking data for leaderboard...");
			for (int i = 0; i < dataL.size(); i++) {
				HashMap<String, String> originalRow = dataL.get(i);

				// Log each row's data for debugging
				LOGGER.info("Processing row " + (i + 1) + ": " + originalRow);

				// Check if the row contains valid keys before processing
				if (originalRow.containsKey("UserName") && originalRow.containsKey("Level")) {
					HashMap<String, String> row = new HashMap<>();
					row.put("Username", originalRow.get("UserName"));
					row.put("Level", originalRow.get("Level"));
					row.put("Time Spent", originalRow.getOrDefault("TimeSpent", "0")); // Default to "0" if key is missing
					row.put("Average", originalRow.getOrDefault("Average", "0"));     // Default to "0" if key is missing
					row.put("Location", originalRow.getOrDefault("Location", "Unknown")); // Default to "Unknown" if key is missing
					row.put("Total Badges", originalRow.getOrDefault("TotalBadges", "0"));
					row.put("Rank", String.valueOf(i + 1)); // Rank starts from 1 after sorting

					// Add the transformed row to the new list
					transformedDataL.add(row);
				} else {
					// Log when essential keys are missing
					LOGGER.info("Skipping row " + (i + 1) + " due to missing 'UserName' or 'Level' keys.");
				}
			}

// Set the transformed rows in the table
			table.setRows(transformedDataL);
			LOGGER.info("Leaderboard table populated with " + transformedDataL.size() + " rows.");

// Add the table to the leaderboard section
			WidgetSection leaderboardSection = WidgetSection.getInstance();
			leaderboardSection.addElements(tableTitle, table);
			leaderboardSection.setId("7");
			widgetResp.addSection(leaderboardSection);

			LOGGER.info("Leaderboard loaded successfully with " + transformedDataL.size() + " entries.");

		} else if (req.getEvent().equals(WIDGET_EVENT.REFRESH) || req.getEvent().equals(WIDGET_EVENT.TAB_CLICK)) {
			String target = req.getTarget().getId();
			String userId = req.getUser().getId();

			LOGGER.info("Event triggered: " + req.getEvent() + ". Tab clicked: " + target);
			widgetResp.setActiveTab(target);

			if (target.equals("history")) {
				LOGGER.info("Loading quiz history section...");

				// Quiz History Section with Table Only
				WidgetElement tableTitle = WidgetElement.getInstance(WIDGET_ELEMENT_TYPE.TITLE);
				tableTitle.setText("Quiz History");

				WidgetElement historyTable = WidgetElement.getInstance(WIDGET_ELEMENT_TYPE.TABLE);
				LOGGER.info("Fetching data for quiz history...");
				List<HashMap<String, String>> dataH = CatalystDatabase.fetchData("4548000000082389", "");
				LOGGER.info("Fetched " + dataH.size() + " rows for quiz history.");

				// Table headers
				historyTable.setHeaders(Arrays.asList("Topic", "Date", "Score", "Time Taken"));

				// Create a new list for the transformed rows
				List<HashMap<String, String>> transformedDataH = new ArrayList<>();

				// Iterate through the original data
				LOGGER.info("Transforming data for quiz history...");
				for (int i = 0; i < dataH.size(); i++) {
					HashMap<String, String> originalRow = dataH.get(i);

					// Log each row's data for debugging
					LOGGER.info("Processing row " + (i + 1) + ": " + originalRow);

					// Check if the row contains valid keys before processing
					if (originalRow.containsKey("QuizId") && originalRow.containsKey("Score") && originalRow.containsKey("UserId")) {
						// Fetch and compare the UserId from the row with the local userId
						if (userId.equals(originalRow.get("UserId"))) {
							HashMap<String, String> row = new HashMap<>();
							row.put("QuizId", originalRow.get("QuizId"));
							row.put("Topic", originalRow.get("QuizTopic"));
							row.put("Date", originalRow.get("QuizDate"));
							row.put("Score", originalRow.get("Score"));
							row.put("Time Taken", originalRow.get("TimeTaken"));

							// Add the transformed row to the new list
							transformedDataH.add(row);
						} else {
							// Log if the UserId does not match
							LOGGER.info("Skipping row " + (i + 1) + " due to mismatched UserId.");
						}
					} else {
						// Log when essential keys are missing
						LOGGER.info("Skipping row " + (i + 1) + " due to missing keys ('QuizId', 'Score', or 'UserId').");
					}
				}

				// Set the transformed rows in the table
				historyTable.setRows(transformedDataH);
				LOGGER.info("Quiz history table populated with " + transformedDataH.size() + " rows.");

				// Add the table to the history section
				WidgetSection historySection = WidgetSection.getInstance();
				historySection.addElements(tableTitle, historyTable);
				historySection.setId("historySection");
				widgetResp.addSection(historySection);

				LOGGER.info("Quiz history loaded successfully with " + transformedDataH.size() + " entries.");
			}
		}



		return widgetResp;
	}
}
