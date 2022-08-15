package com.solbeg.nplusoneproblem.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "comment")
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_comment_id")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commentIdGenerator")
//    @SequenceGenerator(name = "commentIdGenerator", sequenceName = "seq_comment_id", allocationSize = 1)
    @GenericGenerator(
            name = "commentIdGenerator",
            strategy = "enhanced-sequence",
            parameters = {
                    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "seq_comment_id"),
                    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1000"),
                    @Parameter(name = SequenceStyleGenerator.OPT_PARAM, value = "pooled-lo")
            }
    )
    private Long id;

    @Column(name = "comment_text", nullable = false)
    private String commentText;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    private Topic topic;
}
