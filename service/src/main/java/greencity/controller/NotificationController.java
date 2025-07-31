package greencity.controller;

import greencity.constant.ErrorMessage;
import greencity.dto.notification.NotificationDto;
import greencity.entity.Notification;
import greencity.service.NotificationService;
import greencity.service.UserService;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final UserService userService;

    /** Get all notifications for the current user. */
    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications(Principal principal) {
        Long userId = currentUserId(principal);
        return ResponseEntity.ok(notificationService.getAllNotificationsForUser(userId));
    }

    /** Mark a single notification as read. */
    @PostMapping("/{id}/markAsRead")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Principal principal) {
        Long userId = currentUserId(principal);
        ensureOwner(id, userId);
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    /** Mark all notifications of the current user as read. */
    @PostMapping("/markAllAsRead")
    public ResponseEntity<Void> markAllAsRead(Principal principal) {
        notificationService.markAllAsRead(currentUserId(principal));
        return ResponseEntity.noContent().build();
    }

    /** Mark a list of notifications as read (bulk). */
    @PostMapping("/markAsReadBulk")
    public ResponseEntity<Void> markAsReadBulk(@RequestBody @NotEmpty List<Long> ids,
                                               Principal principal) {
        Long userId = currentUserId(principal);
        ids.forEach(id -> ensureOwner(id, userId));
        notificationService.markAsReadBulk(ids);
        return ResponseEntity.noContent().build();
    }


    private Long currentUserId(Principal principal) {
        return userService.findByEmail(principal.getName()).getId();
    }

    /** Throws 403 if the notification does not belong to the current user. */
    private void ensureOwner(Long notificationId, Long userId) {
        Notification n = notificationService.findById(notificationId);
        if (!n.getReceiver().getId().equals(userId)) {
            throw new AccessDeniedException(ErrorMessage.ACCESS_DENIED_NOTIFICATION);
        }
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnreadCount(Principal principal) {
        Long userId = currentUserId(principal);
        return ResponseEntity.ok(notificationService.countUnreadNotifications(userId));
    }

}
