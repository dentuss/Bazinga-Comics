package com.bazinga.bazingabe.controller;

import com.bazinga.bazingabe.dto.SubscriptionRequest;
import com.bazinga.bazingabe.dto.SubscriptionResponse;
import com.bazinga.bazingabe.entity.User;
import com.bazinga.bazingabe.repository.UserRepository;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final UserRepository userRepository;

    public SubscriptionController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/subscribe")
    public ResponseEntity<SubscriptionResponse> subscribe(
            @RequestBody SubscriptionRequest request,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String type = request.getSubscriptionType() == null ? "" : request.getSubscriptionType().trim();
        String billing = request.getBillingCycle() == null ? "" : request.getBillingCycle().trim().toLowerCase();

        if (!type.equalsIgnoreCase("Premium") && !type.equalsIgnoreCase("Unlimited")) {
            return ResponseEntity.badRequest().build();
        }

        if (!billing.equals("monthly") && !billing.equals("yearly")) {
            return ResponseEntity.badRequest().build();
        }

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        if (user.getRole() != null && !user.getRole().equalsIgnoreCase("USER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        LocalDate expiration = billing.equals("monthly")
                ? LocalDate.now().plusMonths(1)
                : LocalDate.now().plusYears(1);

        user.setSubscriptionType(type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase());
        user.setSubscriptionExpiration(expiration);
        User saved = userRepository.save(user);

        return ResponseEntity.ok(new SubscriptionResponse(
                saved.getSubscriptionType(),
                saved.getSubscriptionExpiration() != null ? saved.getSubscriptionExpiration().toString() : null));
    }
}
