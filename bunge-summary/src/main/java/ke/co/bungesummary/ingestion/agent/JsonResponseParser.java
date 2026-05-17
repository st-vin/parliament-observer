package ke.co.bungesummary.ingestion.agent;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class JsonResponseParser {

    private static final Pattern JSON_BLOCK =
            Pattern.compile("```(?:json)?\\s*([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonResponseParser() {}

    static String extractJson(String response) {
        if (response == null) {
            return "{}";
        }
        Matcher matcher = JSON_BLOCK.matcher(response.trim());
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        String trimmed = response.trim();
        int start = trimmed.indexOf('{');
        int arrayStart = trimmed.indexOf('[');
        if (arrayStart >= 0 && (start < 0 || arrayStart < start)) {
            int end = trimmed.lastIndexOf(']');
            if (end > arrayStart) {
                return trimmed.substring(arrayStart, end + 1);
            }
        }
        if (start >= 0) {
            int end = trimmed.lastIndexOf('}');
            if (end > start) {
                return trimmed.substring(start, end + 1);
            }
        }
        return trimmed;
    }

    static <T> T read(String response, Class<T> type) {
        try {
            return MAPPER.readValue(extractJson(response), type);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse AI JSON response", e);
        }
    }

    static <T> T readList(String response, TypeReference<T> type) {
        try {
            return MAPPER.readValue(extractJson(response), type);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse AI JSON array response", e);
        }
    }

    static JsonNode readTree(String response) {
        try {
            return MAPPER.readTree(extractJson(response));
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse AI JSON response", e);
        }
    }
}
