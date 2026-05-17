package ke.co.bungesummary.api.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import ke.co.bungesummary.api.dto.PagedResponse;
import ke.co.bungesummary.api.dto.ProceedingDetailResponse;
import ke.co.bungesummary.api.dto.ProceedingSummaryResponse;
import ke.co.bungesummary.api.mapper.ApiMapper;
import ke.co.bungesummary.domain.entity.Contribution;
import ke.co.bungesummary.domain.entity.Proceeding;
import ke.co.bungesummary.domain.entity.ProceedingTopic;
import ke.co.bungesummary.domain.repository.ContributionRepository;
import ke.co.bungesummary.domain.repository.ProceedingRepository;
import ke.co.bungesummary.domain.repository.ProceedingTopicRepository;
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
public class ProceedingService {

    private final ProceedingRepository proceedingRepository;
    private final ProceedingTopicRepository proceedingTopicRepository;
    private final ContributionRepository contributionRepository;

    public ProceedingService(
            ProceedingRepository proceedingRepository,
            ProceedingTopicRepository proceedingTopicRepository,
            ContributionRepository contributionRepository) {
        this.proceedingRepository = proceedingRepository;
        this.proceedingTopicRepository = proceedingTopicRepository;
        this.contributionRepository = contributionRepository;
    }

    public PagedResponse<ProceedingSummaryResponse> list(
            UUID sittingId,
            String topic,
            LocalDate dateFrom,
            LocalDate dateTo,
            UUID memberId,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<Proceeding> proceedings =
                proceedingRepository.findFiltered(
                        sittingId, topic, dateFrom, dateTo, memberId, pageable);
        return PagedResponse.from(proceedings.map(ApiMapper::toSummary));
    }

    public ProceedingDetailResponse getById(UUID id) {
        Proceeding proceeding =
                proceedingRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Proceeding not found"));
        proceeding.getSitting().getDate();
        List<ProceedingTopic> topics = proceedingTopicRepository.findByProceedingId(id);
        proceeding.setProceedingTopics(topics);
        List<Contribution> contributions =
                contributionRepository.findByProceedingIdOrderBySequenceInProceedingAsc(id);
        proceeding.setContributions(contributions);
        return ApiMapper.toDetail(proceeding);
    }
}
