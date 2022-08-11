package com.solbeg.nplusoneproblem;

import com.solbeg.nplusoneproblem.dao.AdvertisementRepository;
import com.solbeg.nplusoneproblem.dao.TopicRepository;
import com.solbeg.nplusoneproblem.entity.Comment;
import com.solbeg.nplusoneproblem.entity.TargetedAdvertisement;
import com.solbeg.nplusoneproblem.entity.Topic;
import org.hibernate.annotations.QueryHints;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;

import static com.solbeg.nplusoneproblem.dao.TopicRepository.ALL_TOPICS_EAGER_QUERY;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.liquibase.enabled=true"
})
@Import(TestConfig.class)
class NPlusOneProblemApplicationTests {

    static final String SIZE_MESSAGE = "\nComments size: %d ads size: %d\n\n";

    @Autowired
    TopicRepository topicRepository;
    @Autowired
    AdvertisementRepository advertisementRepository;
    @Autowired
    EntityManager entityManager;


    @Test
    @Transactional
    void shouldFetchAllTopics() {
        List<Topic> topics = topicRepository.findAllFetched();
        for (Topic topic : topics) {
            Set<Comment> comments = topic.getComments();
            Set<TargetedAdvertisement> ads = topic.getAdvertisements();

            System.out.printf(SIZE_MESSAGE, comments.size(), ads.size());
        }
    }

    @Test
    @Transactional
    void shouldFetchAllTopicsUsingEntityManager() {
        List<Topic> topics = entityManager.createQuery(ALL_TOPICS_EAGER_QUERY, Topic.class)
                .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
                .getResultList();
        for (Topic topic : topics) {
            Set<Comment> comments = topic.getComments();
            Set<TargetedAdvertisement> ads = topic.getAdvertisements();

            System.out.printf(SIZE_MESSAGE, comments.size(), ads.size());
        }
    }
}
