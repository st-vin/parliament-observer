package ke.co.bungesummary.ingestion.agent;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(EmbeddingModel.class)
public class EmbeddingTool {

    private final EmbeddingModel embeddingModel;

    public EmbeddingTool(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public float[] embed(String text) {
        Response<Embedding> response = embeddingModel.embed(text);
        return response.content().vector();
    }
}
