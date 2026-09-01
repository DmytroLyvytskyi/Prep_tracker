package com.example.Prep_tracker.repository;

import com.example.Prep_tracker.entity.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttemptRepository extends JpaRepository<Attempt, Long> {
    List<Attempt> findByUserId(Long userId);
    List<Attempt> findByQuestionId(Long questionId);
    List<Attempt> findByUserIdAndNextReviewDateLessThanEqual(Long userId, LocalDate date);
    Optional<Attempt> findTopByUserIdAndQuestionIdOrderByAttemptedAtDesc(Long userId, Long questionId);


}