package greencity.security.oauth;

import greencity.dto.user.UserVO;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * Handles successful OAuth2 authentication by integrating with existing JWT system.
 *
 * @author Andrii Synytsia
 * @version 1.0
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTool jwtTool;

    private final UserService userService;
    
    private final Environment environment;

    @Value("${client.address}")
    private String clientUrl;

    @Value("${accessTokenValidTimeInMinutes}")
    private Integer accessTokenValidTime;

    @Value("${refreshTokenValidTimeInMinutes}")
    private Integer refreshTokenValidTime;

    /**
     * Handles successful OAuth2 authentication.
     *
     * @param request        the HTTP request
     * @param response       the HTTP response
     * @param authentication the authentication object containing OAuth2 user info
     * @throws IOException if redirect fails
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            String errorUrl = UriComponentsBuilder.fromUriString(clientUrl + "/auth/error")
                    .queryParam("error", "email_not_found")
                    .build().toUriString();

            getRedirectStrategy().sendRedirect(request, response, errorUrl);
            return;
        }

        try {
            UserVO user = userService.findByEmail(email);

            if (user == null) {
                String errorUrl = UriComponentsBuilder.fromUriString(clientUrl + "/auth/error")
                        .queryParam("error", "user_not_registered")
                        .queryParam("message", "Please register using the traditional from first")
                        .build().toUriString();
                getRedirectStrategy().sendRedirect(request, response, errorUrl);
            }

            String accessToken = jwtTool.createAccessToken(email, user.getRole());
            String refreshToken = jwtTool.createRefreshToken(user);

            // Add tokens as secure HTTP-only cookies
            addTokenCookie(response, "accessToken", accessToken, accessTokenValidTime * 60); // Convert minutes to seconds
            addTokenCookie(response, "refreshToken", refreshToken, refreshTokenValidTime * 60); // Convert minutes to seconds

            // Redirect to success page without tokens in URL
            String targetUrl = UriComponentsBuilder.fromUriString(clientUrl + "/auth/success")
                    .build().toUriString();

            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception e) {
            String errorUrl = UriComponentsBuilder.fromUriString(clientUrl + "/auth/error")
                    .queryParam("error", "oauth2_failed")
                    .build().toUriString();

            getRedirectStrategy().sendRedirect(request, response, errorUrl);
        }
    }
    
    /**
     * Adds a secure HTTP-only cookie with the specified token.
     *
     * @param response the HTTP response
     * @param name the cookie name
     * @param value the token value
     * @param maxAge the cookie max age in seconds
     */
    private void addTokenCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
}
