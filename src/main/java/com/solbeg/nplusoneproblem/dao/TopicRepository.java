package com.solbeg.nplusoneproblem.dao;

import com.solbeg.nplusoneproblem.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    Topic findTopicByTopicName(String topicName);
}
