package com.example.Prep_tracker.dto;

import com.example.Prep_tracker.enums.Category;

public record QuestionRequest(String text, String answer, Category category) {}
