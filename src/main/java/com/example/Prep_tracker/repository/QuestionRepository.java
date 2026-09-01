package com.example.Prep_tracker.repository;

import com.example.Prep_tracker.entity.Question;
import com.example.Prep_tracker.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByCreatedById(Long userId);
    List<Question> findByCategory(Category category);
    List<Question> findByCreatedByIdAndMastered(Long userId, boolean mastered);
}
