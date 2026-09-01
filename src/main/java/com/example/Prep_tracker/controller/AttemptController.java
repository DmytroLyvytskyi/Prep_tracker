package com.example.Prep_tracker.controller;

import com.example.Prep_tracker.dto.AttemptRequest;
import com.example.Prep_tracker.dto.AttemptResponse;
import com.example.Prep_tracker.entity.User;
import com.example.Prep_tracker.security.AuthenticatedUser;
import com.example.Prep_tracker.service.AttemptService;
import com.example.Prep_tracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attempts")
public class AttemptController {

    private final AttemptService attemptService;
    private final UserService userService;

    public AttemptController(AttemptService attemptService, UserService userService) {
        this.attemptService = attemptService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<AttemptResponse> recordAttempt(
            @Valid @RequestBody AttemptRequest request,
            @AuthenticationPrincipal AuthenticatedUser principal
    ) {
        User currentUser = userService.getCurrentUserEntity(principal.userId());
        AttemptResponse response = attemptService.recordAttempt(request, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/due")
    public ResponseEntity<List<AttemptResponse>> getDueForReview(
            @AuthenticationPrincipal AuthenticatedUser principal
    ) {
        List<AttemptResponse> due = attemptService.getDueForReview(principal.userId());
        return ResponseEntity.ok(due);
    }
}