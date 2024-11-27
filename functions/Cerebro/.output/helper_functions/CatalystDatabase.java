package helper_functions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class CatalystDatabase {

        public static void insertData(String tableId, String data) {
            try {
                // Define the API endpoint
                String apiUrl = "https://api.catalyst.zoho.com/baas/v1/project/4548000000087809/table/"+tableId+"/row";

                // Create a URL object
                URL url = new URL(apiUrl);

                // Open a connection to the URL
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set the request method to POST
                connection.setRequestMethod("POST");

                // Set the Authorization header
                connection.setRequestProperty("Authorization", "Zoho-oauthtoken " + GenerateAccFromRef.generateAccessToken());

                // Set the Content-Type header
                connection.setRequestProperty("Content-Type", "application/json");

                // Enable input and output streams
                connection.setDoOutput(true);



                // Write the payload to the output stream
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = data.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Get the response code
                int responseCode = connection.getResponseCode();
                System.out.println("Response Code: " + responseCode);

                // Handle the response as needed

            } catch (Exception e) {
                e.printStackTrace();
            }



        }

    public static void updateData(String tableId, String data) {
        try {
            // Define the API endpoint
            String apiUrl = "https://api.catalyst.zoho.com/baas/v1/project/4548000000087809/table/"+tableId+"/row";

            // Create a URL object
            URL url = new URL(apiUrl);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            connection.setRequestMethod("PUT");

            // Set the Authorization header
            connection.setRequestProperty("Authorization", "Zoho-oauthtoken " + GenerateAccFromRef.generateAccessToken());

            // Set the Content-Type header
            connection.setRequestProperty("Content-Type", "application/json");

            // Enable input and output streams
            connection.setDoOutput(true);



            // Write the payload to the output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = data.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Handle the response as needed

        } catch (Exception e) {
            e.printStackTrace();
        }



    }




    public static List<HashMap<String,String>> fetchData(String tableId, String nextToken) {
        List<HashMap<String, String>> result = new ArrayList<>();
        StringBuilder response = new StringBuilder();

        int responseCode = 0;
        try {
            // Define the API endpoint URL for fetching data
            String apiUrl = String.format("https://api.catalyst.zoho.com/baas/v1/project/4548000000087809/table/%s/row?max_rows=100", tableId);

            // Create a URL object
            URL url = new URL(apiUrl);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Set the Authorization header
            connection.setRequestProperty("Authorization", "Zoho-oauthtoken " + GenerateAccFromRef.generateAccessToken());

            // Get the response code
            responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Get the "Content-Type" header
            String contentType = connection.getHeaderField("Content-Type");

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            if (contentType != null && contentType.contains("application/json")) {
                // Parse JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());

                if (jsonResponse.has("data")) {
                    JSONArray dataArray = jsonResponse.getJSONArray("data");

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject row = dataArray.getJSONObject(i);
                        HashMap<String, String> rowMap = new HashMap<>();

                        Iterator<String> keys = row.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            rowMap.put(key, row.getString(key));
                        }

                        result.add(rowMap);
                    }
                }
            } else if (contentType != null && (contentType.contains("application/xml") || contentType.contains("text/xml"))) {
                // Parse XML response
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                InputStream is = new ByteArrayInputStream(response.toString().getBytes("UTF-8"));
                Document doc = builder.parse(is);

                doc.getDocumentElement().normalize();
                NodeList dataNodes = doc.getElementsByTagName("data");

                for (int i = 0; i < dataNodes.getLength(); i++) {
                    Node dataNode = dataNodes.item(i);

                    if (dataNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) dataNode;
                        HashMap<String, String> rowMap = new HashMap<>();

                        NodeList childNodes = element.getChildNodes();
                        for (int j = 0; j < childNodes.getLength(); j++) {
                            Node child = childNodes.item(j);

                            if (child.getNodeType() == Node.ELEMENT_NODE) {
                                rowMap.put(child.getNodeName(), child.getTextContent());
                            }
                        }

                        result.add(rowMap);
                    }
                }
            } else {
                System.out.println("Error: Unsupported Content-Type: " + contentType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Add response code to the result
        HashMap<String, String> respp = new HashMap<>();
        respp.put("response code", String.valueOf(responseCode));
        result.add(respp);

        // Return the result as a JSON string
        return result;
    }


    public static void main(String args[]) {

    }
    }



