package greencity.controller;


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

    /** Отримати всі нотифікації поточного користувача. */
    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications(Principal principal) {
        Long userId = getCurrentUserId(principal);
        return ResponseEntity.ok(notificationService.getAllNotificationsForUser(userId));
    }

    /** одну нотифікацію як прочитану. */
    @PostMapping("/{id}/markAsRead")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Principal principal) {
        Long userId = getCurrentUserId(principal);
        ensureNotificationBelongsToUser(id, userId);
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    /** всі нотифікації користувача як прочитані. */
    @PostMapping("/markAllAsRead")
    public ResponseEntity<Void> markAllAsRead(Principal principal) {
        notificationService.markAllAsRead(getCurrentUserId(principal));
        return ResponseEntity.noContent().build();
    }

    /** список нотифікацій як прочитані. */
    @PostMapping("/markAsReadBulk")
    public ResponseEntity<Void> markAsReadBulk(@RequestBody @NotEmpty List<Long> ids,
                                               Principal principal) {
        Long userId = getCurrentUserId(principal);
        ids.forEach(id -> ensureNotificationBelongsToUser(id, userId));
        notificationService.markAsReadBulk(ids);
        return ResponseEntity.noContent().build();
    }


    private Long getCurrentUserId(Principal principal) {
        return userService.findByEmail(principal.getName()).getId();
    }

    private void ensureNotificationBelongsToUser(Long notificationId, Long userId) {
        Notification n = notificationService.findById(notificationId);
        if (!n.getReceiver().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied to this notification.");
        }
    }
}

