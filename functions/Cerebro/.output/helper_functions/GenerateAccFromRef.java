package helper_functions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class GenerateAccFromRef {
    private static final String CLIENT_ID = System.getenv("CLIENT_ID");
    private static final String CLIENT_SECRET = System.getenv("CLIENT_SECRET");
    private static final String REFRESH_TOKEN = System.getenv("REFRESH_TOKEN");
    public static String generateAccessToken() {


        String accessToken = null;
        try {
            // Define the URL for the API request
            String url = "https://accounts.zoho.com/oauth/v2/token";

            // Create the request body
            String requestBody = String.format(
                    "refresh_token=%s&grant_type=refresh_token&client_id=%s&client_secret=%s&redirect_uri=%s",
                    REFRESH_TOKEN, CLIENT_ID, CLIENT_SECRET, "http://application_name.com/"
            );

            // Create a URL object
            URL obj = new URL(url);

            // Open connection
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");

            // Set headers
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            // Send the request body
            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestBody.getBytes());
                os.flush();
            }

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse the JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());

                // Extract the access token
                if (jsonResponse.has("access_token")) {
                    accessToken = jsonResponse.getString("access_token");
                } else {
                    System.out.println("Error: Access token not found in response.");
                }
            } else {
                System.out.println("Error: Unable to fetch access token. Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accessToken;
    }

    public static void main(String[] args) {

        String accessToken = generateAccessToken();
    }
}
