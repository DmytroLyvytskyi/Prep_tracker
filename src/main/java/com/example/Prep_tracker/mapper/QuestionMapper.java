package com.example.Prep_tracker.mapper;

import com.example.Prep_tracker.dto.QuestionRequest;
import com.example.Prep_tracker.dto.QuestionResponse;
import com.example.Prep_tracker.entity.Question;
import com.example.Prep_tracker.entity.User;

public class QuestionMapper {

    public static QuestionResponse toResponse(Question question) {
        return new QuestionResponse(
                question.getId(),
                question.getText(),
                question.getAnswer(),
                question.getCategory(),
                question.isMastered()
        );
    }

    public static Question toEntity(QuestionRequest request, User createdBy) {
        Question question = new Question();
        question.setText(request.text());
        question.setAnswer(request.answer());
        question.setCategory(request.category());
        question.setCreatedBy(createdBy);
        return question;
    }
}