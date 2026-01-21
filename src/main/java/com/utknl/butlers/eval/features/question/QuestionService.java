package com.utknl.butlers.eval.features.question;

import com.utknl.butlers.eval.core.config.LlmProperties;
import com.utknl.butlers.eval.core.exception.DuplicateQuestionException;
import com.utknl.butlers.eval.core.exception.LlmGenerationException;
import com.utknl.butlers.eval.core.exception.QuestionSaveException;
import com.utknl.butlers.eval.core.factory.ChatClientFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final VectorStore vectorStore;
    private final ChatClientFactory clientFactory;
    private final LlmProperties llmProperties;

    @Value("classpath:/prompts/generator-prompt.md")
    private Resource generatorPromptResource;
    @Value("classpath:/references/question-references.json")
    private Resource referenceResource;

    @Transactional
    public Question generateQuestion() {
        log.info("Generating question with: {}", llmProperties.generator().getModelId());

        QuestionCandidate candidate = clientFactory.getClient(llmProperties.generator())
                .prompt()
                .system(generatorPromptResource)
                .user(u -> u.text("""
                        Here are existing questions as a JSON array.
                        Use them only as reference for style and diversity.
                        Do not duplicate them.
                        
                        ReferenceQuestions:
                        {references}
                        """).param("references", referenceResource))
                .advisors(new SimpleLoggerAdvisor())
                .call()
                .entity(QuestionCandidate.class);

        validateCandidate(candidate);

        Question question = mapToQuestion(candidate);
        save(question);
        return question;
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public void save(Question question) {
        if (isSimilar(question.getContent(), llmProperties.similarityThreshold())) {
            throw new DuplicateQuestionException("Similar content already exists.");
        }

        try {
            questionRepository.save(question);
            log.debug("Saved question to database: {}", question.getId());

            saveToVectorStore(question);
            log.debug("Saved question to vector store: {}", question.getId());
        } catch (Exception e) {
            log.error("Failed to save question id={}", question.getId());
            throw new QuestionSaveException("Failed to save question: " + e.getMessage());
        }
    }

    public boolean isSimilar(String content, double threshold) {
        var results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(content)
                        .topK(1)
                        .similarityThreshold(threshold)
                        .build());
        return !results.isEmpty();
    }

    private void validateCandidate(QuestionCandidate candidate) {
        if (candidate == null) {
            throw new LlmGenerationException("LLM returned a null question candidate.");
        }
        if (candidate.content() == null || candidate.content().isBlank()) {
            throw new LlmGenerationException("LLM returned an empty or null question content.");
        }
        if (candidate.category() == null || candidate.category().isBlank()) {
            throw new LlmGenerationException("LLM returned an empty or null question category.");
        }
        if (candidate.complexityScore() < 1 || candidate.complexityScore() > 5) {
            throw new LlmGenerationException("LLM returned an out-of-range complexity score.");
        }
    }

    private Question mapToQuestion(QuestionCandidate candidate) {
        return Question.builder()
                .id(UUID.randomUUID())
                .model(llmProperties.generator().getModelId())
                .content(candidate.content())
                .category(candidate.category())
                .complexityScore(candidate.complexityScore())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private void saveToVectorStore(Question question) {
        vectorStore.add(List.of(new Document(
                question.getId().toString(),
                question.getContent(),
                Map.of("category", question.getCategory()))));
    }

}
