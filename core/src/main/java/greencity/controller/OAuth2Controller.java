package greencity.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import greencity.dto.user.UserVO;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

/**
 * Controller for OAuth2 authentication endpoints.
 *
 * @author Andrii Synytsia
 * @version 1.0
 */
@Tag(name = "OAuth2", description = "OAuth2 authentication endpoints")
@CrossOrigin(origins = "${cors.allowed-origins}")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class OAuth2Controller {

    private final JwtTool jwtTool;
    private final UserService userService;

    @Value("${google.clientId}")
    private String googleClientId;

    /**
     * Handles Google JWT token authentication.
     *
     * @param request containing Google JWT token and language
     * @return authentication response
     */
    @Operation(summary = "Authenticate with Google JWT token")
    @ApiResponse(responseCode = "200", description = "Authentication successful")
    @ApiResponse(responseCode = "401", description = "Invalid token")
    @PostMapping("/googleSecurity")
    public ResponseEntity<?> googleAuth(@RequestBody Map<String, String> request) {
        String token = request.get("token");

        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "Token is required"));
        }

        // Decode Google JWT token to extract email
        String email = extractEmailFromGoogleToken(token);

        try {
            UserVO user = userService.findByEmail(email);


            String accessToken = jwtTool.createAccessToken(email, user.getRole());
            String refreshToken = jwtTool.createRefreshToken(user);

            SuccessSignInDto response = SuccessSignInDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .userId(user.getId())
                    .name(user.getName())
                    .ownRegistrations(true)
                    .build();

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("OAuth2 authentication failed for email: {}", email, e);
            return ResponseEntity.status(401).body(Map.of("error", "User not found or authentication failed"));
        }
    }

    private String extractEmailFromGoogleToken(String token) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(token);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                return payload.getEmail();
            } else {
                throw new RuntimeException("Invalid Google token");
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid Google token", e);
        }
    }
}