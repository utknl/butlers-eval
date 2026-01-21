package com.utknl.butlers.eval.core.factory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LlmProvider {

    // Ollama
    OLLAMA_LLAMA_3_2("ollama", "llama3.2:1b"),

    // OpenAI
    OPENAI_GPT_4O("openai", "gpt-4o"),
    OPENAI_GPT_4O_MINI("openai", "gpt-4o-mini"),

    // Anthropic
    ANTHROPIC_CLAUDE_SONNET("anthropic", "claude-sonnet-4-5-20250929"),
    ANTHROPIC_CLAUDE_HAIKU("anthropic", "claude-haiku-4-5-20251001");

    private final String providerName;
    private final String modelId;

}
