package com.example.Prep_tracker.entity;

import com.example.Prep_tracker.enums.ConfidenceLevel;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "attempts")
public class Attempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "confidence_level", nullable = false, length = 10)
    private ConfidenceLevel confidenceLevel;

    @Column(name = "attempted_at", nullable = false, updatable = false)
    private LocalDateTime attemptedAt;

    @Column(name = "next_review_date", nullable = false)
    private LocalDate nextReviewDate;

    @Column(name = "review_interval_days", nullable = false)
    private Integer reviewIntervalDays;

    @PrePersist
    protected void onCreate() {
        this.attemptedAt = LocalDateTime.now();
    }

    public Attempt() {}


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public ConfidenceLevel getConfidenceLevel() { return confidenceLevel; }
    public void setConfidenceLevel(ConfidenceLevel confidenceLevel) { this.confidenceLevel = confidenceLevel; }

    public LocalDateTime getAttemptedAt() { return attemptedAt; }

    public LocalDate getNextReviewDate() { return nextReviewDate; }
    public void setNextReviewDate(LocalDate nextReviewDate) { this.nextReviewDate = nextReviewDate; }

    public Integer getReviewIntervalDays() { return reviewIntervalDays; }
    public void setReviewIntervalDays(Integer reviewIntervalDays) { this.reviewIntervalDays = reviewIntervalDays; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attempt attempt = (Attempt) o;
        return id != null && Objects.equals(id, attempt.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}