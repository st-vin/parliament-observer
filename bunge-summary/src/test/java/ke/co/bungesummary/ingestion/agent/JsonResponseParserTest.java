package ke.co.bungesummary.ingestion.agent;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import org.junit.jupiter.api.Test;

class JsonResponseParserTest {

    @Test
    void read_parsesJsonFromMarkdownFence() {
        String response =
                """
                ```json
                {"plain_summary":"Citizens learned about the Finance Bill."}
                ```
                """;

        SummaryResult result = JsonResponseParser.read(response, SummaryResult.class);
        assertThat(result.plainSummary()).contains("Finance Bill");
    }

    @Test
    void readList_parsesTopicArray() {
        String response = "[{\"slug\":\"agriculture\",\"confidence\":0.9}]";
        List<TopicClassification> topics =
                JsonResponseParser.readList(
                        response, new TypeReference<List<TopicClassification>>() {});

        assertThat(topics).hasSize(1);
        assertThat(topics.getFirst().slug()).isEqualTo("agriculture");
    }
}
