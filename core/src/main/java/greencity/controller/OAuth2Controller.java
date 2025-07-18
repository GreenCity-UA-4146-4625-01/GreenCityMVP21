package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.user.UserVO;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Map;

/**
 * Controller for OAuth2 authentication endpoints.
 *
 * @author Andrii Synytsia
 * @version 1.0
 */
@Tag(name = "OAuth2", description = "OAuth2 authentication endpoints")
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final JwtTool jwtTool;
    private final UserService userService;

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
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "User not found or authentication failed"));
        }
    }

    private String extractEmailFromGoogleToken(String token) {
        try {
            String[] parts = token.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> claims = mapper.readValue(payload, Map.class);
            return (String) claims.get("email");
        } catch (Exception e) {
            throw new RuntimeException("Invalid Google token", e);
        }
    }
}