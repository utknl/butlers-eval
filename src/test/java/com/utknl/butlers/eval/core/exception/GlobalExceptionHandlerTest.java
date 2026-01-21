package com.utknl.butlers.eval.core.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void givenDuplicateQuestionException_whenHandled_thenReturns409() {
        DuplicateQuestionException ex = new DuplicateQuestionException("Similar content already exists.");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDuplicate(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
        assertThat(response.getBody().message()).isEqualTo("Similar content already exists.");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    void givenLlmGenerationException_whenHandled_thenReturns500() {
        LlmGenerationException ex = new LlmGenerationException("LLM returned a null question candidate.");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleLlmGeneration(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("LLM returned a null question candidate.");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    void givenQuestionSaveException_whenHandled_thenReturns500() {
        QuestionSaveException ex = new QuestionSaveException("Failed to save question.");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleQuestionSave(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("Failed to save question.");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    void givenIllegalArgumentException_whenHandled_thenReturns400() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument provided.");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgument(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Invalid argument provided.");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

}
