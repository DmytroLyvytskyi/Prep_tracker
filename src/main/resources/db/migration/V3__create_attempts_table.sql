CREATE TABLE attempts (
                          id BIGSERIAL PRIMARY KEY,
                          question_id BIGINT NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
                          user_id BIGINT NOT NULL REFERENCES users(id),
                          confidence_level VARCHAR(10) NOT NULL,
                          attempted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          next_review_date DATE NOT NULL,
                          review_interval_days INTEGER NOT NULL
);

CREATE INDEX idx_attempts_question_id ON attempts(question_id);
CREATE INDEX idx_attempts_user_id ON attempts(user_id);
CREATE INDEX idx_attempts_next_review_date ON attempts(next_review_date);