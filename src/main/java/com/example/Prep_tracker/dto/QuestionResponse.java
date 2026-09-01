package com.example.Prep_tracker.dto;

import com.example.Prep_tracker.enums.Category;

public record QuestionResponse(
        Long id,
        String text,
        String answer,
        Category category,
        boolean mastered
) {}