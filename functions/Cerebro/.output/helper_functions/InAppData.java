package helper_functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InAppData {
    public static List<List<String>> giveReview () {
        // Create a List of List of Strings to hold the motivational statements
        List<List<String>> statements = new ArrayList<>();

        // Top Scorer (100% or Perfect Score)
        List<String> topScorer = new ArrayList<>();
        topScorer.add("Outstanding Achievement!");
        topScorer.add("You’ve nailed it! Your dedication and sharp focus have paid off with a perfect score. Keep aiming for the stars!");

        // High Scorer (Above 80%)
        List<String> highScorer = new ArrayList<>();
        highScorer.add("Great Performance!");
        highScorer.add("Fantastic effort! You’re among the top performers, showing a solid understanding. With a little more focus, you could reach the perfect score next time!");

        // Moderate Scorer (50-80%)
        List<String> moderateScorer = new ArrayList<>();
        moderateScorer.add("Good Progress!");
        moderateScorer.add("Well done! You’ve made great strides. Keep working on the tricky areas, and your score will improve even further!");

        // Low Scorer (30-50%)
        List<String> lowScorer = new ArrayList<>();
        lowScorer.add("Keep Going!");
        lowScorer.add("Nice try! You’re on the right path, and every effort counts. Review your answers, and with a little more practice, you'll see great improvement!");

        // Struggler (Below 30%)
        List<String> struggler = new ArrayList<>();
        struggler.add("Don’t Give Up!");
        struggler.add("It’s okay! Every mistake is a step toward learning. Focus on the areas you found difficult, and soon enough, you’ll be mastering them.");

        // Add all statements to the main list
        statements.add(topScorer);
        statements.add(highScorer);
        statements.add(moderateScorer);
        statements.add(lowScorer);
        statements.add(struggler);

        return statements;
    }

        public static String returnCountryName(String countryCode) {
            Map<String, String> countryCodeMap = new HashMap<>();

            // Asia
            countryCodeMap.put("India", "IN");
            countryCodeMap.put("China", "CN");
            countryCodeMap.put("Japan", "JP");
            countryCodeMap.put("Singapore", "SG");
            countryCodeMap.put("South Korea", "KR");
            countryCodeMap.put("Pakistan", "PK");
            countryCodeMap.put("Indonesia", "ID");
            countryCodeMap.put("Thailand", "TH");
            countryCodeMap.put("Vietnam", "VN");

            // Europe
            countryCodeMap.put("United Kingdom", "GB");
            countryCodeMap.put("Germany", "DE");
            countryCodeMap.put("France", "FR");
            countryCodeMap.put("Italy", "IT");
            countryCodeMap.put("Spain", "ES");
            countryCodeMap.put("Netherlands", "NL");
            countryCodeMap.put("Switzerland", "CH");
            countryCodeMap.put("Sweden", "SE");
            countryCodeMap.put("Norway", "NO");

            // Americas
            countryCodeMap.put("United States", "US");
            countryCodeMap.put("Canada", "CA");
            countryCodeMap.put("Mexico", "MX");
            countryCodeMap.put("Brazil", "BR");
            countryCodeMap.put("Argentina", "AR");
            countryCodeMap.put("Chile", "CL");
            countryCodeMap.put("Colombia", "CO");
            countryCodeMap.put("Peru", "PE");

            // Africa
            countryCodeMap.put("South Africa", "ZA");
            countryCodeMap.put("Nigeria", "NG");
            countryCodeMap.put("Egypt", "EG");
            countryCodeMap.put("Kenya", "KE");
            countryCodeMap.put("Morocco", "MA");

            // Oceania
            countryCodeMap.put("Australia", "AU");
            countryCodeMap.put("New Zealand", "NZ");
            countryCodeMap.put("Fiji", "FJ");

            // Reversed map
            Map<String, String> reversedMap = new HashMap<>();
            for (Map.Entry<String, String> entry : countryCodeMap.entrySet()) {
                reversedMap.put(entry.getValue().toLowerCase(), entry.getKey());
            }

            return reversedMap.get(countryCode);
        }



}
