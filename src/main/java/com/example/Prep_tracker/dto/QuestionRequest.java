package com.example.Prep_tracker.dto;

import com.example.Prep_tracker.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuestionRequest(

        @NotBlank(message = "Question text is required")
        String text,

        @NotBlank(message = "Answer is required")
        String answer,

        @NotNull(message = "Category is required")
        Category category
) {}