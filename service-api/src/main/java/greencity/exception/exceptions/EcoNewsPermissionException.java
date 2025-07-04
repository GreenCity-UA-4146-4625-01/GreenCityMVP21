package greencity.exception.exceptions;

/**
 * EcoNewsAccessDeniedException is thrown when an authenticated user attempts
 * to update an EcoNews entity they do not own or if they are not an administrator.
 * This exception indicates a violation of access control rules and
 * results in an HTTP 403 Forbidden response.
 *
 * @author Mykyta Sirobaba
 * @version 1.0
 */
public class EcoNewsPermissionException extends RuntimeException {
    /**
     * Constructor for EcoNewsPermissionException.
     *
     * @param message - giving message.
     */
    public EcoNewsPermissionException(String message) {
        super(message);
    }
}