package helper_functions;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class GenerateAccFromRef {


    // Define constants for client ID, client secret, and refresh token
    private static final String CLIENT_ID = "1000.VD6L7ZTU9GV7VZ43ROF7VAZERL9CMB";
    private static final String CLIENT_SECRET = "cebe0bf5286c67725866787c7947b9296b4d7a1e9f";
    private static final String REFRESH_TOKEN = "1000.5ee525d1afe97883b127b677ca559d4b.e464fbe18fd112218181309ed0ce22f6";

    public static String generateAccessToken() {



        String accessToken = null;

        // Specify the token API endpoint
        String tokenApiUrl = "https://accounts.zoho.com/oauth/v2/token";

        // Build the request to get the access token from the refresh token
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenApiUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(buildTokenRequest())
                .build();

        // Create HTTP client
        HttpClient client = HttpClient.newHttpClient();

        // Send the request and handle the response
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the JSON response
            JSONObject json = new JSONObject(response.body());

            // Check if the "access_token" attribute exists in the JSON
            if (json.has("access_token")) {
                // Get the value of the "access_token" attribute
                accessToken = json.getString("access_token");
                System.out.println(accessToken);
            } else {
                System.out.println("Error: Access token not found in the response.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return accessToken;
    }

    private static HttpRequest.BodyPublisher buildTokenRequest() {
        // Build the request body with the necessary parameters
        String requestBody = "refresh_token=" + REFRESH_TOKEN
                + "&client_id=" + CLIENT_ID
                + "&client_secret=" + CLIENT_SECRET
                + "&grant_type=refresh_token";

        return HttpRequest.BodyPublishers.ofString(requestBody);
    }
}