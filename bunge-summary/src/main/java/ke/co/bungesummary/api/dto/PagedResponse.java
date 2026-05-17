package ke.co.bungesummary.api.dto;

import java.util.List;
import org.springframework.data.domain.Page;

public record PagedResponse<T>(List<T> content, PageMeta page) {

    public static <T> PagedResponse<T> from(Page<T> page) {
        return new PagedResponse<>(page.getContent(), meta(page));
    }

    public static <T> PagedResponse<T> from(Page<?> page, List<T> content) {
        return new PagedResponse<>(content, meta(page));
    }

    private static PageMeta meta(Page<?> page) {
        return new PageMeta(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
