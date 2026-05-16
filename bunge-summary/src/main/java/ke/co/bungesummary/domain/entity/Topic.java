package ke.co.bungesummary.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "topics")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "topic")
    private List<ProceedingTopic> proceedingTopics = new ArrayList<>();

    @OneToMany(mappedBy = "topic")
    private List<UserTopicSubscription> subscriptions = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ProceedingTopic> getProceedingTopics() {
        return proceedingTopics;
    }

    public void setProceedingTopics(List<ProceedingTopic> proceedingTopics) {
        this.proceedingTopics = proceedingTopics;
    }

    public List<UserTopicSubscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<UserTopicSubscription> subscriptions) {
        this.subscriptions = subscriptions;
    }
}
