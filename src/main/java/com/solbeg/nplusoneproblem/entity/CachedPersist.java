package com.solbeg.nplusoneproblem.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "cached_persist")
@Getter
@Setter
@NoArgsConstructor
public class CachedPersist {

    @Id
    @SequenceGenerator(name = "seq_persist_cached_id_gen", sequenceName = "seq_persist_cached_id", allocationSize = 5000)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_persist_cached_id_gen")
    private Long id;

    @Column(name = "test_data")
    private String testData;
}
