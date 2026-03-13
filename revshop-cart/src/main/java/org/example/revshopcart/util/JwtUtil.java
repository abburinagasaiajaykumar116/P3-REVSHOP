package org.example.revshopcart.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class JwtUtil {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Integer extractUserIdFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null; // Handle missing or invalid header
        }

        String token = authHeader.substring(7); // Remove "Bearer "
        
        try {
            // A JWT has 3 parts separated by dots: header.payload.signature
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }

            // The payload is the second part (index 1)
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            
            // Parse the JSON payload
            JsonNode jsonNode = objectMapper.readTree(payload);
            
            // Check for id or userId
            if (jsonNode.has("userId")) {
                return jsonNode.get("userId").asInt();
            } else if (jsonNode.has("id")) {
                return jsonNode.get("id").asInt(); 
            }
            
            return null;
        } catch (Exception e) {
            System.err.println("Error decoding JWT payload: " + e.getMessage());
            return null;
        }
    }
}
