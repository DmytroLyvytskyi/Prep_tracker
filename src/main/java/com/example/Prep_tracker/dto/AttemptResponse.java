package com.example.Prep_tracker.dto;

import com.example.Prep_tracker.enums.ConfidenceLevel;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttemptResponse(
        Long id,
        Long questionId,
        ConfidenceLevel confidenceLevel,
        LocalDateTime attemptedAt,
        LocalDate nextReviewDate,
        Integer reviewIntervalDays
) {}