package greencity.controller;

import greencity.dto.subscription.SubscriptionDto;
import greencity.service.EmailSubscriptionService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/subscriptions")
public class EmailSubscriptionController {
    private final EmailSubscriptionService emailSubscriptionService;

    @Autowired
    public EmailSubscriptionController(EmailSubscriptionService emailSubscriptionService) {
        this.emailSubscriptionService = emailSubscriptionService;
    }

    @PostMapping
    public ResponseEntity<SubscriptionDto> subscribe(@Email @NotBlank String email) {
        return ResponseEntity.status(HttpStatus.CREATED).body(emailSubscriptionService.createSubscription(email));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@PathVariable UUID id) {
        emailSubscriptionService.deleteSubscription(id);
    }
}
