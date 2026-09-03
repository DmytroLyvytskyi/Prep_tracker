package com.example.Prep_tracker.service;

import com.example.Prep_tracker.dto.QuestionRequest;
import com.example.Prep_tracker.dto.QuestionResponse;
import com.example.Prep_tracker.entity.Question;
import com.example.Prep_tracker.entity.User;
import com.example.Prep_tracker.enums.Category;
import com.example.Prep_tracker.mapper.QuestionMapper;
import com.example.Prep_tracker.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public QuestionResponse createQuestion(QuestionRequest request, User currentUser) {
        Question question = QuestionMapper.toEntity(request, currentUser);
        Question saved = questionRepository.save(question);
        return QuestionMapper.toResponse(saved);
    }

    public QuestionResponse getQuestionById(Long id, Long userId) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + id));

        if (!question.getCreatedBy().getId().equals(userId)) {
            throw new SecurityException("You can only view your own questions");
        }

        return QuestionMapper.toResponse(question);
    }

    public List<QuestionResponse> getMyQuestions(Long userId) {
        return questionRepository.findByCreatedById(userId)
                .stream()
                .map(QuestionMapper::toResponse)
                .toList();
    }

    public List<QuestionResponse> getByCategory(Category category, Long userId) {
        return questionRepository.findByCreatedByIdAndCategory(userId, category)
                .stream()
                .map(QuestionMapper::toResponse)
                .toList();
    }

    public QuestionResponse updateQuestion(Long id, QuestionRequest request, User currentUser) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + id));

        if (!question.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new SecurityException("You can only edit your own questions");
        }

        question.setText(request.text());
        question.setAnswer(request.answer());
        question.setCategory(request.category());

        Question updated = questionRepository.save(question);
        return QuestionMapper.toResponse(updated);
    }

    public void markAsMastered(Long id, User currentUser) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + id));

        if (!question.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new SecurityException("You can only update your own questions");
        }

        question.setMastered(true);
        questionRepository.save(question);
    }

    public void deleteQuestion(Long id, User currentUser) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + id));

        if (!question.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new SecurityException("You can only delete your own questions");
        }

        questionRepository.delete(question);
    }
}