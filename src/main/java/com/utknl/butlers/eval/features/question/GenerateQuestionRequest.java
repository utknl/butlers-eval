package com.utknl.butlers.eval.features.question;

import com.utknl.butlers.eval.core.factory.LlmProvider;

import groovyjarjarantlr4.v4.runtime.misc.Nullable;
import jakarta.validation.constraints.*;

public record GenerateQuestionRequest(@NotNull(message = "Category is required")
                                      Category category,

                                      @Min(1) @Max(5)
                                      Integer complexityLevel,

                                      @Nullable
                                      LlmProvider provider) {
}
