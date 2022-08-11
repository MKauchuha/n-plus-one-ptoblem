package com.solbeg.nplusoneproblem.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "topic_name")
    private String topicName;

    @ToString.Exclude
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Comment> comments;

    @ToString.Exclude
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL/*, fetch = FetchType.EAGER*/) //uncomment and get org.hibernate.loader.MultipleBagFetchException exception
    private List<TargetedAdvertisement> advertisements;
}
