package com.utknl.butlers.eval.core.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.AdvisorParams;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ChatClientFactory {

    private final Map<String, ChatClient> clients;

    public ChatClient getClient(LlmProvider provider) {
        ChatClient baseClient = clients.get(provider.getProviderName());

        if (baseClient == null) {
            throw new IllegalArgumentException("No base client found for: " + provider.getProviderName());
        }

        return baseClient.mutate().defaultOptions(ChatOptions.builder()
                        .model(provider.getModelId())
                        .maxTokens(2000)
                        .build())
                .defaultAdvisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
                .build();
    }

}
