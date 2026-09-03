# Prep Tracker

A backend REST API for tracking interview preparation, using a **spaced repetition algorithm** to schedule question reviews based on how confident you were in your last answer.

## Tech Stack

Java 21 · Spring Boot 4 · Spring Security (JWT) · PostgreSQL · Flyway · springdoc-openapi (Swagger UI) · Docker

## Features

- JWT authentication with per-user data isolation
- Spaced repetition scheduling - review intervals grow or shrink based on self-rated confidence (`BAD` / `OK` / `GOOD`)
- Question and attempt tracking with automatic next-review-date calculation
- Interactive API docs via Swagger UI

## API Overview

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Log in and receive a JWT |
| GET/POST | `/api/questions` | List / create questions |
| POST | `/api/attempts` | Record an attempt, schedule next review |
| GET | `/api/attempts/due` | Get questions due for review today |
