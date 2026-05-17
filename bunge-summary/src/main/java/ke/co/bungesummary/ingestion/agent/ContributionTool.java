package ke.co.bungesummary.ingestion.agent;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.langchain4j.model.chat.ChatLanguageModel;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(ChatLanguageModel.class)
public class ContributionTool {

    private final ChatLanguageModel chatModel;

    public ContributionTool(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
    }

    public List<ContributionExtraction> extractContributions(String proceedingText) {
        String prompt =
                """
                Extract MP contributions from this proceeding.
                Respond ONLY with a JSON array:
                [{"member_name":"...","summary":"...","stance":"FOR|AGAINST|NEUTRAL|UNCERTAIN",
                "verbatim_excerpt":"...","stance_confidence":0.0-1.0}]
                Use UNCERTAIN when stance is ambiguous.

                Proceeding:
                %s
                """
                        .formatted(proceedingText);

        String response = chatModel.generate(prompt);
        return JsonResponseParser.readList(
                response, new TypeReference<List<ContributionExtraction>>() {});
    }
}
