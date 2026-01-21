package com.utknl.butlers.eval.features.question;

import java.time.LocalDateTime;
import java.util.UUID;

public record QuestionResponse(UUID id,
                               String model,
                               String content,
                               String category,
                               int complexityScore,
                               LocalDateTime createdAt) {
}
