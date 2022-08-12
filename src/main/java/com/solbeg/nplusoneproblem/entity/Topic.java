package com.solbeg.nplusoneproblem.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "topic")
@Data
@NoArgsConstructor
public class Topic {

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_topic_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "topicIdGenerator")
//    @SequenceGenerator(name = "topicIdGenerator", sequenceName = "seq_topic_id", allocationSize = 1)
    private Long id;

    @Column(name = "topic_name")
    private String topicName;

    @ToString.Exclude
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    @BatchSize(size = 1000)
    private List<Comment> comments;

    @ToString.Exclude
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    @BatchSize(size = 1000)
    private List<TargetedAdvertisement> advertisements;
}
