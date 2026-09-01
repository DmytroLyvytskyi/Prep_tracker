package com.example.Prep_tracker.dto;

import com.example.Prep_tracker.enums.ConfidenceLevel;
import jakarta.validation.constraints.NotNull;

public record AttemptRequest(

        @NotNull(message = "Question id is required")
        Long questionId,

        @NotNull(message = "Confidence level is required")
        ConfidenceLevel confidenceLevel
) {}