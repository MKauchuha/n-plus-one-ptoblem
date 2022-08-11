package com.solbeg.nplusoneproblem.dao;

import com.solbeg.nplusoneproblem.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {


    //language=HQL
    String ALL_TOPICS_EAGER_QUERY = """
        select distinct t from Topic t
          join fetch t.comments
          join fetch t.advertisements
    """;

    @Query(ALL_TOPICS_EAGER_QUERY)
    List<Topic> findAllFetched();
}
