package ke.co.bungesummary.api.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import ke.co.bungesummary.api.dto.PagedResponse;
import ke.co.bungesummary.api.dto.ProceedingSummaryResponse;
import ke.co.bungesummary.api.dto.TopicResponse;
import ke.co.bungesummary.api.mapper.ApiMapper;
import ke.co.bungesummary.domain.entity.Topic;
import ke.co.bungesummary.domain.repository.ProceedingRepository;
import ke.co.bungesummary.domain.repository.TopicRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Profile("api")
@Transactional(readOnly = true)
public class TopicService {

    private final TopicRepository topicRepository;
    private final ProceedingRepository proceedingRepository;

    public TopicService(TopicRepository topicRepository, ProceedingRepository proceedingRepository) {
        this.topicRepository = topicRepository;
        this.proceedingRepository = proceedingRepository;
    }

    public List<TopicResponse> listAll() {
        Map<UUID, Long> counts = loadProceedingCounts();
        return topicRepository.findAllByOrderByNameAsc().stream()
                .map(topic -> ApiMapper.toTopic(topic, counts.getOrDefault(topic.getId(), 0L)))
                .toList();
    }

    public PagedResponse<ProceedingSummaryResponse> proceedingsBySlug(String slug, int page, int size) {
        topicRepository
                .findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<ke.co.bungesummary.domain.entity.Proceeding> proceedings =
                proceedingRepository.findByTopicSlug(slug, pageable);
        return PagedResponse.from(proceedings.map(ApiMapper::toSummary));
    }

    private Map<UUID, Long> loadProceedingCounts() {
        Map<UUID, Long> counts = new HashMap<>();
        for (Object[] row : topicRepository.countProceedingsByTopic()) {
            counts.put((UUID) row[0], (Long) row[1]);
        }
        return counts;
    }
}
