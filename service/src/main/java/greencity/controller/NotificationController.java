package greencity.controller;

import greencity.dto.notification.NotificationDto;
import greencity.entity.Notification;
import greencity.service.NotificationService;
import greencity.service.UserService;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    /**
      Get list of notifications .
     */
    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications(Principal principal) {
        Long userId = getCurrentUserId(principal);
        List<NotificationDto> notifications = notificationService.getAllNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Mark one notification as read.
     */
    @PostMapping("/{id}/markAsRead")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Principal principal) {
        Long userId = getCurrentUserId(principal);
        ensureNotificationBelongsToUser(id, userId);
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Mark all notifications  as read.
     */
    @PostMapping("/markAllAsRead")
    public ResponseEntity<Void> markAllAsRead(Principal principal) {
        Long userId = getCurrentUserId(principal);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Mark list of notifications as read .
     */
    @PostMapping("/markAsReadBulk")
    public ResponseEntity<Void> markAsReadBulk(@RequestBody @NotEmpty List<Long> ids, Principal principal) {
        Long userId = getCurrentUserId(principal);
        for (Long id : ids) {
            ensureNotificationBelongsToUser(id, userId);
        }
        notificationService.markAsReadBulk(ids);
        return ResponseEntity.noContent().build();
    }

    // Utility methods
    private Long getCurrentUserId(Principal principal) {
        return userService.findByEmail(principal.getName()).getId();
    }

    private void ensureNotificationBelongsToUser(Long notificationId, Long userId) {
        Notification notification = notificationService.findById(notificationId);
        if (!notification.getReceiver().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied to this notification.");
        }
    }
}
