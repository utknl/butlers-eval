# butlers-eval

Spring Boot service that generates educational questions with LLMs and stores
them in Postgres + pgvector to avoid duplicates.

## What it does
- Generates a single question from a prompt and reference set.
- Persists questions in Postgres and embeddings in pgvector for similarity checks.
- Supports OpenAI, Anthropic, and Ollama via Spring AI.

## Requirements
- Java 21
- Docker (for Ollama and pgvector services)
- API keys if using OpenAI, Anthropic, or Google:
  - `OPENAI_API_KEY`
  - `ANTHROPIC_API_KEY`
  - `GOOGLE_API_KEY`

## Configuration
- Provider selection lives in `src/main/resources/application.yml` under
  `butler.ai.generator` and `butler.ai.judge`.

## Run
Option A (recommended, uses Spring Boot Docker Compose support):
```sh
./gradlew bootRun
```

Option B (start services manually, then run the app):
```sh
docker compose -f compose.yml up -d
./gradlew bootRun
```

## API
- `GET /generate` -> returns a newly generated question.
- `GET /questions` -> returns all stored questions.

## Local data
`ollama_data/` is a local runtime directory for Ollama models created by the
compose volume. It is safe to delete if you want to reset local models.
