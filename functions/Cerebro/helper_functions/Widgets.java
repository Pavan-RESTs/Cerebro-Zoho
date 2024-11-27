package helper_functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Widgets {
    public static Map<String, Object> getQuizBotResponse() {


        // Define the elements list
        List<Map<String, Object>> elements = new ArrayList<>();

        // Add title element
        Map<String, Object> titleElement = new HashMap<>();
        titleElement.put("type", "title");
        titleElement.put("text", "Widget Title");
        elements.add(titleElement);

        // Add text element
        Map<String, Object> textElement = new HashMap<>();
        textElement.put("type", "text");
        textElement.put("text", "Here goes the text!");
        elements.add(textElement);

        // Add user activity element
        Map<String, Object> userActivityElement = new HashMap<>();
        Map<String, Object> user = new HashMap<>();
        user.put("id", "1234567");
        user.put("name", "User Name");

        userActivityElement.put("type", "user_activity");
        userActivityElement.put("user", user);
        userActivityElement.put("text", "You can add text and instant buttons in the user activity element.[Edit Profile]($1)");


        elements.add(userActivityElement);

        // Define the sections list
        List<Map<String, Object>> sections = new ArrayList<>();

        // Add a section with elements
        Map<String, Object> section = new HashMap<>();
        section.put("id", 1);
        section.put("elements", elements);
        sections.add(section);

        // Prepare the final response map
        Map<String, Object> response = new HashMap<>();
        response.put("type", "applet");

        // Define tabs
        List<Map<String, Object>> tabs = new ArrayList<>();
        Map<String, Object> tab = new HashMap<>();
        tab.put("label", "Home");
        tab.put("id", "homeTab");
        tabs.add(tab);

        response.put("tabs", tabs);
        response.put("active_tab", "homeTab");
        response.put("sections", sections);

        return response;
    }

}
