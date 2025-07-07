package greencity.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SwaggerErrorControllerTest {

    @Mock
    private ErrorAttributes errorAttributes;

    @Mock
    private ServerProperties serverProperties;

    private SwaggerErrorController controller;

    @BeforeEach
    void setUp() {
        ErrorProperties errorProps = new ErrorProperties();
        when(serverProperties.getError()).thenReturn(errorProps);

        controller = new SwaggerErrorController(errorAttributes, serverProperties);
    }

    @Test
    void error_Returns200AndDelegatesToErrorAttributes() {
        Map<String, Object> fakeAttrs = new HashMap<>();
        fakeAttrs.put("timestamp", "2025-07-07T10:00:00.000Z");
        fakeAttrs.put("status", 500);
        fakeAttrs.put("error", "Internal Server Error");
        fakeAttrs.put("message", "Oops!");
        fakeAttrs.put("path", "/some/path");

        when(errorAttributes.getErrorAttributes(
                any(ServletWebRequest.class),
                any(ErrorAttributeOptions.class))
        ).thenReturn(fakeAttrs);

        MockHttpServletRequest servletReq = new MockHttpServletRequest();
        ResponseEntity<Map<String, Object>> response = controller.error(servletReq);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertSame(fakeAttrs, response.getBody());

        verify(errorAttributes)
                .getErrorAttributes(any(ServletWebRequest.class), any(ErrorAttributeOptions.class));
    }
}