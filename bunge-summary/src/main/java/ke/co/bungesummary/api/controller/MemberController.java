package ke.co.bungesummary.api.controller;

import java.time.LocalDate;
import java.util.UUID;
import ke.co.bungesummary.api.dto.ContributionResponse;
import ke.co.bungesummary.api.dto.MemberResponse;
import ke.co.bungesummary.api.dto.PagedResponse;
import ke.co.bungesummary.api.service.MemberService;
import org.springframework.context.annotation.Profile;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@Profile("api")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<PagedResponse<MemberResponse>> listMembers(
            @RequestParam(required = false) String constituency,
            @RequestParam(required = false) String party,
            @RequestParam(required = false) String chamber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(memberService.list(constituency, party, chamber, page, size));
    }

    @GetMapping("/{id}/contributions")
    public ResponseEntity<PagedResponse<ContributionResponse>> memberContributions(
            @PathVariable UUID id,
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                memberService.contributions(id, topic, dateFrom, dateTo, page, size));
    }
}
