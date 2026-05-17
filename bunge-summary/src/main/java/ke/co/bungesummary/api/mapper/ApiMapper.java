package ke.co.bungesummary.api.mapper;

import java.util.Comparator;
import java.util.List;
import ke.co.bungesummary.api.dto.ContributionResponse;
import ke.co.bungesummary.api.dto.MemberResponse;
import ke.co.bungesummary.api.dto.ProceedingDetailResponse;
import ke.co.bungesummary.api.dto.ProceedingSummaryResponse;
import ke.co.bungesummary.api.dto.SittingDetailResponse;
import ke.co.bungesummary.api.dto.SittingProceedingSummary;
import ke.co.bungesummary.api.dto.SittingSummaryResponse;
import ke.co.bungesummary.api.dto.TopicTagResponse;
import ke.co.bungesummary.api.dto.TopicResponse;
import ke.co.bungesummary.api.dto.TrackedTopicResponse;
import ke.co.bungesummary.domain.entity.Contribution;
import ke.co.bungesummary.domain.entity.Member;
import ke.co.bungesummary.domain.entity.Proceeding;
import ke.co.bungesummary.domain.entity.ProceedingTopic;
import ke.co.bungesummary.domain.entity.Sitting;
import ke.co.bungesummary.domain.entity.Topic;
import ke.co.bungesummary.domain.entity.UserTopicSubscription;

public final class ApiMapper {

    private ApiMapper() {}

    public static SittingSummaryResponse toSummary(Sitting sitting, long proceedingCount) {
        return new SittingSummaryResponse(
                sitting.getId(),
                sitting.getDate(),
                sitting.getChamber(),
                proceedingCount,
                sitting.getIngestionStatus());
    }

    public static SittingDetailResponse toDetail(Sitting sitting, List<Proceeding> proceedings) {
        List<SittingProceedingSummary> items =
                proceedings.stream()
                        .sorted(
                                Comparator.comparing(
                                        Proceeding::getSequenceInSitting,
                                        Comparator.nullsLast(Comparator.naturalOrder())))
                        .map(
                                p ->
                                        new SittingProceedingSummary(
                                                p.getId(),
                                                p.getTitle(),
                                                p.getProceedingType(),
                                                p.getOutcome(),
                                                p.getSequenceInSitting()))
                        .toList();
        return new SittingDetailResponse(
                sitting.getId(),
                sitting.getDate(),
                sitting.getChamber(),
                sitting.getIngestionStatus(),
                items);
    }

    public static ProceedingSummaryResponse toSummary(Proceeding proceeding) {
        return new ProceedingSummaryResponse(
                proceeding.getId(),
                proceeding.getTitle(),
                proceeding.getSitting().getDate(),
                proceeding.getProceedingType(),
                proceeding.getOutcome(),
                proceeding.getPlainSummary(),
                toTopicTags(proceeding.getProceedingTopics()));
    }

    public static ProceedingDetailResponse toDetail(Proceeding proceeding) {
        List<ContributionResponse> contributions =
                proceeding.getContributions().stream()
                        .sorted(
                                Comparator.comparing(
                                        Contribution::getSequenceInProceeding,
                                        Comparator.nullsLast(Comparator.naturalOrder())))
                        .map(ApiMapper::toContribution)
                        .toList();
        return new ProceedingDetailResponse(
                proceeding.getId(),
                proceeding.getSitting().getId(),
                proceeding.getSitting().getDate(),
                proceeding.getTitle(),
                proceeding.getProceedingType(),
                proceeding.getStage(),
                proceeding.getOutcome(),
                proceeding.getPlainSummary(),
                proceeding.getRawText(),
                proceeding.getSourcePageStart(),
                proceeding.getSourcePageEnd(),
                toTopicTags(proceeding.getProceedingTopics()),
                contributions);
    }

    public static TopicResponse toTopic(Topic topic, long proceedingCount) {
        return new TopicResponse(
                topic.getId(), topic.getName(), topic.getSlug(), topic.getDescription(), proceedingCount);
    }

    public static MemberResponse toMember(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getFullName(),
                member.getDisplayName(),
                member.getConstituency(),
                member.getCounty(),
                member.getParty(),
                member.getChamber(),
                member.isActive());
    }

    public static ContributionResponse toContribution(Contribution contribution) {
        Member member = contribution.getMember();
        return new ContributionResponse(
                contribution.getId(),
                member.getId(),
                member.getDisplayName() != null ? member.getDisplayName() : member.getFullName(),
                contribution.getSummary(),
                contribution.getStance(),
                contribution.getVerbatimExcerpt(),
                contribution.getExcerptPage(),
                contribution.getSequenceInProceeding());
    }

    public static TrackedTopicResponse toTracked(UserTopicSubscription subscription) {
        Topic topic = subscription.getTopic();
        return new TrackedTopicResponse(topic.getName(), topic.getSlug(), subscription.getSubscribedAt());
    }

    private static List<TopicTagResponse> toTopicTags(List<ProceedingTopic> proceedingTopics) {
        return proceedingTopics.stream()
                .map(
                        pt ->
                                new TopicTagResponse(
                                        pt.getTopic().getName(),
                                        pt.getTopic().getSlug(),
                                        pt.getConfidenceScore()))
                .toList();
    }
}
