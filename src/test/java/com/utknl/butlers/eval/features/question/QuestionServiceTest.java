package com.utknl.butlers.eval.features.question;

import com.utknl.butlers.eval.core.config.LlmProperties;
import com.utknl.butlers.eval.core.exception.LlmGenerationException;
import com.utknl.butlers.eval.core.factory.ChatClientFactory;
import com.utknl.butlers.eval.core.factory.LlmProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private ChatClientFactory clientFactory;

    @Mock
    private LlmProperties llmProperties;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private VectorStore vectorStore;

    @InjectMocks
    private QuestionService questionService;

    @ParameterizedTest(name = "Scenario: {0}")
    @MethodSource("provideInvalidCandidates")
    void givenInvalidCandidate_whenGenerateQuestionInvoked_thenThrowsAppropriateException(
            String name,
            QuestionCandidate candidate,
            Class<? extends Throwable> expectedException) {

        ChatClient deepChatClient = mock(ChatClient.class, Answers.RETURNS_DEEP_STUBS);
        when(clientFactory.getClient(any())).thenReturn(deepChatClient);
        when(llmProperties.generator()).thenReturn(LlmProvider.OLLAMA_LLAMA_3_2);

        when(deepChatClient.prompt()
                .system(any(Resource.class))
                .user(any(Consumer.class))
                .advisors((Advisor[]) any())
                .call()
                .entity(eq(QuestionCandidate.class)))
                .thenReturn(candidate);

        assertThrows(expectedException, () -> questionService.generateQuestion());
    }

    private static Stream<Arguments> provideInvalidCandidates() {
        return Stream.of(
                Arguments.of("Null Content",
                        new QuestionCandidate(null, "Category", 3),
                        LlmGenerationException.class),
                Arguments.of("Blank Content",
                        new QuestionCandidate("   ", "Category", 3),
                        LlmGenerationException.class),
                Arguments.of("Null Category",
                        new QuestionCandidate("Content", null, 3),
                        LlmGenerationException.class),
                Arguments.of("Complexity Zero",
                        new QuestionCandidate("Content", "Category", 0),
                        LlmGenerationException.class),
                Arguments.of("Complexity Above Max",
                        new QuestionCandidate("Content", "Category", 6),
                        LlmGenerationException.class)
        );
    }

    @Test
    void givenDeepMocks_whenGenerateQuestionInvoked_thenQuestionIsCreated() {
        QuestionCandidate expectedCandidate = new QuestionCandidate(
                "What is polymorphism in OOP?", "Programming", 3);

        ChatClient deepChatClient = mock(ChatClient.class, Answers.RETURNS_DEEP_STUBS);
        when(clientFactory.getClient(any())).thenReturn(deepChatClient);
        when(llmProperties.generator()).thenReturn(LlmProvider.OLLAMA_LLAMA_3_2);
        when(deepChatClient
                .prompt()
                .system(any(Resource.class))
                .user(any(Consumer.class))
                .advisors(any(Advisor.class))
                .call()
                .entity(eq(QuestionCandidate.class)))
                .thenReturn(expectedCandidate);

        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(Collections.emptyList());
        when(questionRepository.save(any(Question.class))).thenAnswer(inv -> inv.getArgument(0));

        Question result = questionService.generateQuestion();
        assertThat(result.getContent()).isEqualTo("What is polymorphism in OOP?");
        verify(questionRepository).save(any());
    }

    @Test
    void whenGetAllQuestions_thenReturnsAllQuestions() {
        List<Question> mockQuestions = List.of(
                Question.builder().id(UUID.randomUUID()).content("Q1").category("Cat1").build(),
                Question.builder().id(UUID.randomUUID()).content("Q2").category("Cat2").build()
        );
        when(questionRepository.findAll()).thenReturn(mockQuestions);

        List<Question> result = questionService.getAllQuestions();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getContent()).isEqualTo("Q1");
        assertThat(result.get(1).getContent()).isEqualTo("Q2");
        verify(questionRepository).findAll();
    }

    @Test
    void givenNoSimilarContent_whenIsSimilar_thenReturnsFalse() {
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(Collections.emptyList());

        boolean result = questionService.isSimilar("test content", 0.85);

        assertThat(result).isFalse();
        verify(vectorStore).similaritySearch(any(SearchRequest.class));
    }

    @Test
    void givenSimilarContentExists_whenIsSimilar_thenReturnsTrue() {
        List<Document> similarDocuments = List.of(new Document("id", "similar content", Map.of()));
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(similarDocuments);

        boolean result = questionService.isSimilar("test content", 0.85);

        assertThat(result).isTrue();
        verify(vectorStore).similaritySearch(any(SearchRequest.class));
    }

}
