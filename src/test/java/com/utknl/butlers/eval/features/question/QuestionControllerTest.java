package com.utknl.butlers.eval.features.question;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utknl.butlers.eval.core.exception.DuplicateQuestionException;
import com.utknl.butlers.eval.core.exception.LlmGenerationException;
import com.utknl.butlers.eval.core.factory.LlmProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuestionController.class)
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuestionService questionService;

    @MockitoBean
    private QuestionMapper questionMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private GenerateQuestionRequest generateQuestionRequest() {
        return new GenerateQuestionRequest(Category.ARTIFICIAL_INTELLIGENCE, 2, LlmProvider.OLLAMA_LLAMA_3_2);
    }

    @Test
    void givenValidRequest_whenGenerate_thenReturns200WithQuestion() throws Exception {
        UUID questionId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Question question = Question.builder()
                .id(questionId)
                .content("What is polymorphism?")
                .category(Category.ARTIFICIAL_INTELLIGENCE)
                .complexityScore(3)
                .model("llama3.2:1b")
                .createdAt(now)
                .build();

        QuestionResponse response = new QuestionResponse(
                questionId,
                "llama3.2:1b",
                "What is polymorphism?",
                Category.ARTIFICIAL_INTELLIGENCE,
                3,
                now
        );

        when(questionService.generateQuestion(any())).thenReturn(question);
        when(questionMapper.toResponse(question)).thenReturn(response);

        mockMvc.perform(post("/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(generateQuestionRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("What is polymorphism?"))
                .andExpect(jsonPath("$.category").value(Category.ARTIFICIAL_INTELLIGENCE.name()))
                .andExpect(jsonPath("$.complexityScore").value(3))
                .andExpect(jsonPath("$.model").value("llama3.2:1b"));
    }

    @Test
    void givenDuplicateQuestion_whenGenerate_thenReturns409() throws Exception {
        when(questionService.generateQuestion(any()))
                .thenThrow(new DuplicateQuestionException("Similar content already exists."));

        mockMvc.perform(post("/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(generateQuestionRequest())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Similar content already exists."));
    }

    @Test
    void givenLlmFailure_whenGenerate_thenReturns500() throws Exception {
        when(questionService.generateQuestion(any()))
                .thenThrow(new LlmGenerationException("LLM returned a null question candidate."));

        mockMvc.perform(post("/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(generateQuestionRequest())))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("LLM returned a null question candidate."));
    }

    @Test
    void whenGetAllQuestions_thenReturns200WithList() throws Exception {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Question q1 = Question.builder()
                .id(id1)
                .content("Question 1")
                .category(Category.ARTIFICIAL_INTELLIGENCE)
                .complexityScore(2)
                .model("model1")
                .createdAt(now)
                .build();

        Question q2 = Question.builder()
                .id(id2)
                .content("Question 2")
                .category(Category.ARTIFICIAL_INTELLIGENCE)
                .complexityScore(4)
                .model("model2")
                .createdAt(now)
                .build();

        List<Question> questions = List.of(q1, q2);
        List<QuestionResponse> responses = List.of(
                new QuestionResponse(id1, "model1", "Question 1", Category.ARTIFICIAL_INTELLIGENCE, 2, now),
                new QuestionResponse(id2, "model2", "Question 2", Category.ARTIFICIAL_INTELLIGENCE, 4, now)
        );

        when(questionService.getAllQuestions()).thenReturn(questions);
        when(questionMapper.toResponseList(questions)).thenReturn(responses);

        mockMvc.perform(get("/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].content").value("Question 1"))
                .andExpect(jsonPath("$[1].content").value("Question 2"));
    }
}
