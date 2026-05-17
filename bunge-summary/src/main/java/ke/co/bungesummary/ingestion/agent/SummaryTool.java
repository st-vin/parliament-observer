package ke.co.bungesummary.ingestion.agent;

import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(ChatLanguageModel.class)
public class SummaryTool {

    private final ChatLanguageModel chatModel;

    public SummaryTool(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
    }

    public SummaryResult summarize(String proceedingText) {
        String prompt =
                """
                Summarise this Kenya National Assembly proceeding for ordinary citizens.
                Plain English, secondary-school reading level, 150-300 words.
                Respond ONLY with JSON: {"plain_summary":"..."}

                Proceeding:
                %s
                """
                        .formatted(proceedingText);

        String response = chatModel.generate(prompt);
        JsonNode node = JsonResponseParser.readTree(response);
        String summary =
                node.has("plain_summary")
                        ? node.get("plain_summary").asText()
                        : node.asText();
        return new SummaryResult(summary);
    }
}
