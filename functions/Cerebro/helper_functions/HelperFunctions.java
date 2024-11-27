package helper_functions;

import java.time.Instant;
import java.util.Random;

public class HelperFunctions {

    public static void main(String[] args) {
    }

    public static String generateUnique16DigitCode() {
        // Get the current time in milliseconds since the Unix epoch
        Instant now = Instant.now();
        long timestamp = now.toEpochMilli();

        // Add a random number to increase uniqueness (e.g., 6-digit random number)
        Random random = new Random();
        int randomPart = random.nextInt(1_000_000); // Random 6-digit number

        // Combine timestamp and random part to create a unique 16-digit number
        String code = String.format("%d%06d", timestamp, randomPart);

        // Ensure the code is exactly 16 digits
        if (code.length() < 16) {
            // If it's shorter, pad it with leading zeros
            while (code.length() < 16) {
                code = "0" + code;
            }
        } else if (code.length() > 16) {
            // If it's longer, trim it to 16 digits
            code = code.substring(0, 16);
        }

        return code;
    }
}
