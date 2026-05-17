package ke.co.bungesummary.api.service;

import java.time.LocalDate;
import java.util.UUID;
import ke.co.bungesummary.api.dto.ContributionResponse;
import ke.co.bungesummary.api.dto.MemberResponse;
import ke.co.bungesummary.api.dto.PagedResponse;
import ke.co.bungesummary.api.mapper.ApiMapper;
import ke.co.bungesummary.domain.repository.MemberRepository;
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
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public PagedResponse<MemberResponse> list(
            String constituency, String party, String chamber, int page, int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        return PagedResponse.from(
                memberRepository
                        .findFiltered(constituency, party, chamber, pageable)
                        .map(ApiMapper::toMember));
    }

    public PagedResponse<ContributionResponse> contributions(
            UUID memberId,
            String topic,
            LocalDate dateFrom,
            LocalDate dateTo,
            int page,
            int size) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found");
        }
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        return PagedResponse.from(
                memberRepository
                        .findMemberContributions(memberId, topic, dateFrom, dateTo, pageable)
                        .map(ApiMapper::toContribution));
    }
}
