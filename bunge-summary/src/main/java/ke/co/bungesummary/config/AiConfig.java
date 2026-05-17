package ke.co.bungesummary.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class AiConfig {

    @Bean
    @ConditionalOnExpression("!'${gemini.api-key:}'.isBlank()")
    public ChatLanguageModel geminiChatModel(
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.model}") String modelName) {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(0.1)
                .build();
    }

    @Bean
    @ConditionalOnExpression("!'${gemini.api-key:}'.isBlank()")
    public EmbeddingModel geminiEmbeddingModel(
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.embedding-model}") String embeddingModelName) {
        return GoogleAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName(embeddingModelName)
                .build();
    }

    @Bean
    @Profile("fallback")
    @ConditionalOnExpression("!'${openrouter.api-key:}'.isBlank()")
    public ChatLanguageModel openRouterChatModel(
            @Value("${openrouter.api-key}") String apiKey,
            @Value("${openrouter.base-url}") String baseUrl,
            @Value("${gemini.model}") String modelName) {
        return OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName("google/" + modelName)
                .temperature(0.1)
                .build();
    }
}
