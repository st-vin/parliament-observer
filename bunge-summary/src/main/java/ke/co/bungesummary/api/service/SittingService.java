package ke.co.bungesummary.api.service;

import java.util.UUID;
import ke.co.bungesummary.api.dto.PagedResponse;
import ke.co.bungesummary.api.dto.SittingDetailResponse;
import ke.co.bungesummary.api.dto.SittingSummaryResponse;
import ke.co.bungesummary.api.mapper.ApiMapper;
import ke.co.bungesummary.domain.entity.Sitting;
import ke.co.bungesummary.domain.repository.ProceedingRepository;
import ke.co.bungesummary.domain.repository.SittingRepository;
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
public class SittingService {

    private final SittingRepository sittingRepository;
    private final ProceedingRepository proceedingRepository;

    public SittingService(SittingRepository sittingRepository, ProceedingRepository proceedingRepository) {
        this.sittingRepository = sittingRepository;
        this.proceedingRepository = proceedingRepository;
    }

    public PagedResponse<SittingSummaryResponse> list(Integer year, Integer month, int page, int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<Sitting> sittings = sittingRepository.findFiltered(year, month, pageable);
        return PagedResponse.from(
                sittings.map(s -> ApiMapper.toSummary(s, proceedingRepository.countBySittingId(s.getId()))));
    }

    public SittingDetailResponse getById(UUID id) {
        Sitting sitting =
                sittingRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sitting not found"));
        return ApiMapper.toDetail(sitting, proceedingRepository.findBySittingIdOrderBySequenceInSittingAsc(id));
    }
}
