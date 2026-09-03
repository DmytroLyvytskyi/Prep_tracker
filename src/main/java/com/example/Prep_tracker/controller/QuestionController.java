package com.example.Prep_tracker.controller;

import com.example.Prep_tracker.dto.QuestionRequest;
import com.example.Prep_tracker.dto.QuestionResponse;
import com.example.Prep_tracker.entity.User;
import com.example.Prep_tracker.enums.Category;
import com.example.Prep_tracker.security.AuthenticatedUser;
import com.example.Prep_tracker.service.QuestionService;
import com.example.Prep_tracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;

    public QuestionController(QuestionService questionService, UserService userService) {
        this.questionService = questionService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<QuestionResponse> create(
            @Valid @RequestBody QuestionRequest request,
            @AuthenticationPrincipal AuthenticatedUser principal
    ) {
        User currentUser = userService.getCurrentUserEntity(principal.userId());
        QuestionResponse response = questionService.createQuestion(request, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser principal
    ) {
        return ResponseEntity.ok(questionService.getQuestionById(id, principal.userId()));
    }

    @GetMapping("/my")
    public ResponseEntity<List<QuestionResponse>> getMyQuestions(
            @AuthenticationPrincipal AuthenticatedUser principal
    ) {
        return ResponseEntity.ok(questionService.getMyQuestions(principal.userId()));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<QuestionResponse>> getByCategory(
            @PathVariable Category category,
            @AuthenticationPrincipal AuthenticatedUser principal
    ) {
        return ResponseEntity.ok(questionService.getByCategory(category, principal.userId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody QuestionRequest request,
            @AuthenticationPrincipal AuthenticatedUser principal
    ) {
        User currentUser = userService.getCurrentUserEntity(principal.userId());
        QuestionResponse response = questionService.updateQuestion(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/master")
    public ResponseEntity<Void> markAsMastered(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser principal
    ) {
        User currentUser = userService.getCurrentUserEntity(principal.userId());
        questionService.markAsMastered(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser principal
    ) {
        User currentUser = userService.getCurrentUserEntity(principal.userId());
        questionService.deleteQuestion(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}