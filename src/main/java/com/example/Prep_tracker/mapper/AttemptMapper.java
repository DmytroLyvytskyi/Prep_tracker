package com.example.Prep_tracker.mapper;

import com.example.Prep_tracker.dto.AttemptResponse;
import com.example.Prep_tracker.entity.Attempt;

public class AttemptMapper {

    public static AttemptResponse toResponse(Attempt attempt) {
        return new AttemptResponse(
                attempt.getId(),
                attempt.getQuestion().getId(),
                attempt.getConfidenceLevel(),
                attempt.getAttemptedAt(),
                attempt.getNextReviewDate(),
                attempt.getReviewIntervalDays()
        );
    }
}