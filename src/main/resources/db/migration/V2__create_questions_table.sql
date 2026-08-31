CREATE TABLE questions (
                           id BIGSERIAL PRIMARY KEY,
                           text TEXT NOT NULL,
                           answer TEXT NOT NULL,
                           mastered BOOLEAN NOT NULL DEFAULT FALSE,
                           created_by BIGINT NOT NULL REFERENCES users(id),
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_questions_created_by ON questions(created_by);