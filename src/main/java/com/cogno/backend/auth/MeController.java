// src/main/java/com/cogno/backend/auth/MeController.java
package com.cogno.backend.auth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MeController {

    // ✅ React dev 서버(5173)에서 이 API 부를 수 있게 CORS 허용
    @CrossOrigin(
            origins = "http://localhost:5173",
            allowCredentials = "true"
    )
    @GetMapping("/api/me")
    public Map<String, Object> me(@AuthenticationPrincipal OAuth2User user) {
        if (user == null) {
            return Map.of("authenticated", false);
        }

        Map<String, Object> props = user.getAttribute("properties");
        String nickname = props != null ? (String) props.get("nickname") : null;

        return Map.of(
                "authenticated", true,
                "id", user.getAttribute("id"),
                "nickname", nickname
        );
    }
}
