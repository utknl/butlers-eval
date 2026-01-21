package com.utknl.butlers.eval.core.config;

import com.utknl.butlers.eval.core.factory.LlmProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "butler.ai")
public record LlmProperties(LlmProvider generator,
                            LlmProvider judge,
                            double similarityThreshold) {
}
