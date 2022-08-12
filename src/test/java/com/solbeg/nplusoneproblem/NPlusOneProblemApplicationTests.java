package com.solbeg.nplusoneproblem;

import com.solbeg.nplusoneproblem.dao.AdvertisementRepository;
import com.solbeg.nplusoneproblem.dao.CommentRepository;
import com.solbeg.nplusoneproblem.dao.TopicRepository;
import com.solbeg.nplusoneproblem.entity.Comment;
import com.solbeg.nplusoneproblem.entity.TargetedAdvertisement;
import com.solbeg.nplusoneproblem.entity.Topic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.IntStream;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.liquibase.enabled=true"
})
@Import(TestConfig.class)
class NPlusOneProblemApplicationTests {

    static final String SIZE_MESSAGE = "\nComments size: %d ads size: %d\n\n";
    static final int COMMENTS_COUNT = 5000;

    @Autowired
    TopicRepository topicRepository;
    @Autowired
    AdvertisementRepository advertisementRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    EntityManager entityManager;


    @Test
    @Transactional
    void shouldFetchAllTopics() {
        List<Topic> topics = topicRepository.findAll();
        for (Topic topic : topics) {
            List<Comment> comments = topic.getComments();
            List<TargetedAdvertisement> ads = topic.getAdvertisements();

            System.out.printf(SIZE_MESSAGE, comments.size(), ads.size());
        }
    }

    @Test
    void shouldPersistSingleTopic() {
        Topic topic = getTopic("Single topic");
        topicRepository.save(topic);
    }

    @Test
    void shouldPersistTwoSingleTopics() {
        Topic topic = getTopic("Single topic 1");
        topicRepository.save(topic);

        topic = getTopic("Single topic 2");
        topicRepository.save(topic);
    }

    @Test
    void shouldPersistTwoSingleAds() {
        String topicName = "Topic with ads";
        Topic topic = getTopic(topicName);
        Topic saved = topicRepository.save(topic);
        topic = topicRepository.findTopicByTopicName(topicName);

        TargetedAdvertisement ad1 = getAd(topic, 1);
        advertisementRepository.save(ad1);

        TargetedAdvertisement ad2 = getAd(topic, 2);
        advertisementRepository.save(ad2);
    }

    @Test
    void shouldPersistHugeAmountOfCommentsInTopic() {
        String topicName = "Big topic";
        Topic topic = getTopic(topicName);
        topic.setComments(getListOfComments(topic));

        topicRepository.save(topic);

        Topic found = topicRepository.findTopicByTopicName(topicName);
        System.out.println("Topic saved " + found.getId());
        System.out.println("Topic comments size " + found.getComments().size());
    }

    @Test
    @Transactional
    void shouldPersistAdsInTopic() {
        String topicName = "Ads topic";
        Topic topic = getTopic(topicName);
        List<TargetedAdvertisement> ads = List.of(getAd(topic, 1), getAd(topic, 2));
        topic.setAdvertisements(ads);

        topicRepository.save(topic);

        Topic found = topicRepository.findTopicByTopicName(topicName);
        System.out.println("Topic saved " + found.getId());
        System.out.println("Topic ads size " + found.getAdvertisements().size());
    }

    private  List<Comment> getListOfComments(Topic topic) {
        return IntStream.range(0, COMMENTS_COUNT)
                .mapToObj(i -> getIndexedComment(topic, i))
                .toList();
    }

    private Comment getIndexedComment(Topic topic, int i) {
        Comment comment = new Comment();
        comment.setCommentText("Comment text " + i);
        comment.setTopic(topic);
        return comment;
    }

    private TargetedAdvertisement getAd(Topic topic, int i) {
        TargetedAdvertisement ad = new TargetedAdvertisement();
        ad.setBannerUrl("Ad url " + i);
        ad.setTopic(topic);
        return ad;
    }

    private Topic getTopic(String topicName) {
        Topic topic = new Topic();
        topic.setTopicName(topicName);
        return topic;
    }
}
