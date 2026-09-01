package com.example.Prep_tracker.dto;

import com.example.Prep_tracker.enums.ConfidenceLevel;

public record AttemptRequest(
        Long questionId,
        ConfidenceLevel confidenceLevel
) {}