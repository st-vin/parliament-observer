package ke.co.bungesummary.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import ke.co.bungesummary.api.dto.TrackTopicsRequest;
import ke.co.bungesummary.api.dto.TrackedTopicResponse;
import ke.co.bungesummary.api.mapper.ApiMapper;
import ke.co.bungesummary.domain.entity.Topic;
import ke.co.bungesummary.domain.entity.User;
import ke.co.bungesummary.domain.entity.UserTopicSubscription;
import ke.co.bungesummary.domain.entity.UserTopicSubscriptionId;
import ke.co.bungesummary.domain.repository.TopicRepository;
import ke.co.bungesummary.domain.repository.UserRepository;
import ke.co.bungesummary.domain.repository.UserTopicSubscriptionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Profile("api")
public class TrackingService {

    private final UserTopicSubscriptionRepository subscriptionRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    public TrackingService(
            UserTopicSubscriptionRepository subscriptionRepository,
            TopicRepository topicRepository,
            UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<TrackedTopicResponse> listSubscriptions(UUID userId) {
        return subscriptionRepository.findByUserId(userId).stream()
                .map(ApiMapper::toTracked)
                .toList();
    }

    @Transactional
    public List<TrackedTopicResponse> subscribe(UUID userId, TrackTopicsRequest request) {
        List<String> slugs = request.resolvedSlugs();
        if (slugs.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No topic slugs provided");
        }

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<TrackedTopicResponse> created = new ArrayList<>();
        for (String slug : slugs) {
            Topic topic =
                    topicRepository
                            .findBySlug(slug)
                            .orElseThrow(
                                    () ->
                                            new ResponseStatusException(
                                                    HttpStatus.NOT_FOUND, "Topic not found: " + slug));

            if (!subscriptionRepository.existsByUserIdAndTopicId(userId, topic.getId())) {
                UserTopicSubscription subscription = new UserTopicSubscription();
                UserTopicSubscriptionId id = new UserTopicSubscriptionId();
                id.setUserId(userId);
                id.setTopicId(topic.getId());
                subscription.setId(id);
                subscription.setUser(user);
                subscription.setTopic(topic);
                subscriptionRepository.save(subscription);
                created.add(ApiMapper.toTracked(subscription));
            }
        }
        return created;
    }

    @Transactional
    public void unsubscribe(UUID userId, String topicSlug) {
        if (!topicRepository.findBySlug(topicSlug).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found");
        }
        subscriptionRepository.deleteByUserIdAndTopic_Slug(userId, topicSlug);
    }
}
