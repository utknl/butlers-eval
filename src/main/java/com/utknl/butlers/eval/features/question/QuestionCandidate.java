package com.utknl.butlers.eval.features.question;

public record QuestionCandidate(String content,
                                Category category,
                                int complexityScore) {
}
