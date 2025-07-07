package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.error.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Map;

@RestController
@RequestMapping(path = "${server.error.path:${error.path:/error}}",
        method = RequestMethod.GET)
public class SwaggerErrorController extends BasicErrorController {


    /**
     * Initializing constructor
     */
    public SwaggerErrorController(ErrorAttributes errorAttributes,
                                  ServerProperties serverProperties) {
        super(errorAttributes, serverProperties.getError());
    }


    /**
     * Handles errors forwarded to the default `/error` endpoint.
     *
     * @param request the original {@link HttpServletRequest} in which the error occurred
     * @return a {@link ResponseEntity} with status 200 OK and a JSON body containing
     * a map of error attributes (e.g. "timestamp", "status", "error", "message", "errors", "path")
     *
     * @author Andrii Synytsia
     */

    @Override
    @GetMapping
    @Operation(summary = "Basic error handler")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "500", description = HttpStatuses.INTERNAL_SERVER_ERROR)
    })
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        ServletWebRequest webRequest = new ServletWebRequest(request);
        Map<String, Object> body = getErrorAttributes(
                webRequest.getRequest(),
                ErrorAttributeOptions.of(
                        ErrorAttributeOptions.Include.MESSAGE,
                        ErrorAttributeOptions.Include.BINDING_ERRORS));
        return ResponseEntity.ok(body);
    }
}
