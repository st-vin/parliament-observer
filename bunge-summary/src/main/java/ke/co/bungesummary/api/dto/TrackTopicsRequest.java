package ke.co.bungesummary.api.dto;

import java.util.ArrayList;
import java.util.List;

public record TrackTopicsRequest(List<String> topicSlugs, String topicSlug) {

    public List<String> resolvedSlugs() {
        List<String> slugs = new ArrayList<>();
        if (topicSlugs != null) {
            topicSlugs.stream().filter(s -> s != null && !s.isBlank()).forEach(slugs::add);
        }
        if (topicSlug != null && !topicSlug.isBlank()) {
            slugs.add(topicSlug.trim());
        }
        return slugs;
    }
}
