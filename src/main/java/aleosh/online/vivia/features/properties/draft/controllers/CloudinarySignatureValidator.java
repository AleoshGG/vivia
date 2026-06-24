package aleosh.online.vivia.features.properties.draft.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class CloudinarySignatureValidator {

    private final String apiSecret;

    public CloudinarySignatureValidator(@Value("${cloudinary.api-secret}") String apiSecret) {
        this.apiSecret = apiSecret;
    }

    // Cloudinary webhook signature: SHA1(body + timestamp + apiSecret)
    public boolean isValid(String rawBody, String receivedSignature, String timestamp) {
        if (receivedSignature == null || timestamp == null) {
            return false;
        }
        String expected = sha1Hex(rawBody + timestamp + apiSecret);
        return expected.equalsIgnoreCase(receivedSignature);
    }

    private String sha1Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : bytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 no disponible", e);
        }
    }
}
