package com.example.airassist.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Base64;
import java.util.List;
import java.util.Map;

public class JwtUtils {


    public static String getJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static boolean hasRoleAdmin(String token) {
        return checkRole(token,"ADMIN");
    }

    public static boolean hasRolePassenger(String token) {
       return checkRole(token, "PASSENGER");
    }

    private static boolean checkRole(String token, String role) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return false;
            }

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));

            ObjectMapper mapper = new ObjectMapper();
            var payload = mapper.readValue(payloadJson, Map.class);

            Object authoritiesObj = payload.get("authorities");
            if (authoritiesObj instanceof List<?> authorities) {
                return authorities.stream().anyMatch(role::equals);
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
