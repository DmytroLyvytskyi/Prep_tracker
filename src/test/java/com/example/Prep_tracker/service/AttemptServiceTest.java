package com.example.Prep_tracker.service;

import com.example.Prep_tracker.dto.AttemptRequest;
import com.example.Prep_tracker.dto.AttemptResponse;
import com.example.Prep_tracker.entity.Attempt;
import com.example.Prep_tracker.entity.Question;
import com.example.Prep_tracker.entity.User;
import com.example.Prep_tracker.enums.ConfidenceLevel;
import com.example.Prep_tracker.repository.AttemptRepository;
import com.example.Prep_tracker.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttemptServiceTest {

    @Mock
    private AttemptRepository attemptRepository;

    @Mock
    private QuestionRepository questionRepository;

    private AttemptService attemptService;

    private User user;
    private Question question;

    private static final Long USER_ID = 1L;
    private static final Long QUESTION_ID = 10L;

    @BeforeEach
    void setUp() {
        attemptService = new AttemptService(attemptRepository, questionRepository);

        user = mock(User.class);
        lenient().when(user.getId()).thenReturn(USER_ID);

        question = mock(Question.class);
        lenient().when(question.getId()).thenReturn(QUESTION_ID);
        lenient().when(question.getCreatedBy()).thenReturn(user);
    }

    private void stubSaveToPersist() {
        when(attemptRepository.save(any(Attempt.class))).thenAnswer(invocation -> {
            Attempt saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 100L);
            ReflectionTestUtils.setField(saved, "attemptedAt", LocalDateTime.now());
            return saved;
        });
    }

    @Test
    void recordAttempt_throwsWhenQuestionNotFound() {
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.empty());
        AttemptRequest request = new AttemptRequest(QUESTION_ID, ConfidenceLevel.GOOD);

        assertThrows(IllegalArgumentException.class,
                () -> attemptService.recordAttempt(request, user));

        verify(attemptRepository, never()).save(any());
    }

    @ParameterizedTest
    @CsvSource({
            "BAD, 1",
            "OK, 3",
            "GOOD, 7"
    })
    void recordAttempt_firstAttempt_usesFixedBaseInterval(ConfidenceLevel level, int expectedInterval) {
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));
        when(attemptRepository.findTopByUserIdAndQuestionIdOrderByAttemptedAtDesc(USER_ID, QUESTION_ID))
                .thenReturn(Optional.empty());
        stubSaveToPersist();

        AttemptRequest request = new AttemptRequest(QUESTION_ID, level);
        AttemptResponse response = attemptService.recordAttempt(request, user);

        assertThat(response.reviewIntervalDays()).isEqualTo(expectedInterval);
        assertThat(response.nextReviewDate()).isEqualTo(LocalDate.now().plusDays(expectedInterval));
    }

    @ParameterizedTest
    @CsvSource({
            "5, BAD, 6",
            "5, OK, 10",
            "5, GOOD, 15",
            "7, BAD, 9"
    })
    void recordAttempt_subsequentAttempt_appliesMultiplierWithCeilRounding(
            int previousInterval, ConfidenceLevel level, int expectedInterval) {

        Attempt previousAttempt = mock(Attempt.class);
        when(previousAttempt.getReviewIntervalDays()).thenReturn(previousInterval);

        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));
        when(attemptRepository.findTopByUserIdAndQuestionIdOrderByAttemptedAtDesc(USER_ID, QUESTION_ID))
                .thenReturn(Optional.of(previousAttempt));
        stubSaveToPersist();

        AttemptRequest request = new AttemptRequest(QUESTION_ID, level);
        AttemptResponse response = attemptService.recordAttempt(request, user);

        assertThat(response.reviewIntervalDays()).isEqualTo(expectedInterval);
        assertThat(response.nextReviewDate()).isEqualTo(LocalDate.now().plusDays(expectedInterval));
    }

    @Test
    void recordAttempt_savesAttemptWithCorrectQuestionUserAndConfidenceLevel() {
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));
        when(attemptRepository.findTopByUserIdAndQuestionIdOrderByAttemptedAtDesc(USER_ID, QUESTION_ID))
                .thenReturn(Optional.empty());
        stubSaveToPersist();

        AttemptRequest request = new AttemptRequest(QUESTION_ID, ConfidenceLevel.OK);
        attemptService.recordAttempt(request, user);

        ArgumentCaptor<Attempt> captor = ArgumentCaptor.forClass(Attempt.class);
        verify(attemptRepository).save(captor.capture());

        Attempt saved = captor.getValue();
        assertThat(saved.getQuestion()).isEqualTo(question);
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getConfidenceLevel()).isEqualTo(ConfidenceLevel.OK);
    }

    @Test
    void getDueForReview_returnsMappedResponsesFromRepository() {
        Attempt dueAttempt = mock(Attempt.class);
        when(dueAttempt.getId()).thenReturn(1L);
        when(dueAttempt.getQuestion()).thenReturn(question);
        when(dueAttempt.getConfidenceLevel()).thenReturn(ConfidenceLevel.BAD);
        when(dueAttempt.getAttemptedAt()).thenReturn(LocalDateTime.now().minusDays(1));
        when(dueAttempt.getNextReviewDate()).thenReturn(LocalDate.now());
        when(dueAttempt.getReviewIntervalDays()).thenReturn(1);

        when(attemptRepository.findByUserIdAndNextReviewDateLessThanEqual(eq(USER_ID), any(LocalDate.class)))
                .thenReturn(List.of(dueAttempt));

        List<AttemptResponse> result = attemptService.getDueForReview(USER_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).questionId()).isEqualTo(QUESTION_ID);
        assertThat(result.get(0).confidenceLevel()).isEqualTo(ConfidenceLevel.BAD);
    }

    @Test
    void getDueForReview_returnsEmptyListWhenNothingDue() {
        when(attemptRepository.findByUserIdAndNextReviewDateLessThanEqual(eq(USER_ID), any(LocalDate.class)))
                .thenReturn(List.of());

        List<AttemptResponse> result = attemptService.getDueForReview(USER_ID);

        assertThat(result).isEmpty();
    }

    @Test
    void recordAttempt_throwsWhenQuestionBelongsToAnotherUser() {
        User anotherUser = mock(User.class);
        when(anotherUser.getId()).thenReturn(999L);
        when(question.getCreatedBy()).thenReturn(anotherUser);

        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.of(question));

        AttemptRequest request = new AttemptRequest(QUESTION_ID, ConfidenceLevel.GOOD);

        assertThrows(SecurityException.class,
                () -> attemptService.recordAttempt(request, user));

        verify(attemptRepository, never()).save(any());
    }

}