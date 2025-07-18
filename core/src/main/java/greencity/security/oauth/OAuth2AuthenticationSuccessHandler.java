package greencity.security.oauth;

import greencity.dto.user.UserVO;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
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

    @Value("${client.address}")
    private String clientUrl;

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

        try {
            UserVO user = userService.findByEmail(email);

            String accessToken = jwtTool.createAccessToken(email, user.getRole());
            String refreshToken = jwtTool.createRefreshToken(user);

            String targetUrl = UriComponentsBuilder.fromUriString(clientUrl + "/auth/success")
                    .queryParam("token", accessToken)
                    .queryParam("refreshToken", refreshToken)
                    .build().toUriString();

            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception e) {
            String errorUrl = UriComponentsBuilder.fromUriString(clientUrl + "/auth/error")
                    .queryParam("error", "oauth2_failed")
                    .build().toUriString();

            getRedirectStrategy().sendRedirect(request, response, errorUrl);
        }
    }
}
