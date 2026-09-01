package com.example.Prep_tracker.service;

import com.example.Prep_tracker.dto.AttemptRequest;
import com.example.Prep_tracker.dto.AttemptResponse;
import com.example.Prep_tracker.entity.Attempt;
import com.example.Prep_tracker.entity.Question;
import com.example.Prep_tracker.entity.User;
import com.example.Prep_tracker.enums.ConfidenceLevel;
import com.example.Prep_tracker.mapper.AttemptMapper;
import com.example.Prep_tracker.repository.AttemptRepository;
import com.example.Prep_tracker.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AttemptService {

    private final AttemptRepository attemptRepository;
    private final QuestionRepository questionRepository;

    public AttemptService(AttemptRepository attemptRepository,
                          QuestionRepository questionRepository) {
        this.attemptRepository = attemptRepository;
        this.questionRepository = questionRepository;
    }

    public AttemptResponse recordAttempt(AttemptRequest request, User currentUser) {
        Question question = questionRepository.findById(request.questionId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Question not found: " + request.questionId()));

        Optional<Attempt> lastAttempt = attemptRepository
                .findTopByUserIdAndQuestionIdOrderByAttemptedAtDesc(
                        currentUser.getId(), question.getId());

        int newIntervalDays = calculateNextInterval(
                request.confidenceLevel(),
                lastAttempt.map(Attempt::getReviewIntervalDays).orElse(null)
        );

        Attempt attempt = new Attempt();
        attempt.setQuestion(question);
        attempt.setUser(currentUser);
        attempt.setConfidenceLevel(request.confidenceLevel());
        attempt.setReviewIntervalDays(newIntervalDays);
        attempt.setNextReviewDate(LocalDate.now().plusDays(newIntervalDays));

        Attempt saved = attemptRepository.save(attempt);
        return AttemptMapper.toResponse(saved);
    }

    private int calculateNextInterval(ConfidenceLevel level, Integer previousIntervalDays) {
        if (previousIntervalDays == null) {
            if (level == ConfidenceLevel.BAD) {
                return 1;
            } else if (level == ConfidenceLevel.OK) {
                return 3;
            } else if (level == ConfidenceLevel.GOOD) {
                return 7;
            } else {
                throw new IllegalArgumentException("Unknown confidence level: " + level);
            }
        }

        double multiplier;
        if (level == ConfidenceLevel.BAD) {
            multiplier = 1.2;
        } else if (level == ConfidenceLevel.OK) {
            multiplier = 2.0;
        } else if (level == ConfidenceLevel.GOOD) {
            multiplier = 3.0;
        } else {
            throw new IllegalArgumentException("Unknown confidence level: " + level);
        }

        return (int) Math.ceil(previousIntervalDays * multiplier);
    }

    public List<AttemptResponse> getDueForReview(Long userId) {
        return attemptRepository
                .findByUserIdAndNextReviewDateLessThanEqual(userId, LocalDate.now())
                .stream()
                .map(AttemptMapper::toResponse)
                .toList();
    }
}