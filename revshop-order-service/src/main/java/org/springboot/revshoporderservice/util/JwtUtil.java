package org.springboot.revshoporderservice.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class JwtUtil {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Integer extractUserIdFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authHeader.substring(7);
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return null;
            }

            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            JsonNode jsonNode = objectMapper.readTree(payload);

            if (jsonNode.has("userId")) {
                return jsonNode.get("userId").asInt();
            } else if (jsonNode.has("id")) {
                return jsonNode.get("id").asInt();
            } else if (jsonNode.has("sub")) {
                return Integer.parseInt(jsonNode.get("sub").asText());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
