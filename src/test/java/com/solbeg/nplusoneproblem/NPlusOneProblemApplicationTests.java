package com.solbeg.nplusoneproblem;

import com.solbeg.nplusoneproblem.dao.AdvertisementRepository;
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

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.liquibase.enabled=false"
})
@Import(TestConfig.class)
class NPlusOneProblemApplicationTests {

    static final String SIZE_MESSAGE = "\nComments size: %d ads size: %d\n\n";

    @Autowired
    TopicRepository topicRepository;
    @Autowired
    AdvertisementRepository advertisementRepository;


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
}
