package helper_functions;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SendMsgToBot {

    public static void send() {
        String chatId = "CT_2218463219501460345_835930979-B2";  // Replace with your actual chat ID
        String accessToken = GenerateAccFromRef.generateAccessToken();  // Assuming this function generates the access token
        String messageText = "Generating quiz...";  // The message to send to the chat

        // Create the request payload
        String payload = String.format("{\"text\": \"%s\"}", messageText);

        // Create the HttpClient instance
        HttpClient client = HttpClient.newHttpClient();

        // Prepare the POST request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://cliq.zoho.com/api/v2/chats/" + chatId + "/message"))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        // Send the request and handle the response
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Print the response from the chat API
            System.out.println("Response Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
