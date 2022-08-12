package com.solbeg.nplusoneproblem.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "comment")
@Data
@NoArgsConstructor
public class Comment {

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_comment_id")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commentIdGenerator")
//    @SequenceGenerator(name = "commentIdGenerator", sequenceName = "seq_comment_id", allocationSize = 1)
    @GenericGenerator(name = "commentIdGenerator", strategy = "uuid2")
    private UUID id;

    @Column(name = "comment_text", nullable = false)
    private String commentText;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    private Topic topic;
}
