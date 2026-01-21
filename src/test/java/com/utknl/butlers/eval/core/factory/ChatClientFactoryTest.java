package com.utknl.butlers.eval.core.factory;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class ChatClientFactoryTest {

    @Test
    void givenValidProvider_whenGetClient_thenReturnsConfiguredClient() {
        Map<String, ChatClient> clients = new HashMap<>();
        ChatClient mockBaseChatClient = mock(ChatClient.class, org.mockito.Answers.RETURNS_DEEP_STUBS);

        clients.put("ollama", mockBaseChatClient);
        ChatClientFactory factory = new ChatClientFactory(clients);

        ChatClient result = factory.getClient(LlmProvider.OLLAMA_LLAMA_3_2);

        assertThat(result).isNotNull();
    }

    @Test
    void givenInvalidProvider_whenGetClient_thenThrowsIllegalArgumentException() {
        Map<String, ChatClient> clients = new HashMap<>();
        ChatClientFactory factory = new ChatClientFactory(clients);

        assertThatThrownBy(() -> factory.getClient(LlmProvider.OLLAMA_LLAMA_3_2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No base client found for: ollama");
    }

    @Test
    void givenMultipleProviders_whenGetClient_thenReturnsCorrectClient() {
        Map<String, ChatClient> clients = new HashMap<>();
        ChatClient ollamaMock = mock(ChatClient.class, org.mockito.Answers.RETURNS_DEEP_STUBS);
        ChatClient openaiMock = mock(ChatClient.class, org.mockito.Answers.RETURNS_DEEP_STUBS);
        ChatClient anthropicMock = mock(ChatClient.class, org.mockito.Answers.RETURNS_DEEP_STUBS);

        clients.put("ollama", ollamaMock);
        clients.put("openai", openaiMock);
        clients.put("anthropic", anthropicMock);

        ChatClientFactory factory = new ChatClientFactory(clients);

        assertThat(factory.getClient(LlmProvider.OLLAMA_LLAMA_3_2)).isNotNull();
        assertThat(factory.getClient(LlmProvider.OPENAI_GPT_4O)).isNotNull();
        assertThat(factory.getClient(LlmProvider.ANTHROPIC_CLAUDE_SONNET)).isNotNull();
    }
}
