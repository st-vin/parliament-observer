package ke.co.bungesummary.ingestion.agent;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.langchain4j.model.chat.ChatLanguageModel;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(ChatLanguageModel.class)
public class ClassificationTool {

    private final ChatLanguageModel chatModel;

    public ClassificationTool(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
    }

    public List<TopicClassification> classify(String proceedingText, String topicTaxonomy) {
        String prompt =
                """
                Classify this parliamentary proceeding into 1-5 topics from the taxonomy below.
                Respond ONLY with a JSON array: [{"slug":"topic-slug","confidence":0.0-1.0}]
                Use only slugs from the taxonomy. No other text.

                Taxonomy:
                %s

                Proceeding:
                %s
                """
                        .formatted(topicTaxonomy, proceedingText);

        String response = chatModel.generate(prompt);
        return JsonResponseParser.readList(
                response, new TypeReference<List<TopicClassification>>() {});
    }
}
